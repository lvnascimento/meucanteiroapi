package br.edu.ifrs.hortafacil.dto;

import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Length;

import br.edu.ifrs.hortafacil.model.management.FertilizationPractice;
import br.edu.ifrs.hortafacil.model.management.ManagementPractice;
import br.edu.ifrs.hortafacil.model.management.planting.CultivarPlantingSignature;
import br.edu.ifrs.hortafacil.model.management.planting.DirectSeeding;
import br.edu.ifrs.hortafacil.model.management.planting.PlantingMethod;

/**
 * DTO (Data Transfer Object) que representa a assinatura de plantio de um
 * cultivar ({@link CultivarPlantingSignature}) para serialização (ex.: JSON)
 * e transporte via API REST.
 * <p>
 * Os espaçamentos são convertidos para {@link QuantityDTO}. Nas listas de
 * métodos de plantio e de práticas de manejo, cada elemento que possui DTO
 * próprio é substituído por ele: {@link DirectSeeding} vira
 * {@link DirectSeedingDTO} e {@link FertilizationPractice} vira
 * {@link FertilizationPracticeDTO}.
 * <p>
 * Tratamento dos demais subtipos:
 * <ul>
 *   <li>{@link PlantingMethod} sem DTO específico: repassado como está.</li>
 *   <li>{@link ManagementPractice} sem DTO específico: copiado como um
 *       {@code ManagementPractice} genérico, preservando apenas rótulo,
 *       descrição e alternativas. Campos próprios de subtipos são
 *       descartados.</li>
 * </ul>
 * <p>
 * Os objetos de domínio nunca são alterados: as práticas são copiadas antes
 * de receberem as alternativas convertidas.
 * <p>
 * <strong>Acoplamento:</strong> as listas são tipadas com as classes de
 * domínio porque os DTOs as estendem. Misturar domínio e DTOs na mesma lista
 * depende da (de)serialização polimórfica do Jackson ({@code @JsonTypeInfo}
 * e {@code @JsonSubTypes}) configurada em {@link PlantingMethod} e em
 * {@link ManagementPractice}.
 */
public class CultivarPlantingSignatureDTO {

	/** Meses recomendados para o plantio (1 a 12). */
	private final List<Integer> monthsToPlant;

	/** Espaçamento recomendado entre linhas de plantio; pode ser {@code null}. */
	private final QuantityDTO<Length> rowSpacing;

	/** Espaçamento recomendado entre plantas; pode ser {@code null}. */
	private final QuantityDTO<Length> plantSpacing;

	/** Métodos de plantio, com os tipos que possuem DTO já convertidos. */
	private final List<PlantingMethod> methods;

	/** Práticas de manejo, com os tipos que possuem DTO já convertidos. */
	private final List<ManagementPractice> practices;

	/**
	 * Cria o DTO a partir da assinatura de plantio de domínio, sem alterar
	 * o objeto de origem.
	 *
	 * @param s a assinatura de plantio de origem
	 */
	public CultivarPlantingSignatureDTO(CultivarPlantingSignature s) {
		this.monthsToPlant = s.getMonthsToPlant() == null
				? List.of() : List.copyOf(s.getMonthsToPlant());
		this.rowSpacing = s.getRowSpacing() == null
				? null : new QuantityDTO<Length>(s.getRowSpacing());
		this.plantSpacing = s.getPlantSpacing() == null
				? null : new QuantityDTO<Length>(s.getPlantSpacing());

		List<PlantingMethod> methodsDTO = new ArrayList<>();
		if (s.getMethods() != null) {
			for (PlantingMethod pm : s.getMethods()) {
				methodsDTO.add(toMethodDTO(pm));
			}
		}
		this.methods = methodsDTO;

		List<ManagementPractice> practicesDTO = new ArrayList<>();
		if (s.getPractices() != null) {
			for (ManagementPractice mp : s.getPractices()) {
				practicesDTO.add(toPracticeDTO(mp));
			}
		}
		this.practices = practicesDTO;
	}

	/**
	 * Converte um método de plantio para DTO, quando existir um específico.
	 * Os demais tipos são repassados sem alteração.
	 */
	private static PlantingMethod toMethodDTO(PlantingMethod pm) {
		if (pm instanceof DirectSeeding ds) {
			return new DirectSeedingDTO(ds);
		}
		return pm;
	}

	/**
	 * Converte uma prática de manejo para DTO.
	 * <p>
	 * Primeiro cria uma cópia da prática (para não alterar o objeto de
	 * domínio) com as alternativas já convertidas. Depois, se for uma
	 * {@link FertilizationPractice}, embrulha a cópia em
	 * {@link FertilizationPracticeDTO}. Subtipos sem DTO próprio viram um
	 * {@link ManagementPractice} genérico.
	 */
	private static ManagementPractice toPracticeDTO(ManagementPractice mp) {
		ManagementPractice copy;
		if (mp instanceof FertilizationPractice fp) {
			FertilizationPractice fertilizationCopy = new FertilizationPractice();
			fertilizationCopy.setItems(fp.getItems());
			copy = fertilizationCopy;
		} else {
			copy = new ManagementPractice();
		}
		copy.setLabel(mp.getLabel());
		copy.setDescription(mp.getDescription());

		List<ManagementPractice> alternatives = new ArrayList<>();
		if (mp.getAlternatives() != null) {
			for (ManagementPractice alt : mp.getAlternatives()) {
				alternatives.add(toAlternativeDTO(alt));
			}
		}
		copy.setAlternatives(alternatives);

		if (copy instanceof FertilizationPractice fertilizationCopy) {
			return new FertilizationPracticeDTO(fertilizationCopy);
		}
		return copy;
	}

	/** Converte uma alternativa para DTO quando for de fertilização. */
	private static ManagementPractice toAlternativeDTO(ManagementPractice alt) {
		if (alt instanceof FertilizationPractice fp) {
			return new FertilizationPracticeDTO(fp);
		}
		return alt;
	}

	/** @return os meses recomendados para plantio (lista imutável) */
	public List<Integer> getMonthsToPlant() {
		return monthsToPlant;
	}

	/** @return o espaçamento entre linhas, ou {@code null} se não informado */
	public QuantityDTO<Length> getRowSpacing() {
		return rowSpacing;
	}

	/** @return o espaçamento entre plantas, ou {@code null} se não informado */
	public QuantityDTO<Length> getPlantSpacing() {
		return plantSpacing;
	}

	/** @return os métodos de plantio, já convertidos para DTO quando aplicável */
	public List<PlantingMethod> getMethods() {
		return methods;
	}

	/** @return as práticas de manejo, já convertidas para DTO quando aplicável */
	public List<ManagementPractice> getPractices() {
		return practices;
	}
}