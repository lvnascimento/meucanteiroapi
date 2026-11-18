package br.edu.ifrs.hortafacil.dao;

import java.util.List;
import java.util.Set;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;

import br.edu.ifrs.hortafacil.Constants;

/**
 * Classe utilitária com a infraestrutura comum de acesso à base de conhecimento
 * RDF (servidor Apache Jena Fuseki). Centraliza a montagem de consultas SPARQL
 * a partir de templates e a execução de consultas SELECT, para que os DAOs
 * específicos (como {@link HarvestDAO}) não precisem repetir esse código.
 */
public class DAO {

	/**
	 * Monta e executa uma consulta SPARQL a partir de um template.
	 * O template deve conter dois placeholders {@code %s}: o primeiro recebe o
	 * namespace da ontologia CONTO e o segundo recebe a lista de URIs
	 * (normalmente usada em uma cláusula {@code VALUES}).
	 *
	 * @param queryTemplate template da consulta SPARQL
	 * @param uris          conjunto de URIs a serem inseridas no template
	 * @return lista com todas as linhas de resultado, já carregadas em memória
	 * @throws IllegalStateException se ocorrer qualquer falha na execução
	 */
	public static List<QuerySolution> query(String queryTemplate, Set<String> uris) {
		// Converte o conjunto de URIs para o formato aceito pela cláusula VALUES
		String uriList = stringifyURISet(uris);

		// Preenche o template com o namespace e as URIs, e cria a consulta SPARQL
		ParameterizedSparqlString pss = new ParameterizedSparqlString(queryTemplate.formatted(Constants.CONTO_NS, uriList));

	    // O resultado é lido por completo e a conexão é fechada ANTES do
		// carregamento dos dados aninhados, que abrem as próprias conexões.
		// Assim não há conexões aninhadas nem leitura preguiçosa do stream.
		return  runSelect(pss.asQuery(),
						"Erro ao executar as consultas usando as seguintes URIS: " + uriList);
	}

	/**
	 * Versão de conveniência de {@link #query(String, Set)} para uma única URI.
	 *
	 * @param queryTemplate template da consulta SPARQL
	 * @param uri           URI a ser inserida no template
	 * @return lista com todas as linhas de resultado, já carregadas em memória
	 */
	public static List<QuerySolution> query(String queryTemplate, String uri) {
		return query(queryTemplate, Set.of(uri));
	}

	/**
	 * Converte um conjunto de URIs em uma string no formato SPARQL, com cada URI
	 * entre {@code <>} e separada das demais por espaço.
	 * Exemplo: {@code "<http://ex.org/a> <http://ex.org/b> "}.
	 *
	 * @param uris conjunto de URIs a serem convertidas
	 * @return string pronta para uso dentro de uma cláusula {@code VALUES}
	 */
	public static String stringifyURISet(Set<String> uris) {
		StringBuilder valuesBuilder = new StringBuilder();
	    for (String uri : uris) {
	        // Envolve cada URI em <> e adiciona um espaço separador
	        valuesBuilder.append("<").append(uri).append("> ");
	    }
	    return valuesBuilder.toString();
	}

	/**
	 * Executa uma consulta SELECT e devolve todos os resultados já carregados
	 * em memória. A conexão e a execução são fechadas ao final, mesmo em caso
	 * de erro.
	 *
	 * @param query        consulta SPARQL SELECT a ser executada
	 * @param errorMessage mensagem usada para contextualizar a exceção em caso de falha
	 * @return lista com todas as linhas de resultado
	 * @throws IllegalStateException se a conexão ou a execução da consulta falhar
	 */
	public static List<QuerySolution> runSelect(Query query, String errorMessage) {
		// try-with-resources garante o fechamento da conexão e da execução
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