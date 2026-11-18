package br.edu.ifrs.hortafacil.model.management.planting;

import java.util.Set;

import br.edu.ifrs.hortafacil.dto.CultivarPlantingSignatureDTO;
import br.edu.ifrs.hortafacil.model.cultivars.CultivarID;

/**
 * Representa um perfil de plantio, agrupando um conjunto de
 * recomendações de plantio ({@link CultivarPlantingSignatureDTO}) e o
 * conjunto de cultivares (identificados por {@link CultivarID}) que
 * compartilham essas mesmas recomendações.
 * <p>
 * Segue o mesmo padrão de {@code CultivarPreferenceProfile}, mas para
 * agrupar cultivares com recomendações de plantio semelhantes
 * (espaçamento, métodos de plantio e práticas de manejo) em vez de
 * preferências ambientais.
 */
public class CultivarPlantingProfile {

	/** Informações de plantio (espaçamento, métodos e práticas) do perfil. */
	private CultivarPlantingSignatureDTO plantingInfo;

	/** Conjunto de cultivares que compartilham estas informações de plantio. */
	private Set<CultivarID> cultivars;

	/**
	 * Cria um perfil de plantio a partir das informações de plantio e
	 * do conjunto de cultivares associados a elas.
	 *
	 * @param plantingInfo as informações de plantio do perfil
	 * @param cultivars    o conjunto de cultivares associados a essas
	 *                      informações de plantio
	 */
	public CultivarPlantingProfile(CultivarPlantingSignatureDTO plantingInfo, Set<CultivarID> cultivars) {
		super();
		this.plantingInfo = plantingInfo;
		this.cultivars = cultivars;
	}

	/**
	 * Retorna as informações de plantio do perfil.
	 *
	 * @return as informações de plantio (espaçamento, métodos e
	 *         práticas)
	 */
	public CultivarPlantingSignatureDTO getPlantingInfo() {
		return plantingInfo;
	}

	/**
	 * Define as informações de plantio do perfil.
	 *
	 * @param plantingInfo as informações de plantio a serem atribuídas
	 */
	public void setPlantingInfo(CultivarPlantingSignatureDTO plantingInfo) {
		this.plantingInfo = plantingInfo;
	}

	/**
	 * Retorna o conjunto de cultivares associados a este perfil de
	 * plantio.
	 *
	 * @return o conjunto de identificadores de cultivares
	 */
	public Set<CultivarID> getCultivars() {
		return cultivars;
	}

	/**
	 * Define o conjunto de cultivares associados a este perfil de
	 * plantio.
	 *
	 * @param cultivars o conjunto de identificadores de cultivares a
	 *                  ser atribuído
	 */
	public void setCultivars(Set<CultivarID> cultivars) {
		this.cultivars = cultivars;
	}
}