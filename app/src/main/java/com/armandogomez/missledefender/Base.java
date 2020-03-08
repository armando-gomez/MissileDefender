package com.armandogomez.missledefender;

import android.widget.ImageView;

public class Base {
	private ImageView imageView;

	Base(ImageView imageView) {
		this.imageView = imageView;
		imageView.setX((float) (imageView.getX() + (0.5 * imageView.getWidth())));
		imageView.setY((float) (imageView.getY() + (0.5 * imageView.getHeight())));
	}

	public float getX() {
		return imageView.getX();
	}

	public float getY() {
		return imageView.getY();
	}
}
