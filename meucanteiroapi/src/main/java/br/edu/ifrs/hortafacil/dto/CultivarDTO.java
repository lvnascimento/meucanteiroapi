package br.edu.ifrs.hortafacil.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.edu.ifrs.hortafacil.model.cultivars.Cultivar;
import br.edu.ifrs.hortafacil.model.cultivars.CultivarID;
import br.edu.ifrs.hortafacil.model.harvest.Harvest;
import br.edu.ifrs.hortafacil.model.management.Management;
import br.edu.ifrs.hortafacil.model.prefs.CultivarPreferenceSignature;

/**
 * DTO (objeto de transferência de dados) que representa uma cultivar na API.
 * <p>
 * Reúne, em um único objeto somente leitura, a identificação da cultivar, suas
 * preferências, as informações de plantio, os manejos e as colheitas. Ele é
 * construído a partir da entidade {@link Cultivar} e não possui setters: depois
 * de criado, não é alterado.
 */
public class CultivarDTO {

	/** Identificação da cultivar (URI do recurso e nome). */
	private final CultivarID description;

	/** Preferências da cultivar: temperatura, pH, luz, água e drenagem. */
	private final CultivarPreferenceSignature preferences;

	/** Informações de plantio: meses, espaçamentos, métodos e práticas. */
	private final CultivarPlantingSignatureDTO plantingInfo;

	/**
	 * Manejos aplicáveis à cultivar. A ordem (por estágio e período) é a mesma
	 * da entidade de origem; este DTO não reordena a lista.
	 */
	private final List<Management> management;

	/** Informações de colheita aplicáveis à cultivar. */
	private final List<HarvestDTO> harvest;

	/**
	 * Cria o DTO a partir de uma cultivar do modelo.
	 *
	 * @param c cultivar de origem; não pode ser nula
	 * @throws NullPointerException se {@code c} for nula
	 */
	public CultivarDTO(Cultivar c) {
		Objects.requireNonNull(c, "A cultivar de origem não pode ser nula");

		this.description = c.getDescription();
		this.preferences = c.getPreferences();

		// Plantio ausente na entidade resulta em null no DTO, em vez de NullPointerException.
		this.plantingInfo = c.getPlantingInfo() == null
				? null
				: new CultivarPlantingSignatureDTO(c.getPlantingInfo());

		// Cópia defensiva: o DTO não compartilha a lista com a entidade, e a lista
		// exposta é somente leitura. Sem manejos, devolve uma lista vazia.
		List<Management> managementSource = c.getManagement();
		this.management = managementSource == null
				? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(managementSource));

		// Converte cada colheita da entidade em seu respectivo DTO.
		List<HarvestDTO> harvestDtos = new ArrayList<>();
		if (c.getHarvest() != null) {
			for (Harvest h : c.getHarvest()) {
				harvestDtos.add(new HarvestDTO(h));
			}
		}
		this.harvest = Collections.unmodifiableList(harvestDtos);
	}

	/** @return identificação da cultivar (URI do recurso e nome) */
	public CultivarID getDescription() {
		return description;
	}

	/** @return preferências da cultivar (temperatura, pH, luz, água e drenagem) */
	public CultivarPreferenceSignature getPreferences() {
		return preferences;
	}

	/** @return informações de plantio, ou {@code null} se a cultivar não tiver */
	public CultivarPlantingSignatureDTO getPlantingInfo() {
		return plantingInfo;
	}

	/** @return manejos da cultivar (lista somente leitura, nunca nula) */
	public List<Management> getManagement() {
		return management;
	}

	/** @return colheitas da cultivar (lista somente leitura, nunca nula) */
	public List<HarvestDTO> getHarvest() {
		return harvest;
	}
}
