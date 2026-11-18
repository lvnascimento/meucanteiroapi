package br.edu.ifrs.hortafacil.util;

import java.util.HashMap;
import java.util.Map;

import javax.measure.MetricPrefix;
import javax.measure.Unit;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.Units;

/**
 * Classe utilitária responsável por traduzir os códigos textuais de
 * unidades de medida utilizados na ontologia de cultivares (RDF) para
 * objetos {@link Unit} da API JSR-385 (Units of Measurement), usando a
 * implementação Indriya.
 * <p>
 * O mapeamento entre os códigos (ex.: {@code "KiloM"}, {@code "DEG_C"})
 * e as respectivas unidades é construído uma única vez, na inicialização
 * estática da classe, e mantido em memória em um {@link Map}.
 */
public class UnitsTranslationUtil {

	/**
	 * Mapa que associa o código textual de uma unidade (conforme usado
	 * na ontologia RDF) à unidade de medida correspondente.
	 */
	private static Map<String,Unit<?>> map;

	/**
	 * Traduz o código textual de uma unidade de medida para o objeto
	 * {@link Unit} correspondente.
	 *
	 * @param name o código da unidade (ex.: {@code "KiloM"},
	 *             {@code "DEG_C"}), podendo ser {@code null}
	 * @return a unidade adimensional {@link AbstractUnit#ONE} caso
	 *         {@code name} seja {@code null}; caso contrário, a unidade
	 *         correspondente ao código informado (ou {@code null} caso
	 *         o código não esteja mapeado)
	 */
	public static Unit<?> getUnit(String name) {
        if(name == null)
            return AbstractUnit.ONE;
        else
            return map.get(name);
    }
    
	// Bloco de inicialização estática: popula o mapa de unidades assim
	// que a classe é carregada, antes de qualquer chamada a getUnit.
    static {
        initMap();
    }
    
	/**
	 * Inicializa o mapa de unidades, associando cada código textual
	 * (usado na ontologia RDF) à unidade de medida correspondente,
	 * organizadas por grandeza física.
	 */
    private static void initMap() {
        map = new HashMap<>();
        
        // DISTÂNCIA OU COMPRIMENTO
        
        // Metro
        map.put("M", Units.METRE);
        map.put("FemtoM", MetricPrefix.FEMTO(Units.METRE));
        map.put("PicoM", MetricPrefix.PICO(Units.METRE));
        map.put("NanoM", MetricPrefix.NANO(Units.METRE));
        map.put("MicroM", MetricPrefix.MICRO(Units.METRE));
        map.put("MilliM", MetricPrefix.MILLI(Units.METRE));
        map.put("CentiM", MetricPrefix.CENTI(Units.METRE));
        map.put("DeciM", MetricPrefix.DECI(Units.METRE));
        map.put("HectoM", MetricPrefix.HECTO(Units.METRE));
        map.put("KiloM", MetricPrefix.KILO(Units.METRE));
        
        // UNIDADES DE VELOCIDADE
        
        // Metro por segundo
        map.put("M-PER-SEC", Units.METRE_PER_SECOND);
        
        // Quilômetro por hora
        map.put("KiloM-PER-HR", Units.KILOMETRE_PER_HOUR);
        
        // Milímetro por segundo
        map.put("MilliM-PER-SEC", MetricPrefix.MILLI(Units.METRE).divide(Units.SECOND));
        
        // UNIDADES DE ACELERAÇÃO
        
        // Metro por segundo ao quadrado
        map.put("M-PER-SEC2", Units.METRE_PER_SQUARE_SECOND);
        
        // VELOCIDADE ANGULAR, VELOCIDADE ROTACIONAL, FREQUÊNCIA ANGULAR
        
        // Radianos por segundo
        map.put("RAD-PER-SEC", Units.RADIAN.divide(Units.SECOND));
        
        // Radianos por minuto
        map.put("RAD-PER-MIN", Units.RADIAN.divide(Units.MINUTE));
        
        // Radianos por hora
        map.put("RAD-PER-HR", Units.RADIAN.divide(Units.HOUR));
        
        // UNIDADES DE ÂNGULO
        
        // Grau
        map.put("RAD", Units.RADIAN);
        
        // TEMPERATURA
        
        // Celsius
        map.put("DEG_C", Units.CELSIUS);
        
        // Kelvin
        map.put("K", Units.KELVIN);
        
        // ILUMINÂNCIA
        
        // Lux
        map.put("LUX", Units.LUX);
        
        // INTENSIDADE DE FLUXO DE CAMPO MAGNÉTICO
        
        // Tesla
        map.put("NanoT", MetricPrefix.NANO(Units.TESLA));
        map.put("MicroT", MetricPrefix.MICRO(Units.TESLA));
        map.put("MilliT", MetricPrefix.MILLI(Units.TESLA));
        map.put("T", Units.TESLA);
        
        // PRESSÃO
        
        // Pascal
        map.put("MicroPA", MetricPrefix.MICRO(Units.PASCAL));
        map.put("MilliPA", MetricPrefix.MILLI(Units.PASCAL));
        map.put("PA", Units.PASCAL);
        map.put("DecaPA", MetricPrefix.DEKA(Units.PASCAL));
        map.put("HectoPA", MetricPrefix.HECTO(Units.PASCAL));
        map.put("KiloPA", MetricPrefix.KILO(Units.PASCAL));
        map.put("MegaPA", MetricPrefix.MEGA(Units.PASCAL));
        map.put("GigaPA", MetricPrefix.GIGA(Units.PASCAL));
        
        // PORCENTAGEM
        
        map.put("PERCENT", Units.PERCENT);
        
        // FREQUÊNCIA
        
        map.put("HZ", Units.HERTZ);
        map.put("KiloHZ", MetricPrefix.KILO(Units.HERTZ));
        map.put("MegaHZ", MetricPrefix.MEGA(Units.HERTZ));
        map.put("GigaHZ", MetricPrefix.GIGA(Units.HERTZ));
        map.put("TeraHZ", MetricPrefix.TERA(Units.HERTZ));
        
        // TEMPO
        
        map.put("SEC", Units.SECOND);
        map.put("KiloSEC", MetricPrefix.KILO(Units.SECOND));
        map.put("MicroSEC", MetricPrefix.MICRO(Units.SECOND));
        map.put("MilliSEC", MetricPrefix.MILLI(Units.SECOND));
        map.put("NanoSEC", MetricPrefix.NANO(Units.SECOND));
        map.put("PicoSEC", MetricPrefix.PICO(Units.SECOND));
        
        map.put("HR", Units.HOUR);
        map.put("MIN", Units.MINUTE);
        
        map.put("DAY", Units.DAY);
        map.put("WEEK", Units.WEEK);
        map.put("MONTH", Units.MONTH);
        
        // MASSA POR ÁREA
        
        map.put("KiloGM-PER-M2", MyUnits.KG_PER_M2);
        map.put("KiloGM-PER-HA", MyUnits.KG_PER_HA);
        map.put("GM-PER-M2", MyUnits.G_PER_M2);
    }
}