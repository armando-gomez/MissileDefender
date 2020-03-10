package com.armandogomez.missiledefender;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Score {
	private long datetime;
	private String initials;
	private int score;
	private int level;
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

	public Score(long datetime, String initials, int score, int level) {
		this.datetime = datetime;
		this.initials = initials;
		this.score = score;
		this.level = level;
	}

	public int getScore() {
		return score;
	}

	public String toString() {
		return String.format(Locale.getDefault(), " %4s %5d %5d %12s%n", initials, level, score, sdf.format(new Date(datetime)));
	}
}
