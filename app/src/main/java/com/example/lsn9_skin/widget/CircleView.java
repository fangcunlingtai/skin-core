package com.example.lsn9_skin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.lsn9_skin.R;
import com.example.lsn9_skin.SkinViewSupport;
import com.example.skin_core.utils.SkinResources;

public class CircleView extends View implements SkinViewSupport {

    private AttributeSet attrs;

    private Paint mTextPaint;
//    半径
    private int radius;

    private int colorResId;
    public CircleView(Context context) {
        this(context,null,0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        colorResId = typedArray.getResourceId(R.styleable.CircleView_circleColor,0);
        typedArray.recycle();
        mTextPaint = new Paint();
        mTextPaint.setColor(getResources().getColor(colorResId));
//        开启抗锯齿 平滑文字和圆弧的边缘
        mTextPaint.setAntiAlias(true);
//       设置文本位于相对于原点的中间
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        radius = Math.min(width,height);
        canvas.drawCircle(width,height,radius,mTextPaint);
    }

    public void setCircleColor(int color){
        mTextPaint.setColor(color);
        invalidate();
    }

    @Override
    public void applySkin() {
        if (colorResId != 0){
            int color = SkinResources.getInstance().getColor(colorResId);
            setCircleColor(color);
        }

    }
}
