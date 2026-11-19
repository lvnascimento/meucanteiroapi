package br.edu.ifrs.meucanteiroapi.model.management.planting;

import java.util.List;
import java.util.Objects;

/**
 * Representa o método de plantio por transplante de mudas
 * (semeadura antecipada em outro local, seguida de transplante para o
 * local definitivo).
 * <p>
 * Estende {@link PlantingMethod}, especializando-o com informações
 * próprias desse método: instruções específicas de execução e os
 * métodos de propagação usados (ex.: propagação por sementes,
 * por estacas, etc.).
 */
public class SeedlingTransplanting extends PlantingMethod {

	/** Instruções específicas para o transplante de mudas. */
	private String instructions;

	/**
	 * Métodos de propagação usados para esse tipo de plantio
	 * (ex.: sementes, estacas, divisão de touceiras).
	 */
	private List<String> propagationMethods;

	/**
	 * Retorna as instruções de transplante de mudas.
	 *
	 * @return as instruções específicas para este método de plantio
	 */
	public String getInstructions() {
		return instructions;
	}

	/**
	 * Define as instruções de transplante de mudas.
	 *
	 * @param instructions as instruções a serem atribuídas a este
	 *                      método de plantio
	 */
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	/**
	 * Retorna a lista de métodos de propagação usados.
	 *
	 * @return os métodos de propagação associados a este método de
	 *         plantio
	 */
	public List<String> getPropagationMethods() {
		return propagationMethods;
	}

	/**
	 * Define a lista de métodos de propagação usados.
	 *
	 * @param propagationMethods os métodos de propagação a serem
	 *                            atribuídos a este método de plantio
	 */
	public void setPropagationMethods(List<String> propagationMethods) {
		this.propagationMethods = propagationMethods;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instructions, propagationMethods);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeedlingTransplanting other = (SeedlingTransplanting) obj;
		return Objects.equals(instructions, other.instructions)
				&& Objects.equals(propagationMethods, other.propagationMethods);
	}

	@Override
	public String toString() {
		return "SeedlingTransplanting [instructions=" + instructions + ", propagationMethods=" + propagationMethods
				+ "]";
	}
}