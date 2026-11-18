package br.edu.ifrs.hortafacil.model.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo de domínio que representa um manejo aplicável a uma cultivar: as
 * práticas a executar, a janela de tempo em que se aplicam e as condições para
 * isso.
 * <p>
 * A janela de aplicação é expressa em dias, relativos ao início de um estágio
 * fenológico do ciclo da cultura ({@link #getStageLabel()}), ou vale para o ciclo
 * inteiro ({@link #isWholeCycle()}). É um objeto simples de transferência de
 * dados, montado a partir da base de conhecimento RDF (ver
 * {@code ManagementDAO}).
 */
public class Management {

	/** Práticas de manejo associadas à cultivar. */
	private List<ManagementPractice> practices;

	/**
	 * Rótulo do estágio fenológico ao qual a janela é relativa; {@code null}
	 * quando o manejo vale para o ciclo inteiro ou não tem estágio definido.
	 */
	private String stageLabel;

	/** Início da janela de aplicação, em dias a partir do estágio. */
	private int startDays;

	/**
	 * Fim da janela de aplicação, em dias a partir do estágio. Na leitura da
	 * ontologia, assume o mesmo valor de {@link #startDays} quando não há
	 * deslocamento final.
	 */
	private int endDays;

	/**
	 * Indica que o manejo se aplica durante todo o ciclo da cultura; nesse
	 * caso, a janela em dias e o estágio não se aplicam.
	 */
	private boolean wholeCycle;

	/** Condições que devem ser atendidas para aplicar o manejo. */
	private List<Condition> conditions;

	/**
	 * @return as práticas de manejo, ou {@code null} se não definidas
	 */
	public List<ManagementPractice> getPractices() {
		return practices;
	}

	/**
	 * Define as práticas de manejo.
	 * <p>
	 * Guarda uma cópia da lista recebida (cópia rasa: os itens são os mesmos),
	 * para que objetos {@code Management} não compartilhem a mesma instância de
	 * lista, o que pode acontecer quando o DAO reaproveita resultados de
	 * consultas.
	 *
	 * @param practices as práticas de manejo; pode ser {@code null}
	 */
	public void setPractices(List<ManagementPractice> practices) {
		this.practices = practices != null ? new ArrayList<>(practices) : null;
	}

	/**
	 * @return o rótulo do estágio fenológico, ou {@code null} se o manejo vale
	 *         para o ciclo inteiro ou não tem estágio
	 */
	public String getStageLabel() {
		return stageLabel;
	}

	/**
	 * @param stageLabel o rótulo do estágio fenológico; pode ser {@code null}
	 */
	public void setStageLabel(String stageLabel) {
		this.stageLabel = stageLabel;
	}

	/**
	 * @return o início da janela de aplicação, em dias a partir do estágio
	 */
	public int getStartDays() {
		return startDays;
	}

	/**
	 * @param startDays o início da janela de aplicação, em dias
	 */
	public void setStartDays(int startDays) {
		this.startDays = startDays;
	}

	/**
	 * @return o fim da janela de aplicação, em dias a partir do estágio
	 */
	public int getEndDays() {
		return endDays;
	}

	/**
	 * @param endDays o fim da janela de aplicação, em dias
	 */
	public void setEndDays(int endDays) {
		this.endDays = endDays;
	}

	/**
	 * @return {@code true} se o manejo se aplica durante todo o ciclo da cultura
	 */
	public boolean isWholeCycle() {
		return wholeCycle;
	}

	/**
	 * @param wholeCycle {@code true} se o manejo se aplica durante todo o ciclo
	 */
	public void setWholeCycle(boolean wholeCycle) {
		this.wholeCycle = wholeCycle;
	}

	/**
	 * @return as condições para aplicar o manejo, ou {@code null} se não
	 *         definidas
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * Define as condições do manejo.
	 * <p>
	 * Guarda uma cópia da lista recebida (cópia rasa), pelo mesmo motivo de
	 * {@link #setPractices(List)}.
	 *
	 * @param conditions as condições do manejo; pode ser {@code null}
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions != null ? new ArrayList<>(conditions) : new ArrayList<>();
	}

	@Override
	public int hashCode() {
		return Objects.hash(conditions, endDays, practices, stageLabel, startDays, wholeCycle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Management other = (Management) obj;
		return Objects.equals(conditions, other.conditions)
				&& endDays == other.endDays
				&& Objects.equals(practices, other.practices)
				&& Objects.equals(stageLabel, other.stageLabel)
				&& startDays == other.startDays
				&& wholeCycle == other.wholeCycle;
	}

	@Override
	public String toString() {
		return "Management [practices=" + practices + ", stageLabel=" + stageLabel + ", startDays=" + startDays
				+ ", endDays=" + endDays + ", wholeCycle=" + wholeCycle + ", conditions=" + conditions + "]";
	}
}