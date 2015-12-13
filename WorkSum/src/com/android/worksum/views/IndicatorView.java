package com.android.worksum.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class IndicatorView extends TextView{

	public IndicatorView(Context context,int iconId,int textId) {
		super(context);
		setText(context.getString(textId));
		Drawable d = context.getResources().getDrawable(iconId);
		setCompoundDrawables(null, d, null, null);
	}

}
