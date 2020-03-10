package com.armandogomez.missiledefender;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

public class TopTenActivity extends AppCompatActivity {
	private String data;
	private TextView output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_ten);

		setupFullScreen();

		Intent intent = getIntent();
		data = intent.getStringExtra("DATA");

		output = findViewById(R.id.output);

		printScores();
	}

	private void printScores() {
		String headers = String.format(Locale.getDefault(), "%2s %4s %5s %5s %12s%n", "#", "Init", "Level", "Score", "Date/Time");
		String text = headers + data;
		output.setText(text);
	}

	public void exit(View v) {
		finish();
	}

	@SuppressLint("SourceLockedOrientationActivity")
	private void setupFullScreen() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

}
