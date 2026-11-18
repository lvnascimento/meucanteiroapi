package br.edu.ifrs.hortafacil.model.general;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.Unit;

import tech.units.indriya.quantity.Quantities;

/**
 * Representa um intervalo de quantidades (valor mínimo e valor máximo)
 * associadas a uma mesma grandeza física, utilizando a API JSR-385
 * (Units of Measurement) para garantir a consistência das unidades.
 * <p>
 * O parâmetro de tipo {@code T} define a dimensão física da grandeza
 * (ex.: comprimento, massa, temperatura), permitindo que o compilador
 * verifique em tempo de compilação que os valores mínimo e máximo são
 * compatíveis entre si.
 *
 * @param <T> o tipo de grandeza física do intervalo (ex.: {@code Length},
 *            {@code Mass}, {@code Temperature})
 */
public class QuantityInterval<T extends Quantity<T>> {

	/** Valor mínimo do intervalo. */
	private Quantity<T> min;

	/** Valor máximo do intervalo. */
	private Quantity<T> max;

	/**
	 * Cria um intervalo de quantidades a partir de dois objetos
	 * {@link Quantity} já construídos.
	 *
	 * @param min a quantidade mínima do intervalo
	 * @param max a quantidade máxima do intervalo
	 */
	public QuantityInterval(Quantity<T> min, Quantity<T> max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * Cria um intervalo de quantidades a partir de valores numéricos e
	 * de uma unidade de medida comum, construindo internamente os
	 * objetos {@link Quantity} correspondentes.
	 *
	 * @param min  o valor numérico mínimo do intervalo
	 * @param max  o valor numérico máximo do intervalo
	 * @param unit a unidade de medida associada aos valores mínimo e máximo
	 */
	public QuantityInterval(double min, double max, Unit<T> unit) {
		this(Quantities.getQuantity(min, unit), Quantities.getQuantity(max, unit));
	}

	/**
	 * Retorna o valor mínimo do intervalo.
	 *
	 * @return a quantidade mínima do intervalo
	 */
	public Quantity<T> getMin() {
		return min;
	}

	/**
	 * Retorna o valor máximo do intervalo.
	 *
	 * @return a quantidade máxima do intervalo
	 */
	public Quantity<T> getMax() {
		return max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(max, min);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		QuantityInterval<T> other = (QuantityInterval<T>) obj;
		return Objects.equals(max, other.max) && Objects.equals(min, other.min);
	}
}