package br.edu.ifrs.hortafacil.model.management;

import java.util.List;
import java.util.Objects;

/**
 * Prática de manejo específica de adubação.
 *
 * Estende `ManagementPractice` (herdando label, description e alternatives)
 * e acrescenta a lista de itens de fertilizante que podem ser usados.
 * É o equivalente no back-end da classe `FertilizationPractice` do front-end.
 */
public class FertilizationPractice extends ManagementPractice {

	// Fertilizantes (com suas quantidades) que atendem a esta prática de adubação
	private List<FertilizerAlternativeItem> items;

	public List<FertilizerAlternativeItem> getItems() {
		return items;
	}

	public void setItems(List<FertilizerAlternativeItem> items) {
		this.items = items;
	}

	/**
	 * Hash coerente com o equals: combina o hash da classe base
	 * (label, description, alternatives) com o hash de `items`.
	 */
	public int hashCode() {
		final int prime = 31;
		// Começa pelo hash da superclasse para incluir os campos herdados
		int result = super.hashCode();
		result = prime * result + Objects.hash(items);
		return result;
	}

	/**
	 * Duas práticas de adubação são iguais se a parte herdada for igual
	 * (inclui a checagem de null e de classe) e se `items` também for.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		// Delega à superclasse: compara label, description, alternatives
		// e já rejeita null e classes diferentes
		if (!super.equals(obj))
			return false;
		// Checagem redundante (a superclasse já faz), mas inofensiva
		if (getClass() != obj.getClass())
			return false;
		FertilizationPractice other = (FertilizationPractice) obj;
		return Objects.equals(items, other.items);
	}

	@Override
	public String toString() {
		return "FertilizationPractice [items=" + items + ", getLabel()=" + getLabel() + ", getDescription()="
				+ getDescription() + ", getAlternatives()=" + getAlternatives() + "]";
	}
}