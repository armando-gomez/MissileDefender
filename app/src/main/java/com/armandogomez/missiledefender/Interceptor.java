package com.armandogomez.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Interceptor {
	private MainActivity mainActivity;
	private Base base;
	private float endX;
	private float endY;
	private int screenHeight;
	private ImageView imageView;
	private AnimatorSet set = new AnimatorSet();

	Interceptor(final MainActivity mainActivity, Base base, float endX, float endY, int screenHeight) {
		this.mainActivity = mainActivity;
		this.base = base;
		this.endX = endX;
		this.endY = endY;
		this.screenHeight = screenHeight;

		imageView = new ImageView(this.mainActivity);
	}

	public void createInterceptor() {
		imageView.setImageResource(R.drawable.interceptor);

		float startX = base.getX();
		float startY = screenHeight - 70;

		this.endX -= (imageView.getDrawable().getIntrinsicWidth())/2;
		this.endY -= (imageView.getDrawable().getIntrinsicWidth())/2;

		double angle = Math.toDegrees(Math.atan2(endX-startX, endY-startY));
		angle = angle + Math.ceil(-angle/360) * 360;
		Float rotAngle = (float)(190.0f - angle);

		imageView.setX(startX);
		imageView.setY(startY);
		imageView.setZ(-10);
		imageView.setRotation(rotAngle);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(imageView);
			}
		});

		float distance = distanceCalc(startX, startY, endX, endY);

		final ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
		xAnim.setInterpolator(new LinearInterpolator());
		xAnim.setDuration((long) (distance * 2));

		final ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
		yAnim.setInterpolator(new LinearInterpolator());
		yAnim.setDuration((long) (distance * 2));

		xAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mainActivity.getLayout().removeView(imageView);
						makeBlast();
					}
				});
			}
		});

		xAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				SoundPlayer.getInstance().start("launch_interceptor");
			}
		});

		this.set.playTogether(xAnim, yAnim);
	}

	AnimatorSet getSet() { return set; }

	private void makeBlast() {
		SoundPlayer.getInstance().start("interceptor_blast");
		final ImageView blast = new ImageView(mainActivity);
		blast.setImageResource(R.drawable.i_explode);

		float x = imageView.getX();
		float y = imageView.getY();

		x -= (blast.getDrawable().getIntrinsicWidth())/2;
		y -= (blast.getDrawable().getIntrinsicWidth())/2;

		blast.setX(x);
		blast.setY(y);
		blast.setZ(-15);

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

		mainActivity.applyInterceptorBlast(x, y);
	}

	private float distanceCalc(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}
}
