package com.armandogomez.missledefender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
	public static int screenHeight;
	public static int screenWidth;
	private ConstraintLayout layout;
	private boolean titleRunning = true;

	private MissileMaker missileMaker;

	private ArrayList<Base> baseList = new ArrayList<>();
	private ArrayList<Missile> activeMissiles = new ArrayList<>();

	private int score;
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
		SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missle", R.raw.interceptor_hit_missile, false);
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
		ImageView[] baseViews = new ImageView[] {findViewById(R.id.base1), findViewById(R.id.base2), findViewById(R.id.base3)};
		for(int i=0; i < 3; i++) {
			ImageView imageView = baseViews[i];
			imageView.setVisibility(View.VISIBLE);
			Base b = new Base(imageView);
			baseList.add(b);
		}

		createMissileMaker();
	}

	private void createMissileMaker() {
		missileMaker = new MissileMaker(this, screenWidth, screenHeight);
		new Thread(missileMaker).start();
	}

	public void setLevel(final int value) {
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

	public void removeMissile(Missile m) {
		activeMissiles.remove(m);
		missileMaker.removeMissile(m);
	}

	public void handleTouch(float x1, float y1) {
		if(!titleRunning) {
			Log.d(TAG, "handleTouch: " + x1 + ", " + y1);
		}
	}

	private void startBackgroundSound(final String id) {
		SoundPlayer.getInstance().start("background");
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	private void startTitle() {
		startBackgroundSound("background");
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
}
