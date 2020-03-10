package com.armandogomez.missiledefender;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RemoteDatabaseHandler extends AsyncTask<String, Void, Boolean> {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private MainActivity mainActivity;
	private static String databaseURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
	private static final String APPSCORES_TABLE = "AppScores";
	private static final String TAG = "RemoteDatabaseHandler";
	private Connection connection;

	private static ArrayList<Score> scoresList = new ArrayList<>();

	RemoteDatabaseHandler(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	protected void onPostExecute(Boolean b) {
		Log.d(TAG, "onPostExecute: " + b);
		mainActivity.scoreCheck(b);
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		int score = Integer.parseInt(strings[0]);
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(databaseURL, "chri5558_student", "ABC.123");
			getTopTenScores();
			return checkScoreEligibility(score);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkScoreEligibility(int score) {
		for(Score s: scoresList) {
			if(s.getScore() < score) {
				return true;
			}
		}

		return false;
	}

	public void getTopTenScores() throws SQLException {
		Statement statement = connection.createStatement();

		String query = "SELECT * FROM " + APPSCORES_TABLE + " ORDER BY Score DESC LIMIT 10";

		StringBuilder sb = new StringBuilder();
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()) {
			long datetime = resultSet.getLong(1);
			String initials = resultSet.getString(2);
			int score = resultSet.getInt(3);
			int level = resultSet.getInt(4);

			Score s = new Score(datetime, initials, score, level);
			scoresList.add(s);
		}
		resultSet.close();
		statement.close();

		Collections.sort(scoresList, new SortByScore());
	}

	public void submitScore(String initials, int score, int level) throws SQLException {
		Statement statement = connection.createStatement();

		String query = "INSERT INTO " + APPSCORES_TABLE + " VALUES (" + System.currentTimeMillis() + ", '" + initials + "', " + score + ", " + level + ")";

		statement.executeUpdate(query);

		statement.close();
	}

	public ArrayList<Score> getScoresList() {
		return scoresList;
	}

	class SortByScore implements Comparator<Score> {
		public int compare(Score a, Score b) {
			return a.getScore() - b.getScore();
		}
	}
}
