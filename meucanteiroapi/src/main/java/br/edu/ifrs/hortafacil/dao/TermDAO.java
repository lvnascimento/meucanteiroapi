package br.edu.ifrs.hortafacil.dao;

import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;

import br.edu.ifrs.hortafacil.Constants;
import br.edu.ifrs.hortafacil.model.general.Term;
import br.edu.ifrs.hortafacil.util.RDFUtil;

/**
 * DAO (Data Access Object) responsável por consultar termos da ontologia de
 * cultivares em um triplestore RDF (Apache Jena Fuseki).
 * <p>
 * As consultas são feitas via SPARQL, buscando o rótulo (label) e a descrição
 * de um termo, seja através do vocabulário Dublin Core (dc) ou do vocabulário
 * próprio da ontologia de cultivares (conto).
 */
public class TermDAO {

	/**
	 * Consulta SPARQL.
	 */
	private static final String QUERY_TEMPLATE = """
			PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>
			PREFIX dc:    <http://purl.org/dc/elements/1.1/>
			PREFIX conto: <%s>

			SELECT ?description
			WHERE {
			  # Localiza o termo pelo rótulo exato (rdfs:label ou conto:name)
			  {
			    ?concept rdfs:label ?fullLabel .
			  } UNION {
			    ?concept conto:name ?fullLabel .
			  }
			  FILTER(STR(?fullLabel) = ?termLabel)

			  # Descrição via Dublin Core, em pt-BR (opcional)
			  OPTIONAL {
			    ?concept dc:description ?dcDescription .
			    FILTER(LANG(?dcDescription) = "pt-BR")
			  }

			  # Descrição via ontologia "conto", em pt-BR (opcional)
			  OPTIONAL {
			    ?concept conto:description ?contoDescription .
			    FILTER(LANG(?contoDescription) = "pt-BR")
			  }

			  # Prioriza a descrição do Dublin Core
			  BIND(COALESCE(?dcDescription, ?contoDescription) AS ?description)
			}
			ORDER BY DESC(BOUND(?description))
			LIMIT 1
			""".formatted(Constants.CONTO_NS);

	/**
	 * Busca um termo da ontologia a partir do seu rótulo (label).
	 * <p>
	 * O termo é localizado por {@code rdfs:label} ou {@code conto:name}. A
	 * descrição é procurada em duas fontes possíveis dentro do grafo RDF:
	 * primeiro nas propriedades do Dublin Core ({@code dc:description}) e, caso
	 * não exista, nas propriedades próprias da ontologia de cultivares
	 * ({@code conto:description}). Em ambos os casos, apenas descrições no
	 * idioma português do Brasil ({@code pt-BR}) são consideradas.
	 *
	 * @param label o rótulo do termo a ser buscado (ex.: nome de uma
	 *              característica ou categoria da ontologia)
	 * @return um objeto {@link Term} contendo o rótulo informado e a descrição
	 *         encontrada (que pode ser {@code null} se o termo existir sem
	 *         descrição em pt-BR), ou {@code null} caso nenhum termo com esse
	 *         rótulo seja encontrado
	 * @throws IllegalArgumentException se o rótulo for nulo ou vazio
	 * @throws IllegalStateException    se houver falha ao consultar o Fuseki
	 */
	public Term getTermByLabel(String label) {
		if (label == null || label.isBlank()) {
			throw new IllegalArgumentException("O rótulo do termo não pode ser nulo ou vazio.");
		}

		// Injeta o rótulo como literal: o Jena cuida do escape, impedindo que
		// um valor malicioso altere a estrutura da consulta
		ParameterizedSparqlString pss = new ParameterizedSparqlString(QUERY_TEMPLATE);
		pss.setLiteral("termLabel", label);

		List<QuerySolution> rows = runSelect(pss.asQuery(),
				"Erro ao consultar o termo '" + label + "'");

		// Nenhum termo encontrado para o rótulo informado
		if (rows.isEmpty()) {
			return null;
		}

		QuerySolution soln = rows.get(0);

		Term t = new Term();
		t.setLabel(label);
		t.setDescription(RDFUtil.getStringLiteral(soln.getLiteral("description")));

		return t;
	}

	/**
	 * Executa uma consulta SELECT e devolve todos os resultados já carregados em
	 * memória. A conexão e a execução são fechadas ao final, mesmo em caso de
	 * erro.
	 */
	private static List<QuerySolution> runSelect(Query query, String errorMessage) {
		try (RDFConnection conn = RDFConnectionFuseki.create()
				.destination(Constants.KNOWLEDGE_BASE_URL)
				.build();
				QueryExecution exec = conn.query(query)) {

			// toList consome todo o ResultSet antes de fechar a conexão
			return ResultSetFormatter.toList(exec.execSelect());
		} catch (RuntimeException e) {
			// Dá contexto a falhas de rede, Fuseki fora do ar, query inválida...
			throw new IllegalStateException(errorMessage, e);
		}
	}
}