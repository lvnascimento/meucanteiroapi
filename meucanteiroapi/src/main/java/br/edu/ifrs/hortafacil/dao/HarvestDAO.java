package br.edu.ifrs.hortafacil.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import org.apache.jena.query.QuerySolution;

import br.edu.ifrs.hortafacil.Constants;
import br.edu.ifrs.hortafacil.model.harvest.Harvest;
import br.edu.ifrs.hortafacil.model.management.Condition;
import br.edu.ifrs.hortafacil.util.RDFUtil;

/**
 * DAO (Data Access Object) responsável por buscar informações de colheita
 * na base de conhecimento RDF (ontologia CONTO) por meio de consultas SPARQL.
 * As colheitas são recuperadas a partir de cultivares e as condições
 * associadas a cada colheita são obtidas por meio do {@link ConditionDAO}.
 */
public class HarvestDAO {

	/*
	 * Template da consulta SPARQL. Os dois placeholders %s são preenchidos,
	 * respectivamente, com o namespace da ontologia CONTO e com a lista de URIs
	 * das cultivares (cláusula VALUES).
	 *
	 * Para cada cultivar, a consulta navega: cultivar -> produto -> colheita,
	 * e traz, de forma opcional, a descrição (em pt-BR ou sem idioma definido) e
	 * os tempos até a colheita no verão e no inverno (valor numérico + unidade).
	 */
	private static final String QUERY_TEMPLATE = """
			PREFIX conto: <%s>
			PREFIX dc:  <http://purl.org/dc/terms/>
			PREFIX qudt: <http://purl.org/qudt/schema/qudt/>

			SELECT DISTINCT ?cultivar ?h ?description ?thsvalue ?thsunit ?thwvalue ?thwunit
			WHERE {
			  VALUES ?cultivar { %s }
			  ?cultivar conto:providesProduct ?prod .
			  ?prod conto:hasHarvest ?h .


			  OPTIONAL {
			    ?h dc:description ?description .
			    FILTER(LANG(?description) IN ("pt-BR", ""))
			  }

			  OPTIONAL {
			  	?h conto:timeToHarvest_Summer ?thsquant .
			  	?thsquant qudt:quantityValue ?thsval .
			  	?thsval qudt:hasUnit ?thsunit .
			  	?thsval qudt:numericValue ?thsvalue .
			  }

			  OPTIONAL {
			  	?h conto:timeToHarvest_Winter ?thwquant .
			  	?thwquant qudt:quantityValue ?thwval .
			  	?thwval qudt:hasUnit ?thwunit .
			  	?thwval qudt:numericValue ?thwvalue .
			  }
			}
			""";

	// DAOs auxiliares injetados pelo construtor (facilita testes com mocks)
	private final ConditionDAO conditionDAO;

	/** Construtor padrão: usa as implementações reais dos DAOs auxiliares. */
	public HarvestDAO() {
		this(new ConditionDAO());
	}

	/** Construtor para injeção de dependências (útil em testes). */
	public HarvestDAO(ConditionDAO conditionDAO) {
		super();
		this.conditionDAO = conditionDAO;
	}

	/**
	 * Busca as colheitas associadas a um conjunto de cultivares.
	 * Executa uma única consulta SPARQL para todas as cultivares e uma única
	 * consulta adicional para carregar as condições de todas as colheitas
	 * encontradas, evitando uma consulta por cultivar (problema "N+1").
	 *
	 * @param cultivarUris conjunto de URIs das cultivares a serem consultadas
	 * @return mapa em que a chave é a URI da cultivar e o valor é a lista de
	 *         colheitas dessa cultivar; retorna um mapa vazio se o conjunto
	 *         de cultivares for vazio
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<Harvest>> getHarvestsByCultivars(Set<String> cultivarUris) {
		// Evita executar a consulta quando não há cultivares
		if (cultivarUris.isEmpty()) return Map.of();

		// Executa a consulta SPARQL e obtém todas as linhas de resultado
		List<QuerySolution> rows = DAO.query(QUERY_TEMPLATE, cultivarUris);

		// Coleta as URIs únicas das colheitas encontradas (um Set evita duplicatas)
		Set<String> harvestUris = new HashSet<>();
	    for (QuerySolution soln : rows) {
	        harvestUris.add(soln.getResource("h").getURI());
	    }

	    // Carrega, de uma só vez, as condições de todas as colheitas (chave: URI da colheita)
	    Map<String, List<Condition>> conditionsMap = conditionDAO.getConditionsByHarvest(harvestUris);

		// Mapa de saída: URI da cultivar -> lista de colheitas
		Map<String, List<Harvest>> harvestMap = new HashMap<>();

		// Converte cada linha do resultado em um objeto Harvest
		for (QuerySolution soln : rows) {
			String cultUri = soln.getResource("cultivar").getURI();
			String resource = soln.getResource("h").getURI();

			Harvest h = new Harvest();

			// Preenche a descrição (pode ser nula, pois o campo é opcional na consulta)
			h.setDescription(RDFUtil.getStringLiteral(soln.getLiteral("description")));

			// Monta as quantidades de tempo (valor + unidade) para verão e inverno;
			// o cast é necessário porque RDFUtil retorna um Quantity genérico
			h.setTimeToHarvestSummer((Quantity<Time>) RDFUtil.getQuantity(soln.getLiteral("thsvalue"), soln.getResource("thsunit")));
			h.setTimeToHarvestWinter((Quantity<Time>) RDFUtil.getQuantity(soln.getLiteral("thwvalue"), soln.getResource("thwunit")));

			// Associa as condições previamente carregadas a esta colheita
			h.setConditions(conditionsMap.get(resource));

			// Adiciona a colheita à lista da cultivar, criando a lista se ainda não existir
			harvestMap.computeIfAbsent(cultUri, k -> new ArrayList<>()).add(h);
		}

		return harvestMap;
	}

	/**
	 * Método de teste manual: consulta as colheitas de uma cultivar de exemplo
	 * e imprime o resultado no console.
	 */
	public static void main(String[] args) {
		HarvestDAO dao = new HarvestDAO();
		Set<String> cultivars = new HashSet<>();
		cultivars.add(Constants.CONTO_NS + "cv_Itapua_202");
		System.out.println(dao.getHarvestsByCultivars(cultivars));
	}
}