package br.edu.ifrs.hortafacil.model.cultivars;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrs.hortafacil.model.harvest.Harvest;
import br.edu.ifrs.hortafacil.model.management.Management;
import br.edu.ifrs.hortafacil.model.management.planting.CultivarPlantingSignature;
import br.edu.ifrs.hortafacil.model.prefs.CultivarPreferenceSignature;

/**
 * Modelo de domínio que representa uma cultivar de um produto (cultura ou
 * espécie vegetal), reunindo sua identificação, preferências, informações de
 * plantio e manejos.
 * <p>
 * É um objeto simples de transferência de dados, montado a partir da base de
 * conhecimento RDF (ver {@code CultivarsDAO}). Qualquer um dos componentes pode
 * ser {@code null} caso o dado não exista na ontologia.
 */
public class Cultivar {

	/** Identificação da cultivar (URI do recurso e nome). */
	private CultivarID description;

	/** Preferências da cultivar: temperatura, pH, luz, água e drenagem. */
	private CultivarPreferenceSignature preferences;

	/** Informações de plantio: meses, espaçamentos, métodos e práticas. */
	private CultivarPlantingSignature plantingInfo;

	/** Manejos aplicáveis à cultivar, ordenados por estágio e período. */
	private List<Management> management;
	
	/** Informações de colheita aplicáveis à cultivar. */
	private List<Harvest> harvest;

	/**
	 * @return a identificação da cultivar (URI do recurso e nome)
	 */
	public CultivarID getDescription() {
		return description;
	}

	/**
	 * @param description a identificação da cultivar (URI do recurso e nome)
	 */
	public void setDescription(CultivarID description) {
		this.description = description;
	}

	/**
	 * @return as preferências da cultivar, ou {@code null} se não definidas
	 */
	public CultivarPreferenceSignature getPreferences() {
		return preferences;
	}

	/**
	 * @param preferences as preferências da cultivar
	 */
	public void setPreferences(CultivarPreferenceSignature preferences) {
		this.preferences = preferences;
	}

	/**
	 * @return as informações de plantio da cultivar, ou {@code null} se não
	 *         definidas
	 */
	public CultivarPlantingSignature getPlantingInfo() {
		return plantingInfo;
	}

	/**
	 * @param plantingInfo as informações de plantio da cultivar
	 */
	public void setPlantingInfo(CultivarPlantingSignature plantingInfo) {
		this.plantingInfo = plantingInfo;
	}

	/**
	 * @return os manejos da cultivar, ou {@code null} se não definidos
	 */
	public List<Management> getManagement() {
		return management;
	}

	/**
	 * Define os manejos da cultivar.
	 * <p>
	 * Guarda uma cópia da lista recebida (cópia rasa: os itens são os mesmos).
	 * Isso evita que várias cultivares compartilhem a mesma instância de lista,
	 * o que acontece quando o DAO reaproveita o resultado de uma consulta, e
	 * que uma alteração em uma delas afete as outras.
	 *
	 * @param management os manejos da cultivar; pode ser {@code null}
	 */
	public void setManagement(List<Management> management) {
		this.management = management != null ? new ArrayList<>(management) : null;
	}
	
	/**
	 * @return as informações de colheita da cultivar, ou {@code null} se não
	 *         definidas
	 */
	public List<Harvest> getHarvest() {
		return harvest;
	}

	/**
	 * @param harvest as informações de colheita da cultivar
	 */
	public void setHarvest(List<Harvest> harvest) {
		this.harvest = harvest;
	}

	@Override
	public String toString() {
		return "Cultivar [description=" + description + ", preferences=" + preferences + ", plantingInfo="
				+ plantingInfo + ", management=" + management + ", harvest=" + harvest + "]";
	}
}