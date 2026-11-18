package br.edu.ifrs.hortafacil.dao;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Quantity;

import org.apache.jena.query.QuerySolution;

import br.edu.ifrs.hortafacil.model.general.MassPerArea;
import br.edu.ifrs.hortafacil.model.management.FertilizerAlternativeItem;
import br.edu.ifrs.hortafacil.util.RDFUtil;

/**
 * DAO (Data Access Object) responsável por consultar os itens de alternativas
 * de fertilização associadas a uma prática de manejo em um triplestore RDF
 * (Apache Jena Fuseki).
 * <p>
 * A consulta é feita via SPARQL, retornando, para cada item associado à prática
 * informada, o nome do fertilizante e a quantidade recomendada por área
 * (massa por área — ver {@link MassPerArea}).
 */
public class AlternativeItemsDAO {

	/**
	 * Consulta SPARQL.
	 */
	private static final String QUERY_TEMPLATE = """
			PREFIX ro:    <http://purl.obolibrary.org/obo/RO_>
			PREFIX qudt:  <http://purl.org/qudt/schema/qudt/>
			PREFIX conto: <%s>
			PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>

			SELECT DISTINCT ?practice ?alt ?altFertLabel ?quantValNum ?quantValUnit
			WHERE {
			  VALUES ?practice { %s }
			  # Alternativas de fertilização da prática de manejo informada
			  ?practice conto:hasAlternativeItem ?alt .

			  # Fertilizante associado à alternativa (propriedade ro:0000057)
			  # e seu rótulo
			  ?alt ro:0000057 ?altFert .
			  ?altFert rdfs:label ?altFertLabel .
			  
			  # Quantidade recomendada: valor numérico e unidade de medida
			  ?alt qudt:hasQuantity ?quant .
			  ?quant qudt:quantityValue ?quantVal .
			  ?quantVal qudt:hasUnit ?quantValUnit ;
			            qudt:numericValue ?quantValNum .
			}
			ORDER BY ?altFertLabel
			""";

	/**
	 * Busca os itens das alternativas de fertilização associadas a uma prática
	 * de manejo no triplestore.
	 * <p>
	 * Para cada item encontrado, obtém o fertilizante associado e a quantidade
	 * recomendada, convertida para a grandeza customizada {@link MassPerArea}.
	 *
	 * @param resource a URI absoluta (como {@code String}) do recurso RDF que
	 *                 representa a prática de manejo a ser consultada
	 * @return a lista de itens de alternativas de fertilização associadas à
	 *         prática informada, ordenada pelo nome do fertilizante; lista
	 *         vazia caso nenhuma seja encontrada
	 * @throws IllegalArgumentException se a URI for nula, vazia ou inválida
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki ou
	 *                                  se uma unidade lida não for massa por área
	 */
	@SuppressWarnings("unchecked")
	public List<FertilizerAlternativeItem> getAlternativesByPractice(String resource) {
		List<QuerySolution> rows = DAO.query(QUERY_TEMPLATE, resource);
		
		List<FertilizerAlternativeItem> alternatives = new ArrayList<>();
		for(QuerySolution soln : rows) {
			FertilizerAlternativeItem alt = new FertilizerAlternativeItem();
			alt.setFertilizerName(RDFUtil.getStringLiteral(soln.getLiteral("altFertLabel")));

			Quantity<?> quantity = RDFUtil.getQuantity(
					soln.getLiteral("quantValNum"),
					soln.getResource("quantValUnit"));

			// Validação em tempo de execução: lança ClassCastException se a
			// unidade não tiver a dimensão de massa por área
			try {
				quantity.getUnit().asType(MassPerArea.class);
			} catch (ClassCastException e) {
				throw new IllegalStateException("A unidade <" + soln.getResource("quantValUnit").getURI()
						+ "> da alternativa <" + soln.getResource("alt").getURI()
						+ "> não representa massa por área.", e);
			}

			// Seguro neste ponto: a unidade foi validada acima
			alt.setQuantity((Quantity<MassPerArea>) quantity);

			alternatives.add(alt);
		}
		
		return alternatives;
	}
}