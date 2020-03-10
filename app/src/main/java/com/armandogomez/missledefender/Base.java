package com.armandogomez.missledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.BitmapDrawable;
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
				float newY = y - imageView.getHeight();
				imageView.setX(x);
				imageView.setY(newY);
			}
		});
	}

	public float getX() {
		return (float) (imageView.getX() + (0.5 * imageView.getWidth()));
	}

	public float getY() {
		return (float) (imageView.getY() + (0.5 * imageView.getHeight()));
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

		blast.setX(imageView.getX());
		blast.setY(imageView.getY());

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
