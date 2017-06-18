package com.bigstark.interaction;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bigstark on 2017. 3. 29..
 */

public class CircleLoadingView extends View {

    public static final int DURATION_ROTATE_DEFAULT = 800;
    public static final int DURATION_INTERVAL_DEFAULT = 250;

    public static final int RADIUS_DEFAULT = 60;
    public static final int COLOR_DEFAULT = Color.parseColor("#7382C8");
    public static final int ALPHA_FINAL = 51; // 0 <= alpha <= 255, 51 -> 20%


    private int duration = DURATION_ROTATE_DEFAULT;
    private int interval = DURATION_INTERVAL_DEFAULT;

    private int radius = RADIUS_DEFAULT;
    private int color = COLOR_DEFAULT;

    private float frame = 0;

    private ValueAnimator animator;


    private Paint paint = new Paint();

    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValueAnimator();

        if (attrs == null) {
            return;
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView);
        color = ta.getColor(R.styleable.CircleLoadingView_color, COLOR_DEFAULT);
        radius = ta.getDimensionPixelSize(R.styleable.CircleLoadingView_radius, RADIUS_DEFAULT);
        ta.recycle();
    }

    private void initValueAnimator() {
        animator = ValueAnimator.ofInt(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frame = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop(); // if loading view detached from window, animation must be stopped and stop the draw.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // o ㅡㅡ o
        //
        //
        // o     o
        resetPaint(0);
        float[] circleCenter = getHorizontalCenter(width, height, true);
        canvas.drawCircle(circleCenter[0], circleCenter[1], radius, paint);


        // o      o
        //        |
        //        |
        // o      o
        resetPaint(1);
        circleCenter = getVerticalCenter(width, height, true);
        canvas.drawCircle(circleCenter[0], circleCenter[1], radius, paint);


        // o     o
        //
        //
        // o ㅡㅡ o
        resetPaint(2);
        circleCenter = getHorizontalCenter(width, height, false);
        canvas.drawCircle(circleCenter[0], circleCenter[1], radius, paint);


        // o      o
        // ㅣ
        // ㅣ
        // o      o
        resetPaint(3);
        circleCenter = getVerticalCenter(width, height, false);
        canvas.drawCircle(circleCenter[0], circleCenter[1], radius, paint);
    }

    private void resetPaint(int step) {
        paint.reset();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        if (step < 3) {
            paint.setAlpha((int) (255 - (step + frame) * (255 - ALPHA_FINAL) / 3));
        } else {
            paint.setAlpha((int) (ALPHA_FINAL + (255 - ALPHA_FINAL) * frame));
        }
    }

    // "positive" is increasing positively horizontally
    private float[] getHorizontalCenter(int width, int height, boolean positive) {
        float x = (width - 2 * radius) * (positive ? frame : 1 - frame) + radius;
        float y = positive ? radius : height - radius;
        return new float[] {x, y};
    }

    // "negative" is increasing negatively vertically
    private float[] getVerticalCenter(int width, int height, boolean negative) {
        float x = negative ? width - radius : radius;
        float y = (height - 2 * radius) * (negative ? frame : 1 - frame) + radius;
        return new float[] {x, y};
    }


    /**
     * set circle radius
     *
     * @param radius : radius of circle
     */
    public void setRadius(int radius) {
        this.radius = radius;
        postInvalidate();
    }


    /**
     * set circle color
     *
     * @param color : color of circle
     */
    public void setColor(int color) {
        this.color = color;
        postInvalidate();
    }


    /**
     * set rotation duration.
     *
     * It means the duration that one point moves to another point.
     *
     * @param durationRotation
     */
    public void setRotationDuration(int durationRotation) {
        this.duration = durationRotation;
        start();
    }


    /**
     * set interval.
     *
     * It means the break time after move one point.
     *
     * @param interval
     */
    public void setInterval(int interval) {
        this.interval = interval;
        start();
    }


    /**
     * start animation
     */
    public void start() {
        animator.setDuration(duration);
        animator.setStartDelay(interval);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }

    /**
     * stop animation
     */
    public void stop() {
        if (animator.isRunning()) {
            animator.cancel();
        }
    }

}
