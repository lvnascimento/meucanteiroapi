package br.edu.ifrs.meucanteiroapi.dto;

import javax.measure.Quantity;

import br.edu.ifrs.meucanteiroapi.model.general.QuantityInterval;

/**
 * DTO (Data Transfer Object) que representa um intervalo de quantidades
 * de forma simplificada, adequada para serialização (ex.: JSON) e
 * transporte via API REST.
 * <p>
 * Diferente de {@link QuantityInterval}, que mantém os valores como
 * objetos {@link Quantity} (mais ricos, porém menos triviais de
 * serializar), esta classe expõe apenas valores numéricos primitivos
 * (min e max) e a unidade de medida como texto, facilitando o consumo
 * por clientes da API.
 *
 * @param <T> o tipo de grandeza física do intervalo original (ex.:
 *            {@code Length}, {@code Mass}, {@code Temperature}),
 *            usado apenas para tipar o {@link QuantityInterval} de origem
 */
public class QuantityIntervalDTO<T extends Quantity<T>> {

	/** Valor mínimo do intervalo, já convertido para {@code double}. */
	private double min;

	/** Valor máximo do intervalo, já convertido para {@code double}. */
	private double max;

	/** Representação textual da unidade de medida do intervalo. */
	private String unit;

	/**
	 * Cria um DTO a partir de um {@link QuantityInterval}, extraindo os
	 * valores numéricos mínimo e máximo e a unidade de medida associada.
	 * <p>
	 * A unidade é obtida a partir da quantidade mínima, assumindo que
	 * ambos os limites do intervalo (min e max) compartilham a mesma
	 * unidade.
	 *
	 * @param qi o intervalo de quantidades de origem
	 */
	public QuantityIntervalDTO(QuantityInterval<T> qi) {
		this.min = qi.getMin().getValue().doubleValue();
		this.max = qi.getMax().getValue().doubleValue();
		this.unit = qi.getMin().getUnit().toString();
	}

	/**
	 * Retorna o valor mínimo do intervalo.
	 *
	 * @return o valor mínimo, como {@code double}
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Retorna o valor máximo do intervalo.
	 *
	 * @return o valor máximo, como {@code double}
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Retorna a unidade de medida do intervalo.
	 *
	 * @return a representação textual da unidade de medida
	 */
	public String getUnit() {
		return unit;
	}
}