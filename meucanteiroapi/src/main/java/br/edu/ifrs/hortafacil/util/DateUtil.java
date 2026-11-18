package br.edu.ifrs.hortafacil.util;

/**
 * Classe utilitária com constantes relacionadas a datas e períodos do
 * tempo, utilizadas em conjunto com a ontologia de cultivares.
 * <p>
 * É declarada como {@code abstract} apenas para impedir sua
 * instanciação, já que contém somente membros estáticos.
 */
public abstract class DateUtil {

	/**
	 * Vetor com as URIs (identificadores) dos meses do ano, conforme
	 * definidos na ontologia de cultivares (prefixo {@code conto}).
	 * <p>
	 * Os elementos estão na ordem cronológica dos meses, de forma que o
	 * índice do vetor corresponde ao número do mês menos um (índice
	 * {@code 0} representa Janeiro, índice {@code 1} representa
	 * Fevereiro, e assim por diante), facilitando o acesso via
	 * {@code Calendar.MONTH} ou estruturas similares.
	 */
	public static final String[] MONTHS_URIS = {
			"conto:time_Janeiro",
			"conto:time_Fevereiro",
			"conto:time_Marco",
			"conto:time_Abril",
			"conto:time_Maio",
			"conto:time_Junho",
			"conto:time_Julho",
			"conto:time_Agosto",
			"conto:time_Setembro",
			"conto:time_Outubro",
			"conto:time_Novembro",
			"conto:time_Dezembro"
	};
}