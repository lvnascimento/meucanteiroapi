package br.edu.ifrs.hortafacil.model.general;

import javax.measure.Quantity;

/**
 * Grandeza física customizada que representa massa por área (ex.:
 * kg/m², usada para expressar densidade de semeadura, taxa de
 * aplicação de adubo/insumos, etc.).
 * <p>
 * Segue o padrão da API JSR-385 (Units of Measurement) para definição
 * de grandezas físicas: uma interface vazia que estende {@link
 * Quantity}, parametrizada com ela mesma, permitindo que o sistema de
 * tipos garanta, em tempo de compilação, que apenas quantidades dessa
 * dimensão (massa por área) sejam usadas onde {@code MassPerArea} for
 * esperado — de forma semelhante às grandezas padrão da API, como
 * {@code Length} ou {@code Mass}, mas para uma unidade composta que
 * não possui uma interface própria na especificação.
 */
public interface MassPerArea extends Quantity<MassPerArea>{

}