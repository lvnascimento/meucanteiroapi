package br.edu.ifrs.hortafacil.model.prefs;

import java.util.Objects;

/**
 * Representa a "assinatura de preferências" de um cultivar, isto é, o
 * conjunto de condições ambientais e de manejo sob as quais o cultivar
 * se desenvolve melhor.
 * <p>
 * Inclui a faixa de pH e de temperatura adequadas, além de preferências
 * qualitativas de água, drenagem do solo e luminosidade. Essa
 * assinatura pode ser usada, por exemplo, para comparar ou agrupar
 * cultivares com necessidades ambientais semelhantes — por isso a
 * classe implementa {@code equals} e {@code hashCode} com base em
 * todos os seus atributos.
 */
public class CultivarPreferenceSignature {

	/** Valor mínimo da faixa de pH adequada ao cultivar. */
	private Double phMin;

	/** Valor máximo da faixa de pH adequada ao cultivar. */
    private Double phMax;

	/** Valor mínimo da faixa de temperatura adequada ao cultivar. */
    private Double tempMin;

	/** Valor máximo da faixa de temperatura adequada ao cultivar. */
    private Double tempMax;

	/** Preferência de água do cultivar (ex.: nível ou frequência de irrigação). */
    private String water;

	/** Preferência de drenagem do solo do cultivar. */
    private String soilDrenage;

	/** Preferência de luminosidade do cultivar (ex.: sol pleno, meia-sombra). */
    private String luminosity;

	/**
	 * Retorna o valor mínimo da faixa de pH adequada ao cultivar.
	 *
	 * @return o pH mínimo
	 */
	public Double getPhMin() {
		return phMin;
	}

	/**
	 * Define o valor mínimo da faixa de pH adequada ao cultivar.
	 *
	 * @param phMin o pH mínimo a ser atribuído
	 */
	public void setPhMin(Double phMin) {
		this.phMin = phMin;
	}

	/**
	 * Retorna o valor máximo da faixa de pH adequada ao cultivar.
	 *
	 * @return o pH máximo
	 */
	public Double getPhMax() {
		return phMax;
	}

	/**
	 * Define o valor máximo da faixa de pH adequada ao cultivar.
	 *
	 * @param phMax o pH máximo a ser atribuído
	 */
	public void setPhMax(Double phMax) {
		this.phMax = phMax;
	}

	/**
	 * Retorna o valor mínimo da faixa de temperatura adequada ao
	 * cultivar.
	 *
	 * @return a temperatura mínima
	 */
	public Double getTempMin() {
		return tempMin;
	}

	/**
	 * Define o valor mínimo da faixa de temperatura adequada ao
	 * cultivar.
	 *
	 * @param tempMin a temperatura mínima a ser atribuída
	 */
	public void setTempMin(Double tempMin) {
		this.tempMin = tempMin;
	}

	/**
	 * Retorna o valor máximo da faixa de temperatura adequada ao
	 * cultivar.
	 *
	 * @return a temperatura máxima
	 */
	public Double getTempMax() {
		return tempMax;
	}

	/**
	 * Define o valor máximo da faixa de temperatura adequada ao
	 * cultivar.
	 *
	 * @param tempMax a temperatura máxima a ser atribuída
	 */
	public void setTempMax(Double tempMax) {
		this.tempMax = tempMax;
	}

	/**
	 * Retorna a preferência de água do cultivar.
	 *
	 * @return a preferência de água
	 */
	public String getWater() {
		return water;
	}

	/**
	 * Define a preferência de água do cultivar.
	 *
	 * @param water a preferência de água a ser atribuída
	 */
	public void setWater(String water) {
		this.water = water;
	}

	/**
	 * Retorna a preferência de drenagem do solo do cultivar.
	 *
	 * @return a preferência de drenagem do solo
	 */
	public String getSoilDrenage() {
		return soilDrenage;
	}

	/**
	 * Define a preferência de drenagem do solo do cultivar.
	 *
	 * @param soilDrenage a preferência de drenagem do solo a ser
	 *                    atribuída
	 */
	public void setSoilDrenage(String soilDrenage) {
		this.soilDrenage = soilDrenage;
	}

	/**
	 * Retorna a preferência de luminosidade do cultivar.
	 *
	 * @return a preferência de luminosidade
	 */
	public String getLuminosity() {
		return luminosity;
	}

	/**
	 * Define a preferência de luminosidade do cultivar.
	 *
	 * @param luminosity a preferência de luminosidade a ser atribuída
	 */
	public void setLuminosity(String luminosity) {
		this.luminosity = luminosity;
	}

	/**
	 * Calcula o código hash com base em todos os atributos da
	 * assinatura de preferências, de forma consistente com
	 * {@link #equals(Object)}.
	 *
	 * @return o código hash da assinatura de preferências
	 */
	@Override
	public int hashCode() {
		return Objects.hash(luminosity, phMax, phMin, soilDrenage, tempMax, tempMin, water);
	}

	/**
	 * Compara esta assinatura de preferências com outro objeto,
	 * considerando-as iguais quando todos os atributos (pH mínimo e
	 * máximo, temperatura mínima e máxima, água, drenagem do solo e
	 * luminosidade) forem iguais.
	 *
	 * @param obj o objeto a ser comparado com esta assinatura
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

		CultivarPreferenceSignature other = (CultivarPreferenceSignature) obj;

		return Objects.equals(luminosity, other.luminosity) && Objects.equals(phMax, other.phMax)
				&& Objects.equals(phMin, other.phMin) && Objects.equals(soilDrenage, other.soilDrenage)
				&& Objects.equals(tempMax, other.tempMax) && Objects.equals(tempMin, other.tempMin)
				&& Objects.equals(water, other.water);
	}


}