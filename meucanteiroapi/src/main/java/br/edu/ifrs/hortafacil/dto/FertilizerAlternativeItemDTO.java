package br.edu.ifrs.hortafacil.dto;

import br.edu.ifrs.hortafacil.model.general.MassPerArea;
import br.edu.ifrs.hortafacil.model.management.FertilizerAlternativeItem;

/**
 * DTO (Data Transfer Object) que representa um item de uma alternativa de
 * fertilização ({@link FertilizerAlternativeItem}) de forma adequada para
 * serialização (ex.: JSON) e transporte via API REST.
 * <p>
 * Assim como {@link DirectSeedingDTO} faz para {@code DirectSeeding},
 * esta classe converte os atributos ricos de {@link
 * FertilizerAlternativeItem} (baseados na API JSR-385) em seus
 * equivalentes simplificados para transporte: o nome do fertilizante é
 * mantido como está, e a quantidade recomendada por área é convertida
 * para {@link QuantityDTO}.
 */
public class FertilizerAlternativeItemDTO {

	/** Nome do fertilizante. */
	private String fertilizerName;

	/** Quantidade recomendada do fertilizante, por unidade de área. */
	private QuantityDTO<MassPerArea> quantity;

	/**
	 * Cria um DTO a partir de um objeto de domínio {@link
	 * FertilizerAlternativeItem}, copiando o nome do fertilizante e
	 * convertendo a quantidade recomendada para {@link QuantityDTO}.
	 *
	 * @param alt a alternativa de fertilização de origem
	 */
	public FertilizerAlternativeItemDTO(FertilizerAlternativeItem alt) {
		this.fertilizerName = alt.getFertilizerName();
		this.quantity = new QuantityDTO<MassPerArea>(alt.getQuantity());
	}

	/**
	 * Retorna o nome do fertilizante.
	 *
	 * @return o nome do fertilizante
	 */
	public String getFertilizerName() {
		return fertilizerName;
	}

	/**
	 * Retorna a quantidade recomendada do fertilizante, por unidade de
	 * área.
	 *
	 * @return a quantidade de massa por área recomendada
	 */
	public QuantityDTO<MassPerArea> getQuantity() {
		return quantity;
	}
}