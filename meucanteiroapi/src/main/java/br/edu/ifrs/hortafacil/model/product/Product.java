package br.edu.ifrs.hortafacil.model.product;

import java.util.List;

/**
 * Representa um produto da ontologia de cultivares, cujos dados 
 * podem ser consultados via API REST.
 * <p>
 * Um produto é identificado por seu recurso RDF ({@code resource}) e
 * possui um nome principal, uma lista de rótulos alternativos (nomes
 * populares ou sinônimos) e, opcionalmente, uma foto ilustrativa.
 */
public class Product {

	/** URI do recurso RDF que identifica o produto na ontologia. */
	private String resource;

	/** Nome principal do produto (ex.: "Tomate", "Alface"). */
	private String name;

	/**
	 * Lista de rótulos alternativos do produto (nomes populares,
	 * sinônimos ou variações do nome principal).
	 */
	private List<String> altLabels;

	/** URL ou caminho da foto ilustrativa do produto. */
	private String photo;

	/**
	 * Retorna o recurso RDF que identifica o produto.
	 *
	 * @return a URI do recurso do produto
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Define o recurso RDF que identifica o produto.
	 *
	 * @param resource a URI do recurso a ser atribuída ao produto
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * Retorna o nome principal do produto.
	 *
	 * @return o nome do produto
	 */
	public String getName() {
		return name;
	}

	/**
	 * Define o nome principal do produto.
	 *
	 * @param name o nome a ser atribuído ao produto
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna os rótulos alternativos do produto.
	 *
	 * @return a lista de rótulos alternativos (nomes populares ou
	 *         sinônimos) do produto
	 */
	public List<String> getAltLabels() {
		return altLabels;
	}

	/**
	 * Define os rótulos alternativos do produto.
	 *
	 * @param altLabels a lista de rótulos alternativos a ser atribuída
	 *                  ao produto
	 */
	public void setAltLabels(List<String> altLabels) {
		this.altLabels = altLabels;
	}

	/**
	 * Retorna a foto ilustrativa do produto.
	 *
	 * @return a URL ou caminho da foto do produto
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * Define a foto ilustrativa do produto.
	 *
	 * @param photo a URL ou caminho da foto a ser atribuída ao produto
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}
}