package br.edu.ifrs.meucanteiroapi.model.management.planting;

import java.util.List;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import br.edu.ifrs.meucanteiroapi.model.management.ManagementPractice;

/**
 * Representa a "assinatura de plantio" de um cultivar, reunindo as
 * recomendações de espaçamento, os métodos de plantio possíveis e as
 * práticas de manejo associadas.
 * <p>
 * Inclui meses para plantio, o espaçamento entre linhas e entre plantas, a lista de
 * métodos de plantio recomendados (ex.: semeadura direta, transplante
 * de mudas — ver {@link PlantingMethod}) e a lista de práticas de
 * manejo relacionadas (ex.: fertilização — ver {@link
 * ManagementPractice}). Assim como em {@link
 * br.edu.ifrs.hortafacil.model.CultivarPreferenceSignature}, essa
 * assinatura pode ser usada para comparar ou agrupar cultivares com
 * recomendações de plantio semelhantes, por isso implementa {@code
 * equals} e {@code hashCode} com base em todos os seus atributos.
 */
public class CultivarPlantingSignature {

	/** Meses recomendados para plantio na região informada. */
	private List<Integer> monthsToPlant;
	
	/** Espaçamento recomendado entre linhas de plantio. */
	private Quantity<Length> rowSpacing;

	/** Espaçamento recomendado entre plantas, dentro de uma mesma linha. */
	private Quantity<Length> plantSpacing;

	/** Métodos de plantio recomendados para o cultivar. */
	private List<PlantingMethod> methods;

	/** Práticas de manejo associadas ao cultivar. */
	private List<ManagementPractice> practices;

	/**
	 * Retorna a listagem de meses quando se recomenda plantar o cultivar.
	 *
	 * @return a listagem de meses
	 */
	public List<Integer> getMonthsToPlant() {
		return monthsToPlant;
	}

	/**
	 * Define a listagem de meses quando se recomenda plantar o cultivar.
	 *
	 * @param monthsToPlant a listagem de meses a ser atribuída
	 */
	public void setMonthsToPlant(List<Integer> monthsToPlant) {
		this.monthsToPlant = monthsToPlant;
	}

	/**
	 * Retorna o espaçamento recomendado entre linhas de plantio.
	 *
	 * @return o espaçamento entre linhas
	 */
	public Quantity<Length> getRowSpacing() {
		return rowSpacing;
	}

	/**
	 * Define o espaçamento recomendado entre linhas de plantio.
	 *
	 * @param rowSpacing o espaçamento entre linhas a ser atribuído
	 */
	public void setRowSpacing(Quantity<Length> rowSpacing) {
		this.rowSpacing = rowSpacing;
	}

	/**
	 * Retorna o espaçamento recomendado entre plantas.
	 *
	 * @return o espaçamento entre plantas
	 */
	public Quantity<Length> getPlantSpacing() {
		return plantSpacing;
	}

	/**
	 * Define o espaçamento recomendado entre plantas.
	 *
	 * @param plantSpacing o espaçamento entre plantas a ser atribuído
	 */
	public void setPlantSpacing(Quantity<Length> plantSpacing) {
		this.plantSpacing = plantSpacing;
	}

	/**
	 * Retorna os métodos de plantio recomendados para o cultivar.
	 *
	 * @return a lista de métodos de plantio
	 */
	public List<PlantingMethod> getMethods() {
		return methods;
	}

	/**
	 * Define os métodos de plantio recomendados para o cultivar.
	 *
	 * @param methods a lista de métodos de plantio a ser atribuída
	 */
	public void setMethods(List<PlantingMethod> methods) {
		this.methods = methods;
	}

	/**
	 * Retorna as práticas de manejo associadas ao cultivar.
	 *
	 * @return a lista de práticas de manejo
	 */
	public List<ManagementPractice> getPractices() {
		return practices;
	}

	/**
	 * Define as práticas de manejo associadas ao cultivar.
	 *
	 * @param practices a lista de práticas de manejo a ser atribuída
	 */
	public void setPractices(List<ManagementPractice> practices) {
		this.practices = practices;
	}

	/**
	 * Calcula o código hash com base em todos os atributos da
	 * assinatura de plantio, de forma consistente com
	 * {@link #equals(Object)}.
	 *
	 * @return o código hash desta assinatura de plantio
	 */
	@Override
	public int hashCode() {
		return Objects.hash(methods, plantSpacing, practices, rowSpacing);
	}

	/**
	 * Compara esta assinatura de plantio com outro objeto,
	 * considerando-as iguais quando espaçamento entre linhas, entre
	 * plantas, métodos de plantio e práticas de manejo forem todos
	 * iguais.
	 *
	 * @param obj o objeto a ser comparado com esta assinatura de
	 *            plantio
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
		CultivarPlantingSignature other = (CultivarPlantingSignature) obj;
		return Objects.equals(methods, other.methods) && Objects.equals(plantSpacing, other.plantSpacing)
				&& Objects.equals(practices, other.practices) && Objects.equals(rowSpacing, other.rowSpacing);
	}

	@Override
	public String toString() {
		return "CultivarPlantingSignature [monthsToPlant=" + monthsToPlant + ", rowSpacing=" + rowSpacing
				+ ", plantSpacing=" + plantSpacing + ", methods=" + methods + ", practices=" + practices + "]";
	}
}