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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
	private int screenHeight;
	private int screenWidth;
	private ConstraintLayout layout;

	@SuppressLint("ResourceType")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		layout = findViewById(R.layout.activity_main);

		setupFullScreen();
		getScreenDimensions();

		SoundPlayer.getInstance().setupSound(this, "background", R.raw.background);
		SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast);
		SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
		SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missle", R.raw.interceptor_hit_missile);
		SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
		SoundPlayer.getInstance().setupSound(this, "launch_missle", R.raw.launch_missile);

		startTitle();
	}

	private void startTitle() {
		ImageView title = findViewById(R.id.title);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f);
		fadeIn.setDuration(5000);
		final AnimatorSet set = new AnimatorSet();

		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				SoundPlayer.getInstance().start("background");
			}
		});

		set.start();
	}

	public ConstraintLayout getLayout() {
		return layout;
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
