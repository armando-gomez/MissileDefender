package com.armandogomez.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.ImageView;

public class Base {
	private ImageView imageView;
	private MainActivity mainActivity;

	Base(final MainActivity mainActivity) {
		this.mainActivity = mainActivity;

		imageView = new ImageView(mainActivity);
		imageView.setImageResource(R.drawable.base);
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(imageView);
			}
		});
	}

	public void createBase(final float x, final float y) {
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				float newY = y;
				imageView.setX(x);
				imageView.setY(newY);
			}
		});
	}

	public float getX() {
		float width = (float) (0.5 * imageView.getWidth());
		return (imageView.getX() - width);
	}

	public float getY() {
		float height = (float) (0.5 * imageView.getHeight());
		return (imageView.getY() - height);
	}

	public void destruct() {
		SoundPlayer.getInstance().start("base_blast");
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().removeView(imageView);
			}
		});
		final ImageView blast = new ImageView(mainActivity);
		blast.setImageResource(R.drawable.blast);

		blast.setX(getX());
		blast.setY(getY());

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(blast);
			}
		});

		final AnimatorSet set = new AnimatorSet();

		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(blast, "alpha", 1.0f, 0.0f);
		fadeOut.setDuration(3000);

		fadeOut.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mainActivity.getLayout().removeView(blast);
					}
				});
			}
		});

		set.play(fadeOut);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				set.start();
			}
		});
	}
}
