package br.edu.ifrs.hortafacil.model.management;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import br.edu.ifrs.hortafacil.dto.FertilizationPracticeDTO;

/**
 * Prática de manejo agrícola (ex.: adubação, irrigação).
 *
 * Serve como classe base e também como tipo "genérico" de prática.
 * É o equivalente no back-end da classe `ManagementPractice` do front-end.
 *
 * Polimorfismo com Jackson: ao serializar para JSON, o Jackson inclui uma
 * propriedade "type" que indica qual subtipo é o objeto. Ao desserializar,
 * ele lê essa propriedade para saber qual classe instanciar.
 * - Id.NAME: usa nomes lógicos (definidos abaixo) em vez do nome completo da classe
 * - As.PROPERTY: o tipo vai como um campo dentro do próprio objeto JSON
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    // "type": "FERTILIZATION" → desserializa como FertilizationPracticeDTO
    @JsonSubTypes.Type(value = FertilizationPracticeDTO.class, name = "FERTILIZATION"),
    // "type": "GENERIC" → desserializa como a própria ManagementPractice
    @JsonSubTypes.Type(value = ManagementPractice.class, name = "GENERIC")
})
public class ManagementPractice {
	// Nome curto da prática (ex.: "Adubação de base")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	// Descrição detalhada de como executar a prática
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// Alternativas para a prática. Cada alternativa é, por sua vez,
	// uma ManagementPractice (estrutura recursiva).
	public List<ManagementPractice> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<ManagementPractice> alternatives) {
		this.alternatives = alternatives;
	}

	/**
	 * Gera o hash a partir de todos os campos, de forma coerente com o equals.
	 * `Objects.hash` já lida com valores nulos.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(alternatives, description, label);
	}

	/**
	 * Duas práticas são iguais se forem exatamente da mesma classe
	 * e tiverem os mesmos valores de label, description e alternatives.
	 */
	@Override
	public boolean equals(Object obj) {
		// Mesmo objeto na memória
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// getClass() (e não instanceof): uma ManagementPractice nunca é igual
		// a uma subclasse, mesmo com os mesmos campos
		if (getClass() != obj.getClass())
			return false;
		ManagementPractice other = (ManagementPractice) obj;
		// Objects.equals compara tratando null com segurança
		return Objects.equals(alternatives, other.alternatives) && Objects.equals(description, other.description)
				&& Objects.equals(label, other.label);
	}
	
	@Override
	public String toString() {
		return "ManagementPractice [label=" + label + ", description=" + description + ", alternatives=" + alternatives
				+ "]";
	}

	// Atributos (privados, acessados pelos getters/setters acima)
	private String label;
	private String description;
	private List<ManagementPractice> alternatives;
}