package br.edu.ifrs.hortafacil.model.harvest;

import java.util.List;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import br.edu.ifrs.hortafacil.model.management.Condition;

/**
 * Representa as informações de colheita de uma cultura da horta.
 * Armazena a descrição, o tempo necessário até a colheita em cada
 * estação (verão e inverno) e as condições associadas ao manejo.
 */
public class Harvest {

	// Descrição textual da colheita (ex.: orientações ou características gerais)
	private String description;

	// Tempo necessário até a colheita quando o plantio ocorre no verão
	private Quantity<Time> timeToHarvestSummer;

	// Tempo necessário até a colheita quando o plantio ocorre no inverno
	private Quantity<Time> timeToHarvestWinter;

	// Lista de condições (ex.: clima, solo, cuidados) que influenciam a colheita
	private List<Condition> conditions;

	/**
	 * Retorna a descrição da colheita.
	 *
	 * @return descrição da colheita
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Define a descrição da colheita.
	 *
	 * @param description nova descrição da colheita
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retorna o tempo até a colheita no verão.
	 *
	 * @return tempo até a colheita no verão
	 */
	public Quantity<Time> getTimeToHarvestSummer() {
		return timeToHarvestSummer;
	}

	/**
	 * Define o tempo até a colheita no verão.
	 *
	 * @param timeToHarvestSummer tempo até a colheita no verão
	 */
	public void setTimeToHarvestSummer(Quantity<Time> timeToHarvestSummer) {
		this.timeToHarvestSummer = timeToHarvestSummer;
	}

	/**
	 * Retorna o tempo até a colheita no inverno.
	 *
	 * @return tempo até a colheita no inverno
	 */
	public Quantity<Time> getTimeToHarvestWinter() {
		return timeToHarvestWinter;
	}

	/**
	 * Define o tempo até a colheita no inverno.
	 *
	 * @param timeToHarvestWinter tempo até a colheita no inverno
	 */
	public void setTimeToHarvestWinter(Quantity<Time> timeToHarvestWinter) {
		this.timeToHarvestWinter = timeToHarvestWinter;
	}

	/**
	 * Retorna as condições associadas à colheita.
	 *
	 * @return lista de condições
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * Define as condições associadas à colheita.
	 *
	 * @param conditions lista de condições
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	/**
	 * Gera uma representação em texto do objeto, útil para depuração e logs.
	 *
	 * @return string com os valores dos atributos da colheita
	 */
	@Override
	public String toString() {
		return "Harvest [description=" + description + ", timeToHarvestSummer=" + timeToHarvestSummer
				+ ", timeToHarvestWinter=" + timeToHarvestWinter + ", conditions=" + conditions + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(conditions, description, timeToHarvestSummer, timeToHarvestWinter);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Harvest other = (Harvest) obj;
		return Objects.equals(conditions, other.conditions) && Objects.equals(description, other.description)
				&& Objects.equals(timeToHarvestSummer, other.timeToHarvestSummer)
				&& Objects.equals(timeToHarvestWinter, other.timeToHarvestWinter);
	}
}