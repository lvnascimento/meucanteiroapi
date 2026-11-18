package br.edu.ifrs.hortafacil.dto;

import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import br.edu.ifrs.hortafacil.model.management.planting.DirectSeeding;
import br.edu.ifrs.hortafacil.model.management.planting.PlantingMethod;

/**
 * DTO (Data Transfer Object) que representa o método de plantio por
 * semeadura direta ({@link DirectSeeding}) de forma adequada para
 * serialização (ex.: JSON) e transporte via API REST.
 * <p>
 * Assim como {@link QuantityDTO} e {@link QuantityIntervalDTO} fazem
 * para {@code Quantity} e {@code QuantityInterval}, esta classe
 * converte os atributos ricos de {@link DirectSeeding} (baseados na
 * API JSR-385) em seus equivalentes simplificados para transporte,
 * mantendo a mesma estrutura de dados: profundidade de plantio,
 * diâmetro e tempo até a germinação.
 * <p>
 * Estende {@link PlantingMethod} para poder ser usada de forma
 * polimórfica no lugar de um método de plantio, participando da
 * (de)serialização polimórfica configurada em {@code PlantingMethod}
 * (identificada pelo tipo {@code "DIRECT_SEEDING"}).
 */
public class DirectSeedingDTO extends PlantingMethod {

	/** Intervalo de profundidade recomendado para o plantio das sementes. */
	private QuantityIntervalDTO<Length> depth;

	/** Diâmetro recomendado da semente (ou da cova/sulco de plantio). */
	private QuantityDTO<Length> diameter;

	/** Intervalo de tempo esperado até a germinação das sementes. */
	private QuantityIntervalDTO<Time> timeToGerminate;

	/**
	 * Cria um DTO a partir de um objeto de domínio {@link DirectSeeding},
	 * convertendo cada atributo presente (não nulo) para sua
	 * representação em DTO correspondente.
	 * <p>
	 * Atributos ausentes (nulos) em {@code ds} permanecem como
	 * {@code null} no DTO resultante, em vez de lançar exceção.
	 *
	 * @param ds o método de plantio por semeadura direta de origem
	 */
	public DirectSeedingDTO(DirectSeeding ds) {
		if(ds.getDepth() != null)
			this.depth = new QuantityIntervalDTO<Length>(ds.getDepth());
		if(ds.getDiameter() != null)
			this.diameter = new QuantityDTO<Length>(ds.getDiameter());
		if(ds.getTimeToGerminate() != null)
			this.timeToGerminate = new QuantityIntervalDTO<Time>(ds.getTimeToGerminate());
	}

	/**
	 * Retorna o intervalo de profundidade de plantio das sementes.
	 *
	 * @return o intervalo de profundidade recomendado, ou {@code null}
	 *         caso não informado
	 */
	public QuantityIntervalDTO<Length> getDepth() {
		return depth;
	}

	/**
	 * Retorna o diâmetro recomendado da semente (ou da cova/sulco de
	 * plantio).
	 *
	 * @return o diâmetro recomendado, ou {@code null} caso não
	 *         informado
	 */
	public QuantityDTO<Length> getDiameter() {
		return diameter;
	}

	/**
	 * Retorna o intervalo de tempo esperado até a germinação das
	 * sementes.
	 *
	 * @return o intervalo de tempo até a germinação, ou {@code null}
	 *         caso não informado
	 */
	public QuantityIntervalDTO<Time> getTimeToGerminate() {
		return timeToGerminate;
	}
}