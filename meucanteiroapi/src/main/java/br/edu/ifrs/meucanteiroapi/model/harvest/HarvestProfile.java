package br.edu.ifrs.meucanteiroapi.model.harvest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.edu.ifrs.meucanteiroapi.dto.HarvestDTO;
import br.edu.ifrs.meucanteiroapi.model.cultivars.CultivarID;

/**
 * Perfil de colheita: agrupa um conjunto de colheitas (já convertidas para
 * {@link HarvestDTO}) com as cultivares às quais elas se aplicam.
 * Permite representar que um mesmo perfil de colheita é compartilhado por
 * várias cultivares.
 */
public class HarvestProfile {

	// Lista de colheitas do perfil, na forma de DTOs (prontas para exposição)
	private List<HarvestDTO> harvests;

	// Conjunto de identificadores das cultivares que possuem este perfil de colheita
	private Set<CultivarID> cultivars;

	/**
	 * Constrói o perfil a partir de entidades {@link Harvest}, convertendo cada
	 * uma delas para {@link HarvestDTO}.
	 *
	 * @param harvests  lista de colheitas (entidades) que compõem o perfil
	 * @param cultivars conjunto de cultivares associadas a este perfil
	 */
	public HarvestProfile(List<Harvest> harvests, Set<CultivarID> cultivars) {
		// Converte cada entidade Harvest em um HarvestDTO
		List<HarvestDTO> dtoList = new ArrayList<>();
		for(Harvest h : harvests) {
			dtoList.add(new HarvestDTO(h));
		}
		this.harvests = dtoList;

		// O conjunto de cultivares é armazenado diretamente, sem cópia
		this.cultivars = cultivars;
	}

	/**
	 * Retorna as colheitas do perfil.
	 *
	 * @return lista de colheitas no formato DTO
	 */
	public List<HarvestDTO> getHarvests() {
		return harvests;
	}

	/**
	 * Define as colheitas do perfil.
	 *
	 * @param harvests lista de colheitas no formato DTO
	 */
	public void setHarvests(List<HarvestDTO> harvests) {
		this.harvests = harvests;
	}

	/**
	 * Retorna as cultivares associadas ao perfil.
	 *
	 * @return conjunto de identificadores de cultivares
	 */
	public Set<CultivarID> getCultivars() {
		return cultivars;
	}

	/**
	 * Define as cultivares associadas ao perfil.
	 *
	 * @param cultivars conjunto de identificadores de cultivares
	 */
	public void setCultivars(Set<CultivarID> cultivars) {
		this.cultivars = cultivars;
	}
}