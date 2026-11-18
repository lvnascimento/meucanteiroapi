package br.edu.ifrs.hortafacil.model.management;

import java.util.Objects;

/**
 * Modelo de domínio que representa uma condição que deve ser atendida para
 * aplicar um manejo (ver {@link Management}), por exemplo uma condição
 * climática ou do estado da planta.
 * <p>
 * É um objeto simples de transferência de dados, montado a partir da base de
 * conhecimento RDF (ver {@code ConditionDAO}).
 */
public class Condition {

	/** Rótulo (nome curto) da condição. */
	private String label;

	/**
	 * Descrição detalhada da condição; {@code null} quando a ontologia não
	 * traz uma descrição.
	 */
	private String description;

	/**
	 * @return o rótulo (nome curto) da condição
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label o rótulo (nome curto) da condição
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return a descrição detalhada da condição, ou {@code null} se não existir
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description a descrição detalhada da condição; pode ser
	 *                    {@code null}
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, label);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition other = (Condition) obj;
		return Objects.equals(description, other.description) && Objects.equals(label, other.label);
	}

	@Override
	public String toString() {
		return "Condition [label=" + label + ", description=" + description + "]";
	}
}