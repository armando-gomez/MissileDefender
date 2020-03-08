package com.armandogomez.missledefender;

import android.widget.ImageView;

public class Base {
	private ImageView imageView;

	Base(ImageView imageView) {
		this.imageView = imageView;
	}

	public float getX() {
		return (float) (imageView.getX() + (0.5 * imageView.getWidth()));
	}

	public float getY() {
		return (float) (imageView.getY() + (0.5 * imageView.getHeight()));
	}
}
