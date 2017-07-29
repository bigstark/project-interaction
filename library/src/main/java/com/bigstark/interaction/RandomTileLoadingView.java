package com.bigstark.interaction;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.Random;

/**
 * Created by bigstark on 2017. 7. 12..
 */

public class RandomTileLoadingView extends View {

    public static final int DURATION_TILE_MOVE = 180;
    public static final int INTERVAL_TILE_MOVE = 60;
    public static final int COLOR_DEFAULT = Color.parseColor("#7382C8");

    private boolean isRunning = false;

    private float tileMargin;


    private ValueAnimator animator;
    private float frame = 0;

    private Paint paint = new Paint();
    private int color = COLOR_DEFAULT;


    private int[] from = {1, 0};
    private int[] to = {0, 0};
    private static final int[][] TILES = {
            {0, 0}, {0, 1}, {0, 2},
            {1, 0}, {1, 1}, {1, 2},
            {2, 0}, {2, 1}, {2, 2}
    };


    public RandomTileLoadingView(Context context) {
        this(context, null);
    }

    public RandomTileLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandomTileLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValueAnimator();
        initPaint();

        if (attrs == null) {
            return;
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView);
        int marginDefault = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        tileMargin = ta.getDimensionPixelSize(R.styleable.RandomTileLoadingView_tileMargin, marginDefault);
        color = ta.getColor(R.styleable.RandomTileLoadingView_tileColor, COLOR_DEFAULT);
        ta.recycle();

        initPaint();
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
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                frame = 0;
                int[] prevTo = to;

                to = from;
                int[][] fromCandidates = {
                        {to[0] + 1, to[1]},
                        {to[0] - 1, to[1]},
                        {to[0], to[1] + 1},
                        {to[0], to[1] - 1},
                };
                Random random = new Random(System.currentTimeMillis());
                while(true) {
                    int[] fromCandidate = fromCandidates[Math.abs(random.nextInt()) % fromCandidates.length];
                    if (fromCandidate[0] < 0 || fromCandidate[0] > 2) {
                        continue;
                    }

                    if (fromCandidate[1] < 0 || fromCandidate[1] > 2) {
                        continue;
                    }

                    if (fromCandidate[0] == prevTo[0] && fromCandidate[1] == prevTo[1]) {
                        continue;
                    }

                    from = fromCandidate;
                    break;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isRunning) {
                    start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void initPaint() {
        paint.reset();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
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

        float size = (width > height ? height : width) - tileMargin * 2;
        float tileSize = size / 3;


        // draw tiles except from and to
        for (int[] tile : TILES) {
            if ((tile[0] == from[0] && tile[1] == from[1]) ||
                    (tile[0] == to[0] && tile[1] == to[1])) {
                continue;
            }

            float left = tile[0] * (tileSize + tileMargin);
            float top = tile[1] * (tileSize + tileMargin);
            canvas.drawRect(left, top, left + tileSize, top + tileSize, paint);
        }

        // draw from
        float fromLeft = (from[0] + (to[0] - from[0]) * frame) * (tileSize + tileMargin);
        float fromTop = (from[1] + (to[1] - from[1]) * frame) * (tileSize + tileMargin);
        canvas.drawRect(fromLeft, fromTop, fromLeft + tileSize, fromTop + tileSize, paint);
    }

    /**
     * set tile color
     *
     * @param color : color of tile
     */
    public void setColor(int color) {
        this.color = color;
        postInvalidate();
    }

    /**
     * set tile margin
     *
     * @param tileMargin : size of tile margin
     */
    public void setTileMargin(int tileMargin) {
        this.tileMargin = tileMargin;
        postInvalidate();
    }

    /**
     * start animation
     */
    public void start() {
        isRunning = true;

        if (animator.isRunning()){
            animator.cancel();
        }

        animator.setDuration(DURATION_TILE_MOVE);
        animator.setStartDelay(INTERVAL_TILE_MOVE);
        animator.start();
    }

    /**
     * stop animation
     */
    public void stop() {
        isRunning = false;

        if (animator.isRunning()) {
            animator.cancel();
        }
    }
}
