package br.edu.ifrs.meucanteiroapi.util;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import br.edu.ifrs.meucanteiroapi.model.general.QuantityInterval;
import tech.units.indriya.quantity.Quantities;

/**
 * Classe utilitária com métodos auxiliares para conversão de elementos
 * RDF (literais e recursos, do Apache Jena) em tipos de dados Java
 * utilizados pela aplicação, incluindo quantidades e unidades de medida
 * (API JSR-385 / Units of Measurement).
 * <p>
 * Todos os métodos são estáticos e tolerantes a valores nulos: quando
 * um literal ou recurso não está presente no grafo RDF, os métodos
 * retornam {@code null} (ou delegam o tratamento do nulo para
 * {@link UnitsTranslationUtil}, no caso das unidades).
 * <p>
 * <b>Nota sobre tipagem de unidades:</b> como as unidades de medida são
 * resolvidas dinamicamente a partir de texto vindo do RDF (via
 * {@link UnitsTranslationUtil}), não há como o compilador verificar, em
 * tempo de compilação, que a unidade lida pertence a uma dimensão
 * específica (ex.: comprimento, massa). Por isso, os métodos desta
 * classe que envolvem unidades retornam tipos com wildcard
 * ({@code Quantity<?>}, {@code QuantityInterval<?>}, {@code Unit<?>}).
 * Cabe ao código que consome esses valores garantir a dimensão
 * esperada, por exemplo através de {@code Quantity.asType(...)}.
 */
public class RDFUtil {
	
	/**
	 * Converte um literal RDF em um valor {@code Integer}.
	 *
	 * @param lit o literal RDF a ser convertido, podendo ser {@code null}
	 * @return o valor numérico do literal, ou {@code null} caso
	 *         {@code lit} seja {@code null}
	 */
	public static Integer getIntLiteral(Literal lit) {
		return lit == null ? null : lit.getInt();
	}

	/**
	 * Converte um literal RDF em um valor {@code Double}.
	 *
	 * @param lit o literal RDF a ser convertido, podendo ser {@code null}
	 * @return o valor numérico do literal, ou {@code null} caso
	 *         {@code lit} seja {@code null}
	 */
	public static Double getDoubleLiteral(Literal lit) {
		return lit == null ? null : lit.getDouble();
	}
	
	/**
	 * Converte um literal RDF em um {@code boolean}.
	 *
	 * @param lit o literal RDF a ser convertido, podendo ser {@code null}
	 * @return o valor boolean do literal, ou {@code null} caso
	 *         {@code lit} seja {@code null}
	 */
	public static Boolean getBooleanLiteral(Literal lit) {
		return lit == null ? null : lit.getBoolean();
	}

	/**
	 * Converte um literal RDF em uma {@code String}.
	 *
	 * @param lit o literal RDF a ser convertido, podendo ser {@code null}
	 * @return o valor textual do literal, ou {@code null} caso
	 *         {@code lit} seja {@code null}
	 */
	public static String getStringLiteral(Literal lit) {
		return lit == null ? null : lit.getString();
	}

	/**
	 * Constrói uma {@link Quantity} a partir de um literal RDF (valor
	 * numérico) e de um recurso RDF que representa a unidade de medida.
	 * <p>
	 * <b>Atenção:</b> a dimensão da unidade (ex.: comprimento, massa)
	 * só é conhecida em tempo de execução, a partir do nome lido no
	 * RDF. Por isso o retorno é {@code Quantity<?>}, e não uma
	 * {@code Quantity<T>} tipada — o compilador não verifica se a
	 * unidade encontrada é compatível com a grandeza esperada pelo
	 * chamador. Caso necessário, use {@code asType(...)} no valor
	 * retornado para converter (com verificação em runtime) para a
	 * dimensão desejada.
	 *
	 * @param value o literal RDF contendo o valor numérico da quantidade
	 * @param unit  o recurso RDF que representa a unidade de medida
	 * @return a quantidade correspondente, combinando o valor numérico
	 *         e a unidade traduzida
	 */
	public static Quantity<?> getQuantity(Literal value, Resource unit) {
		if(getDoubleLiteral(value) == null)
			return null;
		return Quantities.getQuantity(getDoubleLiteral(value), getUnit(unit));
	}

	/**
	 * Constrói um {@link QuantityInterval} a partir de literais RDF que
	 * representam os valores mínimo e máximo, e de um recurso RDF que
	 * representa a unidade de medida comum a ambos.
	 * <p>
	 * <b>Atenção:</b> pela mesma razão descrita em {@link #getQuantity},
	 * a dimensão da unidade não é verificada em tempo de compilação; o
	 * retorno é {@code QuantityInterval<?>} e cabe ao chamador garantir
	 * que a grandeza obtida é a esperada.
	 *
	 * @param min  o literal RDF contendo o valor mínimo do intervalo
	 * @param max  o literal RDF contendo o valor máximo do intervalo
	 * @param unit o recurso RDF que representa a unidade de medida
	 * @return o intervalo de quantidades correspondente
	 */
	public static QuantityInterval<?> getQuantityInterval(Literal min, Literal max, Resource unit) {
		return new QuantityInterval<>(getDoubleLiteral(min), getDoubleLiteral(max), getUnit(unit));
	}

	/**
	 * Converte um recurso RDF que representa uma unidade de medida em um
	 * objeto {@link Unit}, traduzindo o nome local do recurso (URI) para
	 * a unidade correspondente através de {@link UnitsTranslationUtil}.
	 * <p>
	 * Caso o recurso seja {@code null}, a tradução é delegada da mesma
	 * forma, permitindo que {@link UnitsTranslationUtil} defina o
	 * comportamento padrão para unidade ausente.
	 * <p>
	 * <b>Atenção:</b> o retorno é {@code Unit<?>} propositalmente, pois
	 * a dimensão da unidade só é conhecida em tempo de execução (a
	 * partir do nome lido no RDF). Não existe, nesta classe, uma forma
	 * segura de convertê-la para um {@code Unit<T>} tipado sem uma
	 * checagem adicional em runtime por parte do chamador.
	 *
	 * @param unit o recurso RDF que representa a unidade de medida,
	 *             podendo ser {@code null}
	 * @return a unidade de medida correspondente
	 */
	public static Unit<?> getUnit(Resource unit) {
		if(unit == null)
			return UnitsTranslationUtil.getUnit(null);
		else
			return UnitsTranslationUtil.getUnit(unit.getLocalName());
	}
}