package br.edu.ifrs.hortafacil.model.product;

import java.util.List;

import br.edu.ifrs.hortafacil.model.harvest.HarvestProfile;
import br.edu.ifrs.hortafacil.model.management.ManagementProfile;
import br.edu.ifrs.hortafacil.model.management.planting.CultivarPlantingProfile;
import br.edu.ifrs.hortafacil.model.prefs.CultivarPreferenceProfile;

/**
 * Perfil completo de um produto da horta. Reúne, em um único objeto, os
 * diferentes perfis que descrevem o produto: preferências, plantio, manejo
 * e colheita. Cada perfil é uma lista, pois cada aspecto pode variar entre
 * grupos de cultivares do mesmo produto.
 */
public class ProductProfile {

	// Perfis de preferência (ex.: preferências de temperatura e acidez de solo relacionadas às cultivares)
	private List<CultivarPreferenceProfile> preferencesProfile;

	// Perfis de plantio (ex.: orientações de como e quando plantar cada cultivar)
	private List<CultivarPlantingProfile> plantingProfile;

	// Perfis de manejo (ex.: cuidados durante o cultivo)
	private List<ManagementProfile> managementProfile;

	// Perfis de colheita (ex.: tempo até a colheita e condições associadas)
	private List<HarvestProfile> harvestProfile;
		
	/**
	 * Retorna os perfis de preferência das cultivares.
	 *
	 * @return lista de perfis de preferência
	 */
	public List<CultivarPreferenceProfile> getPreferencesProfile() {
		return preferencesProfile;
	}

	/**
	 * Define os perfis de preferência das cultivares.
	 *
	 * @param preferencesProfile lista de perfis de preferência
	 */
	public void setPreferencesProfile(List<CultivarPreferenceProfile> preferencesProfile) {
		this.preferencesProfile = preferencesProfile;
	}

	/**
	 * Retorna os perfis de plantio das cultivares.
	 *
	 * @return lista de perfis de plantio
	 */
	public List<CultivarPlantingProfile> getPlantingProfile() {
		return plantingProfile;
	}

	/**
	 * Define os perfis de plantio das cultivares.
	 *
	 * @param plantingProfile lista de perfis de plantio
	 */
	public void setPlantingProfile(List<CultivarPlantingProfile> plantingProfile) {
		this.plantingProfile = plantingProfile;
	}

	/**
	 * Retorna os perfis de manejo do produto.
	 *
	 * @return lista de perfis de manejo
	 */
	public List<ManagementProfile> getManagementProfile() {
		return managementProfile;
	}

	/**
	 * Define os perfis de manejo do produto.
	 *
	 * @param managementProfile lista de perfis de manejo
	 */
	public void setManagementProfile(List<ManagementProfile> managementProfile) {
		this.managementProfile = managementProfile;
	}

	/**
	 * Retorna os perfis de colheita do produto.
	 *
	 * @return lista de perfis de colheita
	 */
	public List<HarvestProfile> getHarvestProfile() {
		return harvestProfile;
	}

	/**
	 * Define os perfis de colheita do produto.
	 *
	 * @param harvestProfile lista de perfis de colheita
	 */
	public void setHarvestProfile(List<HarvestProfile> harvestProfile) {
		this.harvestProfile = harvestProfile;
	}
}