package com.armandogomez.missledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {
	private static final String TAG = "Missile";
	private MainActivity mainActivity;
	private long screenTime;
	private int screenWidth, screenHeight;
	public ImageView imageView;
	private AnimatorSet set = new AnimatorSet();

	Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
		this.screenTime = screenTime;
		this.mainActivity = mainActivity;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		imageView = new ImageView(mainActivity);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(imageView);
				imageView.setImageResource(R.drawable.missile);
			}
		});
	}

	public void createMissile() {
		int startX = (int) (Math.random() * screenWidth);
		int endX =  (int) (Math.random() * screenWidth);

		int startY = -100;
		int endY = screenHeight;

		startX -= (imageView.getDrawable().getIntrinsicWidth())/2;
		startY -= (imageView.getDrawable().getIntrinsicWidth())/2;

		double angle = Math.toDegrees(Math.atan2(endX-startX, endY-startY));
		angle = angle + Math.ceil(-angle/360) * 360;
		Float rotAngle = (float)(190.0f - angle);

		imageView.setX(startX);
		imageView.setY(startY);
		imageView.setZ(-10);
		imageView.setRotation(rotAngle);

		final ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
		xAnim.setInterpolator(new LinearInterpolator());
		xAnim.setDuration(screenTime);

		final ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
		yAnim.setInterpolator(new LinearInterpolator());
		yAnim.setDuration(screenTime);

		xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(imageView.getY() > (screenHeight * 0.85)) {
							set.cancel();
							makeGroundBlast();
							mainActivity.removeMissile(Missile.this);
						}
						Log.d(TAG, "run: NUM VIEWS " +
								mainActivity.getLayout().getChildCount());
					}
				});
			}
		});

		xAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				SoundPlayer.getInstance().start("launch_missile");
			}
		});

		set.playTogether(xAnim, yAnim);
	}

	AnimatorSet getSet() {
		return set;
	}

	void stop() {
		mainActivity.removeMissile(this);
		set.cancel();
	}

	void makeGroundBlast() {
		final ImageView explode = new ImageView(mainActivity);
		explode.setImageResource(R.drawable.explode);

		float x = imageView.getX();
		float y = imageView.getY();

		x -= (explode.getDrawable().getIntrinsicWidth())/2;
		y -= (explode.getDrawable().getIntrinsicWidth())/2;

		explode.setX(x);
		explode.setY(y);
		explode.setZ(-15);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(explode);
			}
		});

		final AnimatorSet explodeSet = new AnimatorSet();

		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(explode, "alpha", 1.0f, 0.0f);
		fadeOut.setDuration(3000);

		fadeOut.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mainActivity.getLayout().removeView(explode);
					}
				});
			}
		});

		explodeSet.play(fadeOut);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				explodeSet.start();
			}
		});

		mainActivity.applyMissileBlast(x, y);
	}

	public void interceptorBlast() {
		mainActivity.removeMissile(this);
		final ImageView explode = new ImageView(mainActivity);
		explode.setImageResource(R.drawable.explode);

		explode.setX(imageView.getX());
		explode.setY(imageView.getY());

		stop();

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(explode);
			}
		});

		final AnimatorSet explodeSet = new AnimatorSet();

		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(explode, "alpha", 1.0f, 0.0f);
		fadeOut.setDuration(3000);

		fadeOut.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mainActivity.getLayout().removeView(explode);
					}
				});
			}
		});

		explodeSet.play(fadeOut);

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				explodeSet.start();
			}
		});
	}
}
