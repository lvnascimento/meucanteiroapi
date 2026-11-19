package br.edu.ifrs.meucanteiroapi.dto;

import javax.measure.Quantity;

/**
 * DTO (Data Transfer Object) que representa uma quantidade de forma
 * simplificada, adequada para serialização (ex.: JSON) e transporte
 * via API REST.
 * <p>
 * Diferente de {@link Quantity}, que mantém o valor e a unidade como
 * objetos ricos da API JSR-385 (Units of Measurement), esta classe
 * expõe apenas um valor numérico primitivo e a unidade de medida como
 * texto, facilitando o consumo por clientes da API.
 *
 * @param <T> o tipo de grandeza física da quantidade original (ex.:
 *            {@code Length}, {@code Mass}, {@code Temperature}),
 *            usado apenas para tipar a {@link Quantity} de origem
 */
public class QuantityDTO<T extends Quantity<T>> {

	/** Valor numérico da quantidade, já convertido para {@code double}. */
	private double value;

	/** Representação textual da unidade de medida da quantidade. */
	private String unit;

	/**
	 * Cria um DTO a partir de uma {@link Quantity}, extraindo o valor
	 * numérico e a unidade de medida associada.
	 * <p>
	 * Caso {@code quantity} seja {@code null}, os campos {@code value} e
	 * {@code unit} permanecem com seus valores padrão (0.0 e
	 * {@code null}, respectivamente).
	 *
	 * @param quantity a quantidade de origem, podendo ser {@code null}
	 */
	public QuantityDTO(Quantity<T> quantity) {
		if(quantity != null) {
			value = quantity.getValue().doubleValue();
			unit = quantity.getUnit().toString();
		}
	}

	/**
	 * Retorna o valor numérico da quantidade.
	 *
	 * @return o valor da quantidade, como {@code double}
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Retorna a unidade de medida da quantidade.
	 *
	 * @return a representação textual da unidade de medida
	 */
	public String getUnit() {
		return unit;
	}
}