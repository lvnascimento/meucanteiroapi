package br.edu.ifrs.meucanteiroapi.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.QuerySolution;

import br.edu.ifrs.meucanteiroapi.model.management.Condition;
import br.edu.ifrs.meucanteiroapi.model.management.Management;
import br.edu.ifrs.meucanteiroapi.model.management.ManagementPractice;
import br.edu.ifrs.meucanteiroapi.util.RDFUtil;

/**
 * DAO responsável por buscar, na base de conhecimento (Apache Jena Fuseki),
 * os manejos associados a uma cultivar.
 *
 * Cada manejo possui uma janela de aplicação (início/fim relativos a um
 * estágio fenológico, ou o ciclo inteiro), além de práticas e condições,
 * que são carregadas por DAOs auxiliares.
 */
public class ManagementDAO {

	/**
	 * Consulta SPARQL.
	 */
	private static final String QUERY_TEMPLATE = """
			PREFIX conto: <%s>
			PREFIX time:  <http://www.w3.org/2006/time#>
			PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>

			SELECT DISTINCT ?cultivar ?m ?stageLabel ?startDays ?endDays ?wholeCycle
			WHERE {
			  VALUES ?cultivar { %s }
			  ?cultivar conto:hasManagement ?m .
			  ?m conto:hasApplicationWindow ?w .

			  # Estágio fenológico ao qual a janela é relativa (opcional)
			  OPTIONAL {
			    ?w conto:relativeToStage ?stage .
			    ?stage conto:stageOrder ?ord ;
			           rdfs:label ?stageLabel .
			    FILTER(LANG(?stageLabel) IN ("pt-BR", ""))
			  }

			  # Indica que o manejo vale durante todo o ciclo (opcional)
			  OPTIONAL { ?w conto:appliesThroughoutCycle ?cycle . }

			  # Deslocamento inicial da janela, convertido para dias
			  OPTIONAL {
			    ?w conto:startOffset ?s .
			    ?s time:numericDuration ?sn ;
			       time:unitType ?su .
			    VALUES (?su ?sf) { (time:unitDay 1) (time:unitWeek 7) (time:unitMonth 30) }
			  }

			  # Deslocamento final da janela, convertido para dias
			  OPTIONAL {
			    ?w conto:endOffset ?e .
			    ?e time:numericDuration ?en ;
			       time:unitType ?eu .
			    VALUES (?eu ?ef) { (time:unitDay 1) (time:unitWeek 7) (time:unitMonth 30) }
			  }

			  # Valores padrão quando os dados opcionais não existem
			  BIND(COALESCE(?cycle, false) AS ?wholeCycle)
			  BIND(COALESCE(?sn * ?sf, 0) AS ?startDays)
			  BIND(COALESCE(?en * ?ef, ?startDays) AS ?endDays)

			  # Chave de ordenação: ciclo inteiro primeiro (-1), depois pela
			  # ordem do estágio; manejos sem estágio vão para o fim (999)
			  BIND(IF(?wholeCycle, -1, COALESCE(?ord, 999)) AS ?sortStage)
			}
			ORDER BY ?sortStage ?startDays ?endDays
			""";

	// DAOs auxiliares injetados pelo construtor (facilita testes com mocks)
	private final ManagementPracticeDAO practiceDAO;
	private final ConditionDAO conditionDAO;

	/** Construtor padrão: usa as implementações reais dos DAOs auxiliares. */
	public ManagementDAO() {
		this(new ManagementPracticeDAO(), new ConditionDAO());
	}

	/** Construtor para injeção de dependências (útil em testes). */
	public ManagementDAO(ManagementPracticeDAO practiceDAO, ConditionDAO conditionDAO) {
		this.practiceDAO = practiceDAO;
		this.conditionDAO = conditionDAO;
	}

	/**
	 * Retorna os manejos de um conjunto de cultivares, já ordenados (ciclo inteiro,
	 * depois por estágio e período).
	 *
	 * @param cultivarUris URIs absolutas dos recursos das cultivares na ontologia
	 * @return map contendo listas de manejos (vazia se não houver nenhum) organizadas por cultivar
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public Map<String, List<Management>> getManagementsByCultivars(Set<String> cultivarUris) {
		if (cultivarUris.isEmpty()) return Map.of();

		List<QuerySolution> rows = DAO.query(QUERY_TEMPLATE, cultivarUris);
		
		Set<String> managementUris = new HashSet<>();
	    for (QuerySolution soln : rows) {
	        managementUris.add(soln.getResource("m").getURI());
	    }
	    
	    Map<String, List<ManagementPractice>> practicesMap = practiceDAO.getMngtPracticesByManagements(managementUris);
	    Map<String, List<Condition>> conditionsMap = conditionDAO.getConditionsByManagements(managementUris);
	    
		Map<String, List<Management>> managementMap = new HashMap<>();

		for (QuerySolution soln : rows) {
			String cultUri = soln.getResource("cultivar").getURI();
			String resource = soln.getResource("m").getURI();

			Management m = new Management();

			m.setStageLabel(RDFUtil.getStringLiteral(soln.getLiteral("stageLabel")));
			m.setStartDays(RDFUtil.getIntLiteral(soln.getLiteral("startDays")));
			m.setEndDays(RDFUtil.getIntLiteral(soln.getLiteral("endDays")));
			m.setWholeCycle(RDFUtil.getBooleanLiteral(soln.getLiteral("wholeCycle")));

			m.setPractices(practicesMap.get(resource));
			m.setConditions(conditionsMap.get(resource));

			managementMap.computeIfAbsent(cultUri, k -> new ArrayList<>()).add(m);
		}

		return managementMap;
	}
}