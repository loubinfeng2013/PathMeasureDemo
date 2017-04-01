package com.loubinfeng.www.pathmeasuredemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by loubinfeng on 2017/4/1.
 */

public class CirclePathAnimView extends View{

    private Path mDst;//最终截取取来的path，用来绘制
    private Path mPath;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private float mLength;
    private ValueAnimator mValueAnimator;
    private float mStart;
    private float mStop;

    public CirclePathAnimView(Context context) {
        this(context, null);
    }

    public CirclePathAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePathAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.GRAY);
        mPath = new Path();
        mDst = new Path();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null){
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int r = Math.min(w, h)/2;
        mPath.addCircle(r, r, r-5, Path.Direction.CW);
        mPathMeasure = new PathMeasure(mPath,true);
        mLength = mPathMeasure.getLength();
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(2000);
        mValueAnimator.setInterpolator(new AccelerateInterpolator());
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) valueAnimator.getAnimatedValue();
                mStop = value * mLength;
                invalidate();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               mValueAnimator.start();
            }
        },200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDst.reset();
        mDst.lineTo(0,0);//Android硬件加速的一个bug，必须这么设置
        mStart = 0;
        mPathMeasure.getSegment(mStart,mStop,mDst,true);
        canvas.drawPath(mDst,mPaint);
    }
}
