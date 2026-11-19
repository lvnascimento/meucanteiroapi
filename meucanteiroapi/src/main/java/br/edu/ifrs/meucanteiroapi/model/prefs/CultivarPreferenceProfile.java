package br.edu.ifrs.meucanteiroapi.model.prefs;

import java.util.Set;

import br.edu.ifrs.meucanteiroapi.model.cultivars.CultivarID;

/**
 * Representa um perfil de preferências de cultivo, agrupando um
 * conjunto de condições ambientais e de manejo ({@link
 * CultivarPreferenceSignature}) e o conjunto de cultivares
 * (identificados por {@link CultivarID}) que compartilham essa mesma
 * assinatura de preferências.
 * <p>
 * É útil, por exemplo, para agrupar cultivares com necessidades
 * ambientais semelhantes (mesma faixa de pH, temperatura, água,
 * drenagem do solo e luminosidade) em um único perfil.
 */
public class CultivarPreferenceProfile {

	/** Assinatura de preferências (condições ambientais e de manejo) do perfil. */
	private CultivarPreferenceSignature preferences;

	/** Conjunto de cultivares que compartilham esta assinatura de preferências. */
	private Set<CultivarID> cultivars;

	/**
	 * Cria um perfil de preferências a partir de uma assinatura de
	 * preferências e do conjunto de cultivares associados a ela.
	 *
	 * @param preferences a assinatura de preferências do perfil
	 * @param cultivars   o conjunto de cultivares associados a essa
	 *                    assinatura de preferências
	 */
	public CultivarPreferenceProfile(CultivarPreferenceSignature preferences, Set<CultivarID> cultivars) {
		super();
		this.preferences = preferences;
		this.cultivars = cultivars;
	}

	/**
	 * Retorna a assinatura de preferências do perfil.
	 *
	 * @return a assinatura de preferências (condições ambientais e de
	 *         manejo)
	 */
	public CultivarPreferenceSignature getPreferences() {
		return preferences;
	}

	/**
	 * Define a assinatura de preferências do perfil.
	 *
	 * @param preferences a assinatura de preferências a ser atribuída
	 */
	public void setPreferences(CultivarPreferenceSignature preferences) {
		this.preferences = preferences;
	}

	/**
	 * Retorna o conjunto de cultivares associados a este perfil de
	 * preferências.
	 *
	 * @return o conjunto de identificadores de cultivares
	 */
	public Set<CultivarID> getCultivars() {
		return cultivars;
	}

	/**
	 * Define o conjunto de cultivares associados a este perfil de
	 * preferências.
	 *
	 * @param cultivars o conjunto de identificadores de cultivares a
	 *                  ser atribuído
	 */
	public void setCultivars(Set<CultivarID> cultivars) {
		this.cultivars = cultivars;
	}
}