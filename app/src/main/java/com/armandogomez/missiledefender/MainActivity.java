package com.armandogomez.missiledefender;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	public static int screenHeight;
	public static int screenWidth;
	private ConstraintLayout layout;
	private boolean titleRunning = true;
	private boolean missileMakerRunning = false;

	private MissileMaker missileMaker;
	private RemoteDatabaseHandler remoteDatabaseHandler;

	private ArrayList<Base> baseList = new ArrayList<>();
	private ArrayList<Missile> activeMissiles = new ArrayList<>();

	private int score, level = 1;
	private TextView scoreText, levelText;

	@SuppressLint("ResourceType")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layout = findViewById(R.id.layout);
		scoreText = findViewById(R.id.score);
		levelText = findViewById(R.id.level);

		layout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					handleTouch(event.getX(), event.getY());
				}
				return false;
			}
		});

		setupFullScreen();
		getScreenDimensions();

		SoundPlayer.getInstance().setupSound(this, "background", R.raw.background, true);
		SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast, false);
		SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
		SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile, false);
		SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
		SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile, false);

		startTitle();
	}

	private void startScrollingBackground() {
		scoreText.setVisibility(View.VISIBLE);
		levelText.setVisibility(View.VISIBLE);

		new ScrollingBackground(this, layout, R.drawable.clouds, 4000);
		setUpBases();
	}

	private void setUpBases() {
		for(int i=1; i <= 3; i++) {
			float x = (float) (screenWidth * (i * .25));
			float y = screenHeight;
			Base b = new Base(this);
			b.createBase(x, y-138);
			baseList.add(b);
		}

		createMissileMaker();
	}

	public void createMissileMaker() {
		missileMakerRunning = true;
		missileMaker = new MissileMaker(this, screenWidth, screenHeight);
		new Thread(missileMaker).start();
	}

	public void setLevel(final int value) {
		level = value;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				levelText.setText(String.format(Locale.getDefault(), "Level: %d", value));
			}
		});
	}

	public void addMissile(Missile m) {
		activeMissiles.add(m);
	}

	public void removeMissile(final Missile m) {
		activeMissiles.remove(m);
		missileMaker.removeMissile(m);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getLayout().removeView(m.imageView);
			}
		});
	}

	public void handleTouch(float x, float y) {
		if(!titleRunning && missileMakerRunning) {
			Log.d(TAG, "handleTouch: " + x + ", " + y);

			Base close = null;
			float closeDistance = Float.MAX_VALUE;

			for(int i=0; i < baseList.size(); i++) {
				Base b = baseList.get(i);
				float baseX = b.getX();
				float baseY = b.getY();

				if(distanceCalc(baseX, baseY, x, y) < closeDistance) {
					closeDistance = distanceCalc(baseX, baseY, x, y);
					close = b;
				}
			}

			launchInterceptor(close, x, y);
		}
	}

	private void launchInterceptor(Base base, float x, float y) {
		Interceptor interceptor = new Interceptor(this, base, x, y, screenHeight);

		interceptor.createInterceptor();

		final AnimatorSet set = interceptor.getSet();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				set.start();
			}
		});
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	private void startTitle() {
		final ImageView imageView = new ImageView(this);
		titleRunning = true;

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getLayout().addView(imageView);
				imageView.setImageResource(R.drawable.title);
				imageView.setAlpha(0f);

				ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
				layoutParams.topToTop = R.id.layout;
				layoutParams.bottomToBottom = R.id.layout;
				layoutParams.leftToLeft = R.id.layout;
				layoutParams.rightToRight = R.id.layout;

				imageView.setLayoutParams(layoutParams);
			}
		});

		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
		fadeIn.setDuration(6000);

		fadeIn.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				getLayout().removeView(imageView);
				startScrollingBackground();
				titleRunning = false;
			}
		});

		AnimatorSet set = new AnimatorSet();

		set.play(fadeIn);

		set.start();
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

	private void getScreenDimensions() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenHeight = displayMetrics.heightPixels;
		screenWidth = displayMetrics.widthPixels;
	}

	public void applyMissileBlast(float x, float y) {
		for(int i=0; i < baseList.size(); i++) {
			Base b = baseList.get(i);
			float baseX = b.getX();
			float baseY = b.getY();

			if(distanceCalc(baseX, baseY, x, y) < 250) {
				baseList.remove(b);
				b.destruct();

				if(baseList.size() == 0) {
					endGame();
				}
			}
		}
	}

	private float distanceCalc(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public void applyInterceptorBlast(float x, float y) {
		for(int i=0; i < activeMissiles.size(); i++) {
			final Missile m = activeMissiles.get(i);

			if(distanceCalc(m.imageView.getX(), m.imageView.getY(), x, y) < 120) {
				incrementScore();
				SoundPlayer.getInstance().start("interceptor_hit_missile");

				m.interceptorBlast();

				activeMissiles.remove(m);
			}
		}
	}

	private void incrementScore() {
		score++;
		scoreText.setText(String.format(Locale.getDefault(), "%d", score));
	}

	private void showGameOver() {
		final ImageView imageView = new ImageView(this);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getLayout().addView(imageView);
				imageView.setImageResource(R.drawable.game_over);
				imageView.setAlpha(0f);

				ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
				layoutParams.topToTop = R.id.layout;
				layoutParams.bottomToBottom = R.id.layout;
				layoutParams.leftToLeft = R.id.layout;
				layoutParams.rightToRight = R.id.layout;

				imageView.setLayoutParams(layoutParams);
			}
		});

		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
		fadeIn.setDuration(3000);

		fadeIn.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				getLayout().removeView(imageView);
			}
		});

		AnimatorSet set = new AnimatorSet();

		set.play(fadeIn);

		set.start();
	}

	private void endGame() {
		missileMaker.setRunning(false);
		missileMakerRunning = false;

		showGameOver();

		remoteDatabaseHandler = new RemoteDatabaseHandler(this);
		remoteDatabaseHandler.execute(scoreText.getText().toString());
	}

	public void scoreCheck(Boolean b) {
		if(b.booleanValue()) {
			showScoreSubmissionWindow();
		}
		openTopTenActivity();
	}

	public void openTopTenActivity() {
		Intent intent = new Intent(this, TopTenActivity.class);
		startActivity(intent);
	}

	private void showScoreSubmissionWindow() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("You are a Top-Player!");
		builder.setMessage("Please enter your initials (up to 3 characters):");
		final EditText editText = new EditText(this);
		editText.setGravity(Gravity.CENTER_HORIZONTAL);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
		builder.setView(editText);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String initial = editText.getText().toString().toUpperCase();
				try {
					remoteDatabaseHandler.submitScore(initial, score, level);
					openTopTenActivity();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();
	}
}