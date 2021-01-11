package com.ibo_android.sdmusicplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

	 
	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SquareImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.widget.ImageView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		
		// int width = getMeasuredWidth();
		//   setMeasuredDimension(width, width);
		    
		    
		    int measuredWidth = getMeasuredWidth();
		    int measuredHeight = getMeasuredHeight();
		    if (measuredWidth > measuredHeight) {
		        setMeasuredDimension(measuredHeight, measuredHeight);
		    } else {
		        setMeasuredDimension(measuredWidth, measuredWidth);

		    } 	
		
	}
	

}
