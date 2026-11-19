package br.edu.ifrs.meucanteiroapi.model.management.planting;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import br.edu.ifrs.meucanteiroapi.dto.DirectSeedingDTO;

/**
 * Classe base abstrata que representa um método de plantio de um
 * cultivar (ex.: semeadura direta, transplante de mudas).
 * <p>
 * Não define atributos ou comportamentos próprios: serve apenas como
 * "marcador" comum para suas subclasses concretas, permitindo que
 * diferentes métodos de plantio sejam tratados de forma polimórfica
 * (ex.: em uma lista ou atributo do tipo {@code PlantingMethod}).
 * <p>
 * As anotações do Jackson abaixo configuram a (de)serialização
 * polimórfica em JSON: ao serializar um objeto desta hierarquia, é
 * incluída uma propriedade {@code "type"} indicando a subclasse
 * concreta (ex.: {@code "DIRECT_SEEDING"} ou
 * {@code "SEEDLING_TRANSPLANTING"}); ao desserializar, o Jackson usa
 * essa mesma propriedade para decidir em qual subclasse instanciar o
 * JSON recebido.
 *
 * @see DirectSeedingDTO
 * @see SeedlingTransplanting
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DirectSeedingDTO.class, name = "DIRECT_SEEDING"),
    @JsonSubTypes.Type(value = SeedlingTransplanting.class, name = "SEEDLING_TRANSPLANTING")
})
public abstract class PlantingMethod {

}