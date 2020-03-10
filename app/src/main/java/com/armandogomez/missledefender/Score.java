package com.armandogomez.missledefender;

public class Score {
	private long datetime;
	private String initials;
	private int score;
	private int level;

	public Score(long datetime, String initials, int score, int level) {
		this.datetime = datetime;
		this.initials = initials;
		this.score = score;
		this.score = level;
	}

	public int getScore() {
		return score;
	}
}
