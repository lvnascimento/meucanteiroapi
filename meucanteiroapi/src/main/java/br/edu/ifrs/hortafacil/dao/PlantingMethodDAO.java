package br.edu.ifrs.hortafacil.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import br.edu.ifrs.hortafacil.Constants;
import br.edu.ifrs.hortafacil.model.general.QuantityInterval;
import br.edu.ifrs.hortafacil.model.management.planting.DirectSeeding;
import br.edu.ifrs.hortafacil.model.management.planting.PlantingMethod;
import br.edu.ifrs.hortafacil.model.management.planting.SeedlingTransplanting;
import br.edu.ifrs.hortafacil.util.RDFUtil;

/**
 * DAO (Data Access Object) responsável por consultar os métodos de plantio
 * associados a um manejo (management) de cultivo em um triplestore RDF
 * (Apache Jena Fuseki).
 * <p>
 * A consulta é feita via SPARQL, retornando, para cada método de plantio
 * associado ao recurso de manejo informado, os dados específicos conforme o
 * tipo do método: profundidade, diâmetro e tempo até a germinação para
 * semeadura direta ({@link DirectSeeding}), ou instruções e métodos de
 * propagação para transplante de mudas ({@link SeedlingTransplanting}).
 */
public class PlantingMethodDAO {

	/** Tipo RDF que identifica um método de semeadura direta. */
	private static final String DIRECT_SEEDING_TYPE = Constants.CONTO_NS + "DirectSeeding";

	/**
	 * Separador usado no GROUP_CONCAT dos métodos de propagação. Precisa ser o
	 * mesmo da consulta abaixo. Foi escolhido "|" (e não ",") porque nomes de
	 * métodos podem conter vírgulas.
	 */
	private static final String PROPAGATION_SEPARATOR = "|";

	/**
	 * Consulta SPARQL.
	 */
	private static final String QUERY_TEMPLATE = """
			PREFIX qudt:  <http://purl.org/qudt/schema/qudt/>
			PREFIX conto: <%s>

			SELECT ?management ?pMethod ?pMethodType
			       ?pTimeToGerminateUnit ?pTimeToGerminateMin ?pTimeToGerminateMax
			       ?pDepthUnit ?pDepthMin ?pDepthMax
			       ?pWidthVal ?pWidthUnit
			       ?seedlingPlantingDescription
			       (GROUP_CONCAT(DISTINCT ?propMethodName; separator="|") AS ?propMethods)
			WHERE {
			  VALUES ?management { %s }
			
			  # Apenas os tipos de método suportados
			  VALUES ?pMethodType { conto:DirectSeeding conto:SeedlingTransplanting }

			  # Métodos de plantio do manejo informado
			  ?management a conto:PlantingManagement ;
			              conto:hasPlantingMethod ?pMethod .
			  ?pMethod a ?pMethodType .

			  # Tempo até a germinação: mínimo, máximo e unidade
			  # (semeadura direta)
			  OPTIONAL {
			    ?pMethod conto:timeToGerminate ?pTimeToGerminate .
			    ?pTimeToGerminate qudt:quantityValue ?valTimeToGerminate .
			    ?valTimeToGerminate qudt:hasUnit ?pTimeToGerminateUnit ;
			                        qudt:minInclusive ?pTimeToGerminateMin ;
			                        qudt:maxInclusive ?pTimeToGerminateMax .
			  }

			  # Profundidade de plantio: mínimo, máximo e unidade
			  # (semeadura direta)
			  OPTIONAL {
			    ?pMethod conto:depth ?pDepth .
			    ?pDepth qudt:quantityValue ?valDepth .
			    ?valDepth qudt:hasUnit ?pDepthUnit ;
			              qudt:minInclusive ?pDepthMin ;
			              qudt:maxInclusive ?pDepthMax .
			  }

			  # Largura (usada como diâmetro): valor e unidade
			  # (semeadura direta)
			  OPTIONAL {
			    ?pMethod conto:width ?pWidth .
			    ?pWidth qudt:quantityValue ?valWidth .
			    ?valWidth qudt:hasUnit ?pWidthUnit ;
			              qudt:numericValue ?pWidthVal .
			  }

			  # Descrição do plantio (transplante de mudas)
			  OPTIONAL {
			    ?pMethod conto:plantingDescription ?seedlingPlantingDescription .
			  }

			  # Métodos de propagação (transplante de mudas), em pt-BR
			  OPTIONAL {
			    ?pMethod conto:hasPropagationMethod ?propMethod .
			    ?propMethod conto:name ?propMethodName .
			    FILTER(LANG(?propMethodName) = "pt-BR")
			  }
			}
			GROUP BY ?management ?pMethod ?pMethodType
			         ?pTimeToGerminateUnit ?pTimeToGerminateMin ?pTimeToGerminateMax
			         ?pDepthUnit ?pDepthMin ?pDepthMax
			         ?pWidthVal ?pWidthUnit
			         ?seedlingPlantingDescription
			ORDER BY ?pMethodType ?pMethod
			""";

	/**
	 * Busca todos os métodos de plantio associados a recursos de manejo de
	 * plantio (planting management) no triplestore.
	 * <p>
	 * Para cada método de plantio encontrado, o tipo RDF do recurso
	 * ({@code conto:DirectSeeding} ou {@code conto:SeedlingTransplanting})
	 * determina qual subclasse de {@link PlantingMethod} será instanciada e
	 * como seus atributos específicos serão preenchidos:
	 * <ul>
	 * <li>{@link DirectSeeding}: profundidade de plantio (intervalo), diâmetro
	 * (a partir da largura/{@code width}) e tempo até a germinação
	 * (intervalo);</li>
	 * <li>{@link SeedlingTransplanting}: instruções de plantio e a lista de
	 * métodos de propagação recomendados (em português, {@code pt-BR}).</li>
	 * </ul>
	 * 
	 * @param mngtUris URIs absolutas (como {@code String}) dos recursos RDF que
	 *                representam os manejos de plantio a serem consultados
	 * @return um map associando URIs de manejo a listas de métodos de plantio associados
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki ou
	 *                                  se uma unidade lida tiver dimensão
	 *                                  incompatível
	 */
	public Map<String, List<PlantingMethod>> getPlantingMethodsByManagements(Set<String> mngtUris) {
		if (mngtUris.isEmpty()) return Map.of();

	    	List<QuerySolution> rows = DAO.query(QUERY_TEMPLATE, mngtUris);
		
		Map<String, List<PlantingMethod>> resultMap = new HashMap<>();
	    Set<String> seen = new HashSet<>();

	    for (QuerySolution soln : rows) {
	        String mngtUri = soln.getResource("management").getURI();
	        String pMethodUri = soln.getResource("pMethod").getURI();

	        // Evita duplicatas por manejo + método
	        if (!seen.add(mngtUri + "|" + pMethodUri)) {
	            continue;
	        }

	        String type = soln.getResource("pMethodType").getURI();
	        PlantingMethod pm = DIRECT_SEEDING_TYPE.equals(type) 
	                ? buildDirectSeeding(soln) 
	                : buildSeedlingTransplanting(soln);

	        resultMap.computeIfAbsent(mngtUri, k -> new ArrayList<>()).add(pm);
	    }
		return resultMap;
	}

	/**
	 * Monta um {@link DirectSeeding}: preenche profundidade, diâmetro e tempo de
	 * germinação apenas quando os respectivos dados estão presentes.
	 */
	private static DirectSeeding buildDirectSeeding(QuerySolution soln) {
		DirectSeeding pm = new DirectSeeding();

		// Profundidade (intervalo de comprimento). Se algum dado do grupo
		// estiver ausente, toInterval devolve null e o campo fica sem valor
		QuantityInterval<Length> depth = toInterval(
				soln.getLiteral("pDepthMin"),
				soln.getLiteral("pDepthMax"),
				soln.getResource("pDepthUnit"),
				Length.class, "profundidade");
		if (depth != null) {
			pm.setDepth(depth);
		}

		// Diâmetro (a partir da largura, comprimento)
		Quantity<Length> diameter = toQuantity(
				soln.getLiteral("pWidthVal"),
				soln.getResource("pWidthUnit"),
				Length.class, "largura");
		if (diameter != null) {
			pm.setDiameter(diameter);
		}

		// Tempo até a germinação (intervalo de tempo)
		QuantityInterval<Time> timeToGerminate = toInterval(
				soln.getLiteral("pTimeToGerminateMin"),
				soln.getLiteral("pTimeToGerminateMax"),
				soln.getResource("pTimeToGerminateUnit"),
				Time.class, "tempo de germinação");
		if (timeToGerminate != null) {
			pm.setTimeToGerminate(timeToGerminate);
		}

		return pm;
	}

	/**
	 * Monta um {@link SeedlingTransplanting}: preenche as instruções de plantio
	 * e a lista de métodos de propagação (agregados em uma string pelo
	 * GROUP_CONCAT e separados novamente aqui).
	 */
	private static SeedlingTransplanting buildSeedlingTransplanting(QuerySolution soln) {
		SeedlingTransplanting pm = new SeedlingTransplanting();

		// A descrição é opcional: a variável fica sem valor se não existir
		Literal description = soln.getLiteral("seedlingPlantingDescription");
		pm.setInstructions(description != null ? RDFUtil.getStringLiteral(description) : null);

		// GROUP_CONCAT devolve string vazia (e não nulo) quando não há nomes;
		// por isso removemos itens vazios e espaços. Sem isso, um método sem
		// propagação ficaria com a lista [""].
		Literal propMethods = soln.getLiteral("propMethods");
		if (propMethods != null) {
			List<String> names = Arrays
					.stream(propMethods.getString().split(Pattern.quote(PROPAGATION_SEPARATOR)))
					.map(String::trim)
					.filter(name -> !name.isEmpty())
					.collect(Collectors.toCollection(ArrayList::new));
			pm.setPropagationMethods(names);
		}

		return pm;
	}

	/**
	 * Converte valor e unidade lidos do RDF em {@code Quantity<Q>}.
	 * <p>
	 * Retorna {@code null} quando o valor ou a unidade não existem. O cast não
	 * é verificável pelo compilador; por isso a unidade é validada antes com
	 * {@code Unit.asType(type)}.
	 */
	@SuppressWarnings("unchecked")
	private static <Q extends Quantity<Q>> Quantity<Q> toQuantity(Literal value, Resource unit,
			Class<Q> type, String description) {
		if (value == null || unit == null) {
			return null;
		}

		Quantity<?> quantity = RDFUtil.getQuantity(value, unit);
		if (quantity == null) {
			return null;
		}

		requireDimension(quantity, unit, type, description);
		return (Quantity<Q>) quantity;
	}

	/**
	 * Converte mínimo, máximo e unidade lidos do RDF em
	 * {@code QuantityInterval<Q>}.
	 * <p>
	 * Retorna {@code null} quando algum dos três dados não existe. A unidade é
	 * validada antes do cast, usando o valor mínimo como amostra.
	 */
	@SuppressWarnings("unchecked")
	private static <Q extends Quantity<Q>> QuantityInterval<Q> toInterval(Literal min, Literal max,
			Resource unit, Class<Q> type, String description) {
		if (min == null || max == null || unit == null) {
			return null;
		}

		Quantity<?> sample = RDFUtil.getQuantity(min, unit);
		if (sample == null) {
			return null;
		}

		requireDimension(sample, unit, type, description);
		return (QuantityInterval<Q>) RDFUtil.getQuantityInterval(min, max, unit);
	}

	/**
	 * Valida em tempo de execução que a unidade tem a dimensão esperada
	 * ({@code asType} lança {@link ClassCastException} se for incompatível).
	 */
	private static <Q extends Quantity<Q>> void requireDimension(Quantity<?> quantity, Resource unit,
			Class<Q> type, String description) {
		try {
			quantity.getUnit().asType(type);
		} catch (ClassCastException e) {
			throw new IllegalStateException("A unidade <" + unit.getURI() + "> de " + description
					+ " não tem a dimensão esperada (" + type.getSimpleName() + ").", e);
		}
	}
}