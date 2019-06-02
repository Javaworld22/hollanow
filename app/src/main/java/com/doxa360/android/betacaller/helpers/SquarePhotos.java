package com.doxa360.android.betacaller.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by Apple on 13/05/15.
 */
public class SquarePhotos extends ImageView {
  public SquarePhotos(Context context) {
    super(context);
  }

  public SquarePhotos(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
  }

  public SquarePhotos(Context context, AttributeSet attributeSet, int defStyle) {
    super(context, attributeSet, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
  }
}
