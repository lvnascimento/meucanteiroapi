package br.edu.ifrs.hortafacil.model.management.planting;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import br.edu.ifrs.hortafacil.model.general.QuantityInterval;

/**
 * Representa o método de plantio por semeadura direta (as sementes são
 * plantadas diretamente no local definitivo de cultivo, sem passar por
 * transplante de mudas).
 * <p>
 * Estende {@link PlantingMethod}, especializando-o com informações
 * próprias desse método: intervalo de profundidade de plantio das
 * sementes, diâmetro recomendado da semente/cova e intervalo de tempo
 * esperado até a germinação.
 */
public class DirectSeeding extends PlantingMethod {

	/** Intervalo de profundidade recomendado para o plantio das sementes. */
	private QuantityInterval<Length> depth;

	/** Diâmetro recomendado da semente (ou da cova/sulco de plantio). */
	private Quantity<Length> diameter;

	/** Intervalo de tempo esperado até a germinação das sementes. */
	private QuantityInterval<Time> timeToGerminate;

	/**
	 * Retorna o intervalo de profundidade de plantio das sementes.
	 *
	 * @return o intervalo de profundidade recomendado
	 */
	public QuantityInterval<Length> getDepth() {
		return depth;
	}

	/**
	 * Define o intervalo de profundidade de plantio das sementes.
	 *
	 * @param depth o intervalo de profundidade a ser atribuído
	 */
	public void setDepth(QuantityInterval<Length> depth) {
		this.depth = depth;
	}

	/**
	 * Retorna o diâmetro recomendado da semente (ou da cova/sulco de
	 * plantio).
	 *
	 * @return o diâmetro recomendado
	 */
	public Quantity<Length> getDiameter() {
		return diameter;
	}

	/**
	 * Define o diâmetro recomendado da semente (ou da cova/sulco de
	 * plantio).
	 *
	 * @param diameter o diâmetro a ser atribuído
	 */
	public void setDiameter(Quantity<Length> diameter) {
		this.diameter = diameter;
	}

	/**
	 * Retorna o intervalo de tempo esperado até a germinação das
	 * sementes.
	 *
	 * @return o intervalo de tempo até a germinação
	 */
	public QuantityInterval<Time> getTimeToGerminate() {
		return timeToGerminate;
	}

	/**
	 * Define o intervalo de tempo esperado até a germinação das
	 * sementes.
	 *
	 * @param timeToGerminate o intervalo de tempo até a germinação a
	 *                        ser atribuído
	 */
	public void setTimeToGerminate(QuantityInterval<Time> timeToGerminate) {
		this.timeToGerminate = timeToGerminate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(depth, diameter, timeToGerminate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectSeeding other = (DirectSeeding) obj;
		return Objects.equals(depth, other.depth) && Objects.equals(diameter, other.diameter)
				&& Objects.equals(timeToGerminate, other.timeToGerminate);
	}

	@Override
	public String toString() {
		return "DirectSeeding [depth=" + depth + ", diameter=" + diameter + ", timeToGerminate=" + timeToGerminate
				+ "]";
	}
}