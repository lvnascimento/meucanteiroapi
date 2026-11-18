package br.edu.ifrs.hortafacil.util;

import javax.measure.Unit;
import javax.measure.quantity.Area;

import br.edu.ifrs.hortafacil.model.general.MassPerArea;
import tech.units.indriya.format.SimpleUnitFormat;
import tech.units.indriya.unit.Units;

/**
 * Classe utilitária que define unidades de medida customizadas usadas
 * pela aplicação, mas que não fazem parte do conjunto padrão de
 * unidades da API JSR-385 (Units of Measurement) / Indriya.
 * <p>
 * Inclui a unidade de área "hectare" e unidades de massa por área
 * (usando a grandeza customizada {@link MassPerArea}), como
 * quilograma por metro quadrado, grama por metro quadrado e quilograma
 * por hectare — úteis para expressar densidade de semeadura, taxa de
 * aplicação de insumos, entre outros usos agrícolas.
 * <p>
 * As unidades também recebem rótulos textuais legíveis (ex.:
 * {@code "kg/m²"}) registrados via {@link SimpleUnitFormat}, para que
 * sejam exibidas de forma amigável ao serem convertidas para texto
 * (ex.: em {@code toString()} ou na serialização de DTOs, como em
 * {@code QuantityDTO}).
 */
public class MyUnits {

	/**
	 * Unidade de área "hectare", definida como 10.000 metros quadrados.
	 */
	public static final Unit<Area> HECTARE = Units.SQUARE_METRE.multiply(10000);

	/**
	 * Unidade de massa por área "quilograma por metro quadrado"
	 * (kg/m²).
	 * <p>
	 * O cast para {@code Unit<MassPerArea>} é necessário porque o
	 * resultado de {@code divide(...)} entre unidades da API não é
	 * automaticamente tipado como a grandeza customizada
	 * {@link MassPerArea} — o compilador não tem como inferir essa
	 * associação, por isso o {@code @SuppressWarnings("unchecked")}.
	 */
	@SuppressWarnings("unchecked")
	public static final Unit<MassPerArea> KG_PER_M2 = (Unit<MassPerArea>) Units.KILOGRAM.divide(Units.SQUARE_METRE);

	/**
	 * Unidade de massa por área "quilograma por hectare" (kg/ha),
	 * derivada de {@link #KG_PER_M2} dividido por 10.000 (equivalente a
	 * um hectare em metros quadrados).
	 */
    public static final Unit<MassPerArea> KG_PER_HA = KG_PER_M2.divide(10000);

	/**
	 * Unidade de massa por área "grama por metro quadrado" (g/m²),
	 * derivada de {@link #KG_PER_M2} dividido por 1.000 (conversão de
	 * quilograma para grama).
	 */
    public static final Unit<MassPerArea> G_PER_M2 = KG_PER_M2.divide(1000);

	// Bloco de inicialização estática: registra os rótulos textuais
	// legíveis das unidades customizadas assim que a classe é
	// carregada, para que sejam usados na formatação/exibição dessas
	// unidades.
    static {
    		SimpleUnitFormat format = SimpleUnitFormat.getInstance();
        format.label(KG_PER_M2, "kg/m²");
        format.label(G_PER_M2, "g/m²");
        format.label(KG_PER_HA, "kg/ha");
    }
}