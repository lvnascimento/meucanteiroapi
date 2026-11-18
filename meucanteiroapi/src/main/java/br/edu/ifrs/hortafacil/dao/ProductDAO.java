package br.edu.ifrs.hortafacil.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;

import br.edu.ifrs.hortafacil.Constants;
import br.edu.ifrs.hortafacil.model.product.Product;
import br.edu.ifrs.hortafacil.util.DateUtil;

/**
 * DAO (Data Access Object) responsável por consultar produtos (culturas ou
 * espécies vegetais) da ontologia de cultivares em um triplestore RDF (Apache
 * Jena Fuseki), filtrando por mês de plantio e estado (unidade federativa) de
 * cultivo.
 * <p>
 * A consulta é feita via SPARQL, retornando, para cada produto que possua
 * algum cultivar cuja época de plantio inclua o mês informado e cuja região de
 * cultivo abranja o estado informado, seu nome (regional, quando disponível,
 * ou o nome preferencial), rótulos alternativos e uma foto ilustrativa.
 */
public class ProductDAO {

	/**
	 * Separador usado no GROUP_CONCAT dos rótulos alternativos. Precisa ser o
	 * mesmo da consulta abaixo. Foi escolhido "|" (e não ",") porque rótulos
	 * podem conter vírgulas.
	 */
	private static final String LABEL_SEPARATOR = "|";

	/**
	 * Consulta SPARQL por mês e estado.
	 */
	private static final String QUERY_TEMPLATE_MONTH_STATE = """
			PREFIX conto:  <%s>
			PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX schema: <https://schema.org/>
			PREFIX dul:    <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>
			PREFIX time:   <http://www.w3.org/2006/time#>
			PREFIX skos:   <http://www.w3.org/2004/02/skos/core#>

			SELECT ?class
			       (COALESCE(?regional_name, ?pref_name) AS ?prod_name)
			       (GROUP_CONCAT(DISTINCT ?alt; separator="|") AS ?altLabels)
			       ?photo
			WHERE {
			  # Classe (produto) de cada instância de produto: precisa ser
			  # subclasse de "Produto Principal" e ter nome preferencial e foto
			  ?prod a ?class .
			  ?class rdfs:subClassOf conto:p_ProdutoPrincipal ;
			         rdfs:label ?pref_name ;
			         schema:image ?photo .

			  # Cadeia cultivar > produto > manejo > época de plantio > região,
			  # filtrando pelo mês e pelo estado informados
			  ?cult conto:providesProduct ?prod ;
			        conto:hasManagement ?mngt .
			  ?mngt conto:hasPlantingSeason ?season .
			  ?season dul:hasLocation ?reg ;
			          time:hasTime ?monthIri .
			  ?reg dul:hasPart ?estado .
			  ?estado skos:altLabel ?stateAbbr .

			  # Nome regional do produto: entre os nomes regionais da classe,
			  # aquele cuja localização inclui o mesmo estado filtrado acima
			  OPTIONAL {
			    ?class conto:hasRegionalName ?rn .
			    ?rn dul:hasLocation ?n_reg ;
			        rdfs:label ?regional_name .
			    ?n_reg dul:hasPart ?estado .
			  }

			  # Rótulos alternativos (sinônimos) da classe do produto
			  OPTIONAL {
			    ?class skos:altLabel ?alt .
			  }
			}
			GROUP BY ?class ?regional_name ?pref_name ?photo
			ORDER BY ?prod_name
			""".formatted(Constants.CONTO_NS);
	
	/**
	 * Consulta SPARQL para todos os produtos.
	 */
	private static final String QUERY_TEMPLATE_ALL = """
			PREFIX conto:  <%s>
			PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX schema: <https://schema.org/>
			PREFIX dul:    <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>
			PREFIX time:   <http://www.w3.org/2006/time#>
			PREFIX skos:   <http://www.w3.org/2004/02/skos/core#>

			SELECT ?class
			       (COALESCE(?regional_name, ?pref_name) AS ?prod_name)
			       (GROUP_CONCAT(DISTINCT ?alt; separator="|") AS ?altLabels)
			       ?photo
			WHERE {
			  # Classe (produto) de cada instância de produto: precisa ser
			  # subclasse de "Produto Principal" e ter nome preferencial e foto
			  ?prod a ?class .
			  ?class rdfs:subClassOf conto:p_ProdutoPrincipal ;
			         rdfs:label ?pref_name ;
			         schema:image ?photo .

			  # Nome regional do produto: entre os nomes regionais da classe,
			  # aquele cuja localização inclui o mesmo estado filtrado acima
			  OPTIONAL {
			    ?class conto:hasRegionalName ?rn .
			    ?rn rdfs:label ?regional_name .
			  }

			  # Rótulos alternativos (sinônimos) da classe do produto
			  OPTIONAL {
			    ?class skos:altLabel ?alt .
			  }
			}
			GROUP BY ?class ?regional_name ?pref_name ?photo
			ORDER BY ?prod_name
			""".formatted(Constants.CONTO_NS);
	
	/**
	 * Consulta SPARQL para produtos por nome.
	 */
	private static final String QUERY_TEMPLATE_BY_NAME = """
			PREFIX conto:  <%s>
			PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX schema: <https://schema.org/>
			PREFIX dul:    <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>
			PREFIX time:   <http://www.w3.org/2006/time#>
			PREFIX skos:   <http://www.w3.org/2004/02/skos/core#>

			SELECT ?class
			       (COALESCE(?regional_name, ?pref_name) AS ?prod_name)
			       (GROUP_CONCAT(DISTINCT ?alt; separator="|") AS ?altLabels)
			       ?photo
			WHERE {
			  # Classe (produto) de cada instância de produto: precisa ser
			  # subclasse de "Produto Principal" e ter nome preferencial e foto
			  ?prod a ?class .
			  ?class rdfs:subClassOf conto:p_ProdutoPrincipal ;
			         rdfs:label ?pref_name ;
			         schema:image ?photo .
			  
			  # Nome regional do produto: entre os nomes regionais da classe,
			  # aquele cuja localização inclui o mesmo estado filtrado acima
			  OPTIONAL {
			    ?class conto:hasRegionalName ?rn .
			    ?rn rdfs:label ?regional_name .
			  }

			  # Rótulos alternativos (sinônimos) da classe do produto
			  OPTIONAL {
			    ?class skos:altLabel ?alt .
			  }
			  
			  FILTER (regex(?pref_name, ?prodName, "i") || regex(?regional_name, ?prodName, "i") || regex(?alt, ?prodName, "i"))
			}
			GROUP BY ?class ?regional_name ?pref_name ?photo
			ORDER BY ?prod_name
			""".formatted(Constants.CONTO_NS);

	/**
	 * Busca os produtos cujo plantio é recomendado em um determinado mês e
	 * estado.
	 * <p>
	 * Para cada produto encontrado, o nome retornado prioriza o nome regional
	 * (associado ao estado consultado), caso exista; caso contrário, é usado o
	 * nome preferencial (rótulo padrão) do produto na ontologia. Rótulos
	 * alternativos ({@code skos:altLabel}) são agregados em uma única lista.
	 * <p>
	 * Cada produto aparece uma única vez, mesmo que a consulta retorne mais de
	 * uma linha para ele (por exemplo, com mais de um nome regional ou foto);
	 * nesse caso, vale a primeira linha na ordem por nome.
	 *
	 * @param month o mês de plantio desejado, no formato numérico (1 = Janeiro,
	 *              12 = Dezembro), usado para indexar {@link DateUtil#MONTHS_URIS}
	 * @param state o estado de cultivo, como cadastrado no rótulo alternativo
	 *              ({@code skos:altLabel}) do recurso de estado na ontologia
	 * @return a lista de produtos recomendados para o mês e estado informados,
	 *         ordenada por nome; lista vazia caso nenhum seja encontrado
	 * @throws IllegalArgumentException se o mês ou o estado forem inválidos
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public List<Product> getProductsByMonthAndState(int month, String state) {
		if (state == null || state.isBlank()) {
			throw new IllegalArgumentException("O estado não pode ser nulo ou vazio.");
		}
		String monthIri = monthIri(month);

		// Injeta mês (IRI) e estado (literal): o Jena cuida do escape,
		// impedindo que um valor malicioso altere a estrutura da consulta
		ParameterizedSparqlString pss = new ParameterizedSparqlString(QUERY_TEMPLATE_MONTH_STATE);
		pss.setIri("monthIri", monthIri);
		pss.setLiteral("stateAbbr", state);

		// O resultado é lido por completo e a conexão é fechada antes do
		// processamento (sem leitura preguiçosa do stream HTTP)
		List<QuerySolution> rows = DAO.runSelect(pss.asQuery(),
				"Erro ao consultar os produtos do mês " + month + " e estado '" + state + "'");

		return getProducts(rows);
	}
	
	/**
	 * Busca todos produtos cadastrados na base de conhecimento.
	 * <p>
	 * Para cada produto encontrado, os rótulos alternativos ({@code skos:altLabel}) 
	 * são agregados em uma única lista.
	 * <p>
	 * Cada produto aparece uma única vez, mesmo que a consulta retorne mais de
	 * uma linha para ele (por exemplo, com mais de um nome regional ou foto);
	 * nesse caso, vale a primeira linha na ordem por nome.
	 *
	 * @return a lista de produtos; lista vazia caso nenhum seja encontrado
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public List<Product> getAllProducts() {
		ParameterizedSparqlString pss = new ParameterizedSparqlString(QUERY_TEMPLATE_ALL);
		
		// O resultado é lido por completo e a conexão é fechada antes do
		// processamento (sem leitura preguiçosa do stream HTTP)
		List<QuerySolution> rows = DAO.runSelect(pss.asQuery(),
				"Erro ao consultar todos os produtos.");

		return getProducts(rows);
	}
	
	/**
	 * Busca os produtos cadastrados na base de conhecimento que contém em seu nome o texto especificado
	 * em {@code prodName}. Esse texto pode estar contido no nome do produto, em nomes regionais ou em nomes
	 * alternativos.
	 * <p>
	 * Para cada produto encontrado, os rótulos alternativos ({@code skos:altLabel}) 
	 * são agregados em uma única lista.
	 * <p>
	 * Cada produto aparece uma única vez, mesmo que a consulta retorne mais de
	 * uma linha para ele (por exemplo, com mais de um nome regional ou foto);
	 * nesse caso, vale a primeira linha na ordem por nome.
	 * 
	 * @param prodName texto que deve estar contido no nome do produto.
	 * @return a lista de produtos; lista vazia caso nenhum seja encontrado
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public List<Product> getProductsByName(String prodName) {
		ParameterizedSparqlString pss = new ParameterizedSparqlString(QUERY_TEMPLATE_BY_NAME);
		pss.setLiteral("prodName", prodName);

		// O resultado é lido por completo e a conexão é fechada antes do
		// processamento (sem leitura preguiçosa do stream HTTP)
		List<QuerySolution> rows = DAO.runSelect(pss.asQuery(),
				"Erro ao consultar os produtos pelo nome '" + prodName + "'");

		return getProducts(rows);
	}

	/**
	 * Valida o mês e devolve o IRI correspondente na ontologia, a partir de
	 * {@link DateUtil#MONTHS_URIS} (itens no formato "conto:NomeDoMes").
	 */
	private static String monthIri(int month) {
		if (month < 1 || month > DateUtil.MONTHS_URIS.length) {
			throw new IllegalArgumentException(
					"Mês inválido: " + month + " (esperado de 1 a " + DateUtil.MONTHS_URIS.length + ")");
		}
		String prefixedName = DateUtil.MONTHS_URIS[month - 1];
		return Constants.CONTO_NS + prefixedName.substring(prefixedName.indexOf(':') + 1);
	}
	
	/**
	 * Constrói uma lista de produtos a partir da lista de linhas de resultados de uma consulta SPARQL.
	 * 
	 * @param rows Lista de linhas de resultado.
	 * @return Lista de produtos.
	 */
	private List<Product> getProducts(List<QuerySolution> rows) {
		List<Product> prods = new ArrayList<>();

		// Descarta linhas repetidas para a mesma classe de produto
		Set<String> seen = new HashSet<>();

		// Percorre cada resultado (cada produto encontrado) e monta o objeto
		// de domínio correspondente
		for (QuerySolution soln : rows) {
			String classUri = soln.getResource("class").getURI();
			if (!seen.add(classUri)) {
				continue;
			}

			Product p = new Product();
			p.setResource(classUri);
			p.setName(soln.getLiteral("prod_name").getString());
			p.setPhoto(soln.getResource("photo").getURI());

			// GROUP_CONCAT devolve string vazia (e não nulo) quando não há
			// rótulos; por isso removemos itens vazios e espaços. Sem isso, um
			// produto sem sinônimos ficaria com a lista [""].
			Literal altLabels = soln.getLiteral("altLabels");
			if (altLabels != null) {
				List<String> labels = Arrays
						.stream(altLabels.getString().split(Pattern.quote(LABEL_SEPARATOR)))
						.map(String::trim)
						.filter(label -> !label.isEmpty())
						.collect(Collectors.toCollection(ArrayList::new));
				p.setAltLabels(labels);
			}

			prods.add(p);
		}

		return prods;
	}
}