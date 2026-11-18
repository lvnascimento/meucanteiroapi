package br.edu.ifrs.hortafacil.model.cultivars;

/**
 * Representa a identificação de um cultivar, contendo seu recurso RDF
 * (URI) e seu nome.
 * <p>
 * É utilizada como uma referência leve a um cultivar — por exemplo,
 * dentro de {@code CultivarPreferenceProfile}, para indicar quais
 * cultivares compartilham uma determinada assinatura de preferências —
 * sem carregar todos os demais dados do cultivar.
 */
public class CultivarID {

	/** URI do recurso RDF que identifica o cultivar na ontologia. */
	private String resource;

	/** Nome do cultivar. */
	private String name;

	/**
	 * Retorna o recurso RDF que identifica o cultivar.
	 *
	 * @return a URI do recurso do cultivar
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Define o recurso RDF que identifica o cultivar.
	 *
	 * @param resource a URI do recurso a ser atribuída ao cultivar
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * Retorna o nome do cultivar.
	 *
	 * @return o nome do cultivar
	 */
	public String getName() {
		return name;
	}

	/**
	 * Define o nome do cultivar.
	 *
	 * @param name o nome a ser atribuído ao cultivar
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CultivarID [resource=" + resource + ", name=" + name + "]";
	}
}