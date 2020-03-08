package com.armandogomez.missledefender;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class Missile {
	private static final String TAG = "Missle";
	private MainActivity mainActivity;
	private long screenTime;
	private int screenWidth, screenHeight;
	private ImageView imageView;
	private AnimatorSet set = new AnimatorSet();

	Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
		this.screenTime = screenTime;
		this.mainActivity = mainActivity;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		createMissile();
	}

	void createMissile() {
		imageView = new ImageView(mainActivity);
		imageView.setImageResource(R.drawable.missile);

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

		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mainActivity.getLayout().addView(imageView);
			}
		});

		final ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
		xAnim.setInterpolator(new LinearInterpolator());
		xAnim.setDuration(screenTime);

		final ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
		yAnim.setInterpolator(new LinearInterpolator());
		yAnim.setDuration(screenTime);

		xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if(imageView.getY() > (screenHeight * 0.85)) {
					xAnim.cancel();
					yAnim.cancel();
					makeGroundBlast();
					mainActivity.removeMissile(Missile.this);
				}
			}
		});

		set.playTogether(xAnim, yAnim);
	}

	AnimatorSet getSet() {
		return set;
	}

	void stop() {
		set.cancel();
	}

	void makeGroundBlast() {}
}
