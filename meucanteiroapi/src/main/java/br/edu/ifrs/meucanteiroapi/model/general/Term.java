package br.edu.ifrs.meucanteiroapi.model.general;

/**
 * Representa um termo da ontologia de cultivares.
 * <p>
 * Um termo é composto por um rótulo (label) e uma descrição (description) que explica o significa do termo.
 */
public class Term {

	/** Rótulo (nome) do termo da ontologia. */
	private String label;

	/** Descrição textual do termo. */
	private String description;

	/**
	 * Retorna o rótulo do termo.
	 *
	 * @return o rótulo (label) do termo
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Define o rótulo do termo.
	 *
	 * @param label o rótulo (label) a ser atribuído ao termo
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Retorna a descrição do termo.
	 *
	 * @return a descrição do termo
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Define a descrição do termo.
	 *
	 * @param description a descrição a ser atribuída ao termo
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}