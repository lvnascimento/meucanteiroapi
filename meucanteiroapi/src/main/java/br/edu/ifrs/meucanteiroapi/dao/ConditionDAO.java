package br.edu.ifrs.meucanteiroapi.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.QuerySolution;

import br.edu.ifrs.meucanteiroapi.model.management.Condition;
import br.edu.ifrs.meucanteiroapi.util.RDFUtil;

/**
 * DAO (Data Access Object) responsável por consultar, no triplestore RDF
 * (Apache Jena Fuseki), as condições associadas a um manejo.
 * <p>
 * Cada condição possui um rótulo (obrigatório) e uma descrição (opcional).
 */
public class ConditionDAO {

	/**
	 * Consulta SPARQL de condições em manejos.
	 */
	private static final String QUERY_TEMPLATE_MNGMT = """
			PREFIX dc:    <http://purl.org/dc/elements/1.1/>
			PREFIX conto: <%s>
			PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>

			SELECT DISTINCT ?mngmt ?c ?label ?description
			WHERE {
			  VALUES ?mngmt { %s }
			  # Condições do manejo informado e seus rótulos
			  ?mngmt conto:hasCondition ?c .
			  ?c rdfs:label ?label .
			  FILTER(LANG(?label) IN ("pt-BR", ""))

			  # Descrição da condição (opcional)
			  OPTIONAL {
			    ?c dc:description ?description .
			    FILTER(LANG(?description) IN ("pt-BR", ""))
			  }
			}
			ORDER BY ?label
			""";
	
	/**
	 * Consulta SPARQL de condições em colheitas.
	 */
	private static final String QUERY_TEMPLATE_HARV = """
			PREFIX dc:    <http://purl.org/dc/elements/1.1/>
			PREFIX conto: <%s>
			PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>

			SELECT DISTINCT ?harv ?c ?label ?description
			WHERE {
			  VALUES ?harv { %s }
			  # Condições da colheita informada e seus rótulos
			  ?harv conto:hasCondition ?c .
			  ?c rdfs:label ?label .
			  FILTER(LANG(?label) IN ("pt-BR", ""))

			  # Descrição da condição (opcional)
			  OPTIONAL {
			    ?c dc:description ?description .
			    FILTER(LANG(?description) IN ("pt-BR", ""))
			  }
			}
			ORDER BY ?label
			""";

	/**
	 * Busca as condições associadas a um conjunto de manejos no triplestore.
	 *
	 * @param managementUris um conjunto de URIs absolutas (como {@code String}) dos recursos RDF que
	 *                 representam os manejos a serem consultados
	 * @return um map associando cada manejo a uma lista de condições, ordenada pelo rótulo; lista vazia
	 *         caso nenhuma seja encontrada
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public Map<String, List<Condition>> getConditionsByManagements(Set<String> managementUris) {
		if (managementUris.isEmpty()) return Map.of();

		List<QuerySolution> rows = DAO.query(QUERY_TEMPLATE_MNGMT, managementUris);
		
		Map<String, List<Condition>> conditionsMap = new HashMap<>();
		
		for (QuerySolution soln : rows) {
			String mngmtUri = soln.getResource("mngmt").getURI();
			
			Condition c = new Condition();
			c.setLabel(RDFUtil.getStringLiteral(soln.getLiteral("label")));
			c.setDescription(RDFUtil.getStringLiteral(soln.getLiteral("description")));
			
			conditionsMap.computeIfAbsent(mngmtUri, k -> new ArrayList<>()).add(c);
		}
		
		return conditionsMap;
	}

	/**
	 * Busca as condições associadas a um conjunto de colheitas no triplestore.
	 *
	 * @param harvestUris um conjunto de URIs absolutas (como {@code String}) dos recursos RDF que
	 *                 representam as informações de colheita a serem consultadas
	 * @return um map associando cada colheita a uma lista de condições, ordenada pelo rótulo; lista vazia
	 *         caso nenhuma seja encontrada
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public Map<String, List<Condition>> getConditionsByHarvest(Set<String> harvestUris) {
		if (harvestUris.isEmpty()) return Map.of();

		List<QuerySolution> rows = DAO.query(QUERY_TEMPLATE_HARV, harvestUris);
		
		Map<String, List<Condition>> conditionsMap = new HashMap<>();
		
		for (QuerySolution soln : rows) {
			String harvUri = soln.getResource("harv").getURI();
			
			Condition c = new Condition();
			c.setLabel(RDFUtil.getStringLiteral(soln.getLiteral("label")));
			c.setDescription(RDFUtil.getStringLiteral(soln.getLiteral("description")));
			
			conditionsMap.computeIfAbsent(harvUri, k -> new ArrayList<>()).add(c);
		}
		
		return conditionsMap;
	}
}