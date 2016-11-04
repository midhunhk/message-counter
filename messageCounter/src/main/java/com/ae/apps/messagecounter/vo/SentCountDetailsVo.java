package com.ae.apps.messagecounter.vo;

/**
 * Represents the sent count details
 * 
 * @author user
 * 
 */
public class SentCountDetailsVo {

	private int	sentToday;
	private int	sentCycle;
	private int sentInWeek;
	private int	cycleLimit;
	private int	sentLastCycle;

	public int getSentToday() {
		return sentToday;
	}

	public void setSentToday(int sentToday) {
		this.sentToday = sentToday;
	}

	public int getSentCycle() {
		return sentCycle;
	}

	public void setSentCycle(int sentCycle) {
		this.sentCycle = sentCycle;
	}

	public int getCycleLimit() {
		return cycleLimit;
	}

	public void setCycleLimit(int cycleLimit) {
		this.cycleLimit = cycleLimit;
	}

	public int getSentLastCycle() {
		return sentLastCycle;
	}

	public void setSentLastCycle(int sentLastCycle) {
		this.sentLastCycle = sentLastCycle;
	}

	public int getSentInWeek() {
		return sentInWeek;
	}

	public void setSentInWeek(int sentInWeek) {
		this.sentInWeek = sentInWeek;
	}

}
