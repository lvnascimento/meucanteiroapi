package br.edu.ifrs.hortafacil.model.management;

import java.util.Objects;

import javax.measure.Quantity;

import br.edu.ifrs.hortafacil.model.general.MassPerArea;

/**
 * Representa um item de uma alternativa de adubação/fertilização, associando o
 * nome de um fertilizante à quantidade recomendada por unidade de área
 * (ex.: kg/ha, g/m²), usando a grandeza customizada {@link
 * MassPerArea}.
 * <p>
 * É útil para expressar, por exemplo, diferentes fertilizantes que
 * podem ser usados de forma equivalente em uma mesma recomendação de
 * adubação, cada um com sua respectiva dosagem por área. Implementa
 * {@code equals} e {@code hashCode} com base no nome do fertilizante e
 * na quantidade, permitindo comparar ou deduplicar alternativas.
 */
public class FertilizerAlternativeItem {

	/** Nome do fertilizante (ex.: "Ureia", "Esterco bovino"). */
	private String fertilizerName;

	/** Quantidade recomendada do fertilizante, por unidade de área. */
	private Quantity<MassPerArea> quantity;

	/**
	 * Retorna o nome do fertilizante.
	 *
	 * @return o nome do fertilizante
	 */
	public String getFertilizerName() {
		return fertilizerName;
	}

	/**
	 * Define o nome do fertilizante.
	 *
	 * @param fertilizerName o nome a ser atribuído ao fertilizante
	 */
	public void setFertilizerName(String fertilizerName) {
		this.fertilizerName = fertilizerName;
	}

	/**
	 * Retorna a quantidade recomendada do fertilizante, por unidade de
	 * área.
	 *
	 * @return a quantidade de massa por área recomendada
	 */
	public Quantity<MassPerArea> getQuantity() {
		return quantity;
	}

	/**
	 * Define a quantidade recomendada do fertilizante, por unidade de
	 * área.
	 *
	 * @param quantity a quantidade de massa por área a ser atribuída
	 */
	public void setQuantity(Quantity<MassPerArea> quantity) {
		this.quantity = quantity;
	}

	/**
	 * Calcula o código hash com base no nome do fertilizante e na
	 * quantidade, de forma consistente com {@link #equals(Object)}.
	 *
	 * @return o código hash desta alternativa de fertilização
	 */
	@Override
	public int hashCode() {
		return Objects.hash(fertilizerName, quantity);
	}

	/**
	 * Compara esta alternativa de fertilização com outro objeto,
	 * considerando-as iguais quando o nome do fertilizante e a
	 * quantidade forem iguais.
	 *
	 * @param obj o objeto a ser comparado com esta alternativa
	 * @return {@code true} caso os objetos sejam considerados iguais;
	 *         {@code false} caso contrário
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FertilizerAlternativeItem other = (FertilizerAlternativeItem) obj;
		return Objects.equals(fertilizerName, other.fertilizerName) && Objects.equals(quantity, other.quantity);
	}

	@Override
	public String toString() {
		return "FertilizerAlternativeItem [fertilizerName=" + fertilizerName + ", quantity=" + quantity + "]";
	}
}