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
import java.util.Locale;

public class RemoteDatabaseSubmit extends AsyncTask<String, Void, String> {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private MainActivity mainActivity;
	private static String databaseURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
	private static final String APPSCORES_TABLE = "AppScores";
	private static final String TAG = "RemoteDatabaseSubmit";
	private Connection connection;

	private static ArrayList<Score> scoresList = new ArrayList<>();

	RemoteDatabaseSubmit(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	protected void onPostExecute(String s) {
		Log.d(TAG, "onPostExecute: " + s);
		mainActivity.openTopTenActivity(s);
	}

	@Override
	protected String doInBackground(String... strings) {
		String initials = strings[0];
		int score = Integer.parseInt(strings[1]);
		int level = Integer.parseInt(strings[2]);

		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(databaseURL, "chri5558_student", "ABC.123");
			submitScore(initials, score, level);
			return getTopTenScores();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTopTenScores() throws SQLException {
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

		Collections.sort(scoresList, new RemoteDatabaseSubmit.SortByScore());

		sb = new StringBuilder();
		for(int i=1; i<=scoresList.size(); i++) {
			Score s = scoresList.get(i-1);
			sb.append(String.format(Locale.getDefault(), "%2d %s", i, s.toString()));
		}

		return sb.toString();
	}

	public void submitScore(String initials, int score, int level) throws SQLException {
		Statement statement = connection.createStatement();

		String query = "INSERT INTO " + APPSCORES_TABLE + " VALUES (" + System.currentTimeMillis() + ", '" + initials + "', " + score + ", " + level + ")";

		statement.executeUpdate(query);

		statement.close();
	}

	class SortByScore implements Comparator<Score> {
		public int compare(Score a, Score b) {
			return b.getScore() - a.getScore();
		}
	}
}
