package com.armandogomez.missledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SplashTitle implements Runnable{
	MainActivity mainActivity;
	int screenHeight;
	int screenWidth;

	SplashTitle(MainActivity mainActivity, int screenHeight, int screenWidth) {
		this.mainActivity = mainActivity;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
	}

	@Override
	public void run() {
		final ImageView title = new ImageView(mainActivity);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(title);
				title.setImageResource(R.drawable.title);

				ObjectAnimator fadeIn = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f);
				fadeIn.setDuration(5000);
				final AnimatorSet as = new AnimatorSet();

				as.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);
						SoundPlayer.getInstance().start("background");
					}
				});

				as.start();
			}
		});

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
