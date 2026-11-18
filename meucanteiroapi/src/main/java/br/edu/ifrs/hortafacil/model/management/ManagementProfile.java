package br.edu.ifrs.hortafacil.model.management;

import java.util.List;
import java.util.Set;

import br.edu.ifrs.hortafacil.model.cultivars.CultivarID;

/**
 * Modelo de domínio que associa uma lista de manejos ({@link Management}) ao
 * conjunto de cultivares a que esses manejos se aplicam.
 * <p>
 * É um objeto simples de transferência de dados. As coleções são mantidas por
 * referência, ou seja, o objeto não faz cópias delas: alterar uma coleção
 * depois de entregá-la ao perfil altera o próprio perfil.
 */
public class ManagementProfile {

	/** Manejos que compõem o perfil. */
	private List<Management> managements;

	/** Cultivares (identificação) às quais o perfil se aplica. */
	private Set<CultivarID> cultivars;

	/**
	 * Cria um perfil de manejo.
	 *
	 * @param managements os manejos que compõem o perfil
	 * @param cultivars   as cultivares às quais o perfil se aplica
	 */
	public ManagementProfile(List<Management> managements, Set<CultivarID> cultivars) {
		this.managements = managements;
		this.cultivars = cultivars;
	}

	/**
	 * @return os manejos que compõem o perfil
	 */
	public List<Management> getManagements() {
		return managements;
	}

	/**
	 * @param managements os manejos que compõem o perfil
	 */
	public void setManagements(List<Management> managements) {
		this.managements = managements;
	}

	/**
	 * @return as cultivares às quais o perfil se aplica
	 */
	public Set<CultivarID> getCultivars() {
		return cultivars;
	}

	/**
	 * @param cultivars as cultivares às quais o perfil se aplica
	 */
	public void setCultivars(Set<CultivarID> cultivars) {
		this.cultivars = cultivars;
	}
}