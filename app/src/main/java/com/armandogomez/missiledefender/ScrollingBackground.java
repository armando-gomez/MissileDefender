package com.armandogomez.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import static com.armandogomez.missiledefender.MainActivity.screenWidth;
import static com.armandogomez.missiledefender.MainActivity.screenHeight;

class ScrollingBackground {
	private static final String TAG = "ScrollingBackground";
	private Context context;
	private ConstraintLayout layout;
	private ImageView backImageA;
	private ImageView backImageB;
	private long duration;
	private int resId;
	private AnimatorSet animatorTranslate;

	ScrollingBackground(Context context, ConstraintLayout layout, int resId, long duration) {
		this.context = context;
		this.layout = layout;
		this.resId = resId;
		this.duration = duration;

		setupBackground();
	}

	private void setupBackground() {
		backImageA = new ImageView(context);
		backImageB = new ImageView(context);

		ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(screenWidth + getBarHeight(), screenHeight);
		backImageA.setLayoutParams(params);
		backImageB.setLayoutParams(params);

		layout.addView(backImageA);
		layout.addView(backImageB);

		Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
		Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);

		backImageA.setImageBitmap(backBitmapA);
		backImageB.setImageBitmap(backBitmapB);

		backImageA.setAlpha(0.25f);
		backImageB.setAlpha(0.25f);

		backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
		backImageB.setScaleType(ImageView.ScaleType.FIT_XY);

		animateBack();
	}

	private void animateBack() {
		animatorTranslate = new AnimatorSet();

		ValueAnimator translate = ValueAnimator.ofFloat(0.0f, 1.0f);
		translate.setRepeatCount(ValueAnimator.INFINITE);
		translate.setInterpolator(new LinearInterpolator());
		translate.setDuration(duration);

		translate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float progress = (float) animation.getAnimatedValue();
				float width = screenWidth + getBarHeight();

				float a_translationX = width * progress;
				float b_translationX = width * progress - width;

				backImageA.setTranslationX(a_translationX);
				backImageB.setTranslationX(b_translationX);
			}
		});

		animatorTranslate.play(translate);

		animatorTranslate.start();
	}

	public void animateStop() {
		animatorTranslate.cancel();
		layout.removeView(backImageA);
		layout.removeView(backImageB);
	}

	private int getBarHeight() {
		int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return context.getResources().getDimensionPixelSize(resourceId);
		}
		return 0;
	}
}

