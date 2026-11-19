package br.edu.ifrs.meucanteiroapi.dto;

import java.util.List;

import javax.measure.quantity.Time;

import br.edu.ifrs.meucanteiroapi.model.harvest.Harvest;
import br.edu.ifrs.meucanteiroapi.model.management.Condition;

/**
 * DTO (Data Transfer Object) da colheita.
 * Representa uma versão somente leitura de {@link Harvest}, usada para
 * transportar os dados entre camadas (por exemplo, para a resposta de uma API),
 * sem expor diretamente a entidade do modelo.
 */
public class HarvestDTO {

	// Descrição textual da colheita
	private String description;

	// Tempo até a colheita no verão, já convertido para o DTO de quantidade
	private QuantityDTO<Time> timeToHarvestSummer;

	// Tempo até a colheita no inverno, já convertido para o DTO de quantidade
	private QuantityDTO<Time> timeToHarvestWinter;

	// Condições associadas à colheita (cópia imutável da lista original)
	private List<Condition> conditions;

	/**
	 * Constrói o DTO a partir de uma entidade {@link Harvest}.
	 * Os campos opcionais são tratados para evitar {@code NullPointerException}:
	 * tempos nulos permanecem nulos e a lista de condições nula vira uma lista vazia.
	 *
	 * @param h entidade de colheita que será convertida
	 */
	public HarvestDTO(Harvest h) {
		// Copia a descrição diretamente
		this.description = h.getDescription();

		// Converte o tempo de verão para QuantityDTO; mantém null se não houver valor
		this.timeToHarvestSummer = h.getTimeToHarvestSummer() == null
				? null : new QuantityDTO<Time>(h.getTimeToHarvestSummer());

		// Converte o tempo de inverno para QuantityDTO; mantém null se não houver valor
		this.timeToHarvestWinter = h.getTimeToHarvestWinter() == null
				? null : new QuantityDTO<Time>(h.getTimeToHarvestWinter());

		// Se não houver condições, usa lista vazia; caso contrário, cria uma cópia
		// imutável para evitar que alterações na lista original afetem o DTO
		this.conditions = h.getConditions() == null
				? List.of() : List.copyOf(h.getConditions());
	}

	/**
	 * Retorna a descrição da colheita.
	 *
	 * @return descrição da colheita
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Retorna o tempo até a colheita no verão.
	 *
	 * @return tempo até a colheita no verão, ou {@code null} se não definido
	 */
	public QuantityDTO<Time> getTimeToHarvestSummer() {
		return timeToHarvestSummer;
	}

	/**
	 * Retorna o tempo até a colheita no inverno.
	 *
	 * @return tempo até a colheita no inverno, ou {@code null} se não definido
	 */
	public QuantityDTO<Time> getTimeToHarvestWinter() {
		return timeToHarvestWinter;
	}

	/**
	 * Retorna as condições associadas à colheita.
	 *
	 * @return lista imutável de condições (vazia se não houver nenhuma)
	 */
	public List<Condition> getConditions() {
		return conditions;
	}
}