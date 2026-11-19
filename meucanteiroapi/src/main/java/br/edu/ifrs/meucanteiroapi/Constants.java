package br.edu.ifrs.meucanteiroapi;

public class Constants {
	public static final String KNOWLEDGE_BASE_URL = System.getenv().getOrDefault("FUSEKI_URL", "http://localhost:3030/horta");;
	
	public static final String CONTO_NS = "http://www.semanticweb.org/lvian/ontologies/2024/4/conto#";
}
