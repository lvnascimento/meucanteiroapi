package br.edu.ifrs.hortafacil.dto;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrs.hortafacil.model.management.FertilizationPractice;
import br.edu.ifrs.hortafacil.model.management.FertilizerAlternativeItem;
import br.edu.ifrs.hortafacil.model.management.ManagementPractice;

/**
 * DTO (Data Transfer Object) que representa uma prática de
 * fertilização ({@link FertilizationPractice}) de forma adequada para
 * serialização (ex.: JSON) e transporte via API REST.
 * <p>
 * Copia o rótulo e a descrição herdados de {@link ManagementPractice} e
 * converte a lista de alternativas de fertilizantes ({@link
 * FertilizerAlternativeItem}) para sua representação em DTO ({@link
 * FertilizerAlternativeItemDTO}), seguindo o mesmo padrão de {@link
 * FertilizerAlternativeItemDTO} e {@code DirectSeedingDTO}.
 * <p>
 * Estende {@link ManagementPractice} para poder ser usada de forma
 * polimórfica no lugar de uma prática de manejo.
 */
public class FertilizationPracticeDTO extends ManagementPractice {

	/** Itens de alternativas de fertilizantes recomendadas, em sua representação de DTO. */
	private List<FertilizerAlternativeItemDTO> items;

	/**
	 * Cria um DTO a partir de um objeto de domínio {@link
	 * FertilizationPractice}, copiando o rótulo e a descrição da
	 * prática e convertendo cada item de alternativa de fertilizante para
	 * {@link FertilizerAlternativeItemDTO}.
	 *
	 * @param fp a prática de fertilização de origem
	 */
	public FertilizationPracticeDTO(FertilizationPractice fp) {
		setLabel(fp.getLabel());
		setDescription(fp.getDescription());
		setAlternatives(fp.getAlternatives());
		items = new ArrayList<>();
		for(FertilizerAlternativeItem alt : fp.getItems()) {
			items.add(new FertilizerAlternativeItemDTO(alt));
		}
	}

	/**
	 * Retorna os itens das alternativas de fertilizantes recomendadas, em sua
	 * representação de DTO.
	 *
	 * @return a lista de itens desta prática de fertilização
	 */
	public List<FertilizerAlternativeItemDTO> getItems() {
		return items;
	}
}