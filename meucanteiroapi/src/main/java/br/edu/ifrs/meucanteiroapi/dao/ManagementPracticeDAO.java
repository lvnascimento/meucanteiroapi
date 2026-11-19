package br.edu.ifrs.meucanteiroapi.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;

import br.edu.ifrs.meucanteiroapi.Constants;
import br.edu.ifrs.meucanteiroapi.model.management.FertilizationPractice;
import br.edu.ifrs.meucanteiroapi.model.management.ManagementPractice;
import br.edu.ifrs.meucanteiroapi.util.RDFUtil;

/**
 * DAO responsável por recuperar do triplestore (Fuseki) as práticas de
 * manejo ({@link ManagementPractice}) associadas a manejos ou a outras
 * práticas em lote.
 */
public class ManagementPracticeDAO {

	/** Tipo RDF (AGRO) que identifica uma prática de fertilização. */
	private static final String FERTILIZATION_TYPE =
			"http://purl.obolibrary.org/obo/AGRO_01000000";

	/** Propriedade que liga um manejo às suas práticas. */
	private static final String HAS_PRACTICE = "conto:hasManagementPractice";

	/** Propriedade que liga uma prática às suas alternativas. */
	private static final String HAS_ALTERNATIVE = "conto:hasAlternative";

	private static final String PREFIXES = """
			PREFIX conto: <%s>
			PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX dc: <http://purl.org/dc/terms/>
			""".formatted(Constants.CONTO_NS);

	private final AlternativeItemsDAO alternativeItemsDAO = new AlternativeItemsDAO();

	/**
	 * Busca as práticas de manejo para MÚLTIPLOS manejos em uma única consulta SPARQL (Lote).
	 *
	 * @param managementUris Conjunto com URIs dos recursos de manejo
	 * @return Mapa associando a URI de cada manejo à sua lista de práticas de manejo
	 */
	public Map<String, List<ManagementPractice>> getMngtPracticesByManagements(Set<String> managementUris) {
		if (managementUris == null || managementUris.isEmpty()) {
			return Map.of();
		}
		return findPracticesInBatch(HAS_PRACTICE, managementUris, Set.of());
	}

	/**
	 * Mantido para compatibilidade com chamadas individuais de um único manejo.
	 */
	public List<ManagementPractice> getMngtPracticesByManagement(String managementUri) {
		if (managementUri == null || managementUri.isBlank()) {
			return List.of();
		}
		Map<String, List<ManagementPractice>> map = getMngtPracticesByManagements(Set.of(managementUri));
		return map.getOrDefault(managementUri, List.of());
	}

	/** Dados brutos de uma linha de prática lida do SPARQL. */
	private record PracticeRow(String subjectUri, String practiceUri, String type, String label, String description) {}

	/**
	 * Executa a consulta em lote injetando as URIs do sujeito na cláusula VALUES.
	 */
	private Map<String, List<ManagementPractice>> findPracticesInBatch(
			String property, Set<String> subjectUris, Set<String> ancestors) {

		List<PracticeRow> rows = queryRowsBatch(property, subjectUris);
		Map<String, List<ManagementPractice>> resultMap = new HashMap<>();

		// Mapeia práticas únicas encontradas para posterior carregamento de alternativas/itens
		Map<String, ManagementPractice> practiceInstances = new HashMap<>();
		Set<String> fertilizationPracticeUris = new HashSet<>();
		
		// Estrutura para relacionar Sujeito (Manejo/Prática) -> Lista de URIs de Práticas Filhas
		Map<String, List<String>> subjectToPracticeUris = new HashMap<>();

		for (PracticeRow row : rows) {
			if (ancestors.contains(row.practiceUri())) {
				continue; // Evita ciclos recursivos entre alternativas
			}

			// Instancia a prática se ainda não tiver sido criada
			practiceInstances.computeIfAbsent(row.practiceUri(), uri -> {
				ManagementPractice p = FERTILIZATION_TYPE.equals(row.type())
						? new FertilizationPractice()
						: new ManagementPractice();
				p.setLabel(row.label());
				p.setDescription(row.description());
				return p;
			});

			if (FERTILIZATION_TYPE.equals(row.type())) {
				fertilizationPracticeUris.add(row.practiceUri());
			}

			subjectToPracticeUris
					.computeIfAbsent(row.subjectUri(), k -> new ArrayList<>())
					.add(row.practiceUri());
		}

		// 1. Carrega alternativas em lote para todas as práticas encontradas nesta etapa
		Set<String> currentPracticeUris = practiceInstances.keySet();
		Map<String, List<ManagementPractice>> alternativesMap = Map.of();

		if (!currentPracticeUris.isEmpty()) {
			Set<String> newAncestors = new LinkedHashSet<>(ancestors);
			newAncestors.addAll(currentPracticeUris);
			alternativesMap = findPracticesInBatch(HAS_ALTERNATIVE, currentPracticeUris, newAncestors);
		}

		// 2. Preenche os dados montados e associa com cada sujeito
		for (Map.Entry<String, List<String>> entry : subjectToPracticeUris.entrySet()) {
			String subjectUri = entry.getKey();
			List<ManagementPractice> practiceList = new ArrayList<>();

			for (String pUri : entry.getValue()) {
				ManagementPractice practice = practiceInstances.get(pUri);

				// Atribui alternativas carregadas em lote
				practice.setAlternatives(alternativesMap.getOrDefault(pUri, List.of()));

				// Atribui itens de fertilização se aplicável
				if (practice instanceof FertilizationPractice fertilization) {
					// Assumindo que o AlternativeItemsDAO também ofereça/suporte chamadas em lote
					fertilization.setItems(alternativeItemsDAO.getAlternativesByPractice(pUri));
				}

				practiceList.add(practice);
			}
			resultMap.put(subjectUri, practiceList);
		}

		return resultMap;
	}

	/**
	 * Executa a consulta SPARQL usando VALUES para recuperar dados de múltiplos sujeitos.
	 */
	private List<PracticeRow> queryRowsBatch(String property, Set<String> subjectUris) {
		String queryStr = PREFIXES + """
				SELECT DISTINCT ?pSubject ?mPract ?mPractType ?mPractLabel ?mPractDescr
				WHERE {
				  VALUES ?pSubject { %s }
				  ?pSubject %s ?mPract .
				  ?mPract a ?mPractType .
				  ?mPract rdfs:label ?mPractLabel .
				  ?mPract dc:description ?mPractDescr .
				}
				""".formatted(DAO.stringifyURISet(subjectUris), property);

		ParameterizedSparqlString sparql = new ParameterizedSparqlString(queryStr);
		
		List<QuerySolution> result = DAO.runSelect(sparql.asQuery(), queryStr);
		
		List<PracticeRow> rows = new ArrayList<>();

		for(QuerySolution soln : result) {
			rows.add(new PracticeRow(
					soln.getResource("pSubject").getURI(),
					soln.getResource("mPract").getURI(),
					soln.getResource("mPractType").getURI(),
					RDFUtil.getStringLiteral(soln.getLiteral("mPractLabel")),
					RDFUtil.getStringLiteral(soln.getLiteral("mPractDescr"))));
		}
		return rows;
	}
}