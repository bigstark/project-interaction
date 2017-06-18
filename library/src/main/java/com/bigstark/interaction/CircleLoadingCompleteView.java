package com.bigstark.interaction;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bigstark on 2017. 4. 11..
 */

public class CircleLoadingCompleteView extends View {

    public static final int RADIUS_DEFAULT = 200;
    public static final int STROKE_WIDTH_DEFAULT = 60;
    public static final int PATH_COLOR_DEFAULT = Color.parseColor("#E7E7E7");
    public static final int STROKE_COLOR_DEFAULT = Color.parseColor("#06CA93");
    public static final int CHECK_COLOR_DEFAULT = Color.parseColor("#FFFFFF");
    public static final int LOADING_DURATION_DEFAULT = 1000;
    public static final int COMPLETE_DURATION_DEFAULT = 2000;
    public static final int CHECK_DURATION_DEFAULT = 1000;

    private int radius = RADIUS_DEFAULT;
    private int pathColor = PATH_COLOR_DEFAULT;
    private int strokeWidth = STROKE_WIDTH_DEFAULT;
    private Paint pathPaint;

    private float frame = 0;
    private RectF circleRect;
    private int strokeColor = STROKE_COLOR_DEFAULT;


    private ValueAnimator loadingAnimator;
    private Path loadingPath = new Path();
    private Paint loadingPaint;
    private Path headerTailPath = new Path();
    private Paint headerTailPaint;

    private boolean toBeLoadingComplete = false;
    private boolean isLoadingComplete = false;

    private ValueAnimator completeAnimator;
    private Paint circlePaint = new Paint();
    private CubicBezierInterpolator circleScaleUpInterpolator;
    private CubicBezierInterpolator circleScaleDownInterpolator;

    private boolean isCheckStarted = false;
    private ValueAnimator checkAnimator;
    private Path checkPath = new Path();
    private Paint checkPaint = new Paint();


    public CircleLoadingCompleteView(Context context) {
        this(context, null);
    }

    public CircleLoadingCompleteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingCompleteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPathPaint();
        initLoadingPaint();
        initCheckPaint();

        initLoadingAnimator();
        initCompleteAnimator();
        initCheckAnimator();
    }


    private void initPathPaint() {
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(strokeWidth);
        pathPaint.setColor(pathColor);
    }


    private void initLoadingPaint() {
        loadingPaint = new Paint();
        loadingPaint.setAntiAlias(true);
        loadingPaint.setStyle(Paint.Style.STROKE);
        loadingPaint.setStrokeWidth(strokeWidth);
        loadingPaint.setColor(strokeColor);

        headerTailPaint = new Paint();
        headerTailPaint.setAntiAlias(true);
        headerTailPaint.setStyle(Paint.Style.FILL);
        headerTailPaint.setColor(strokeColor);
    }


    private void initCheckPaint() {
        checkPaint = new Paint();
        checkPaint.setAntiAlias(true);
        checkPaint.setStyle(Paint.Style.STROKE);
        checkPaint.setStrokeWidth(strokeWidth);
        checkPaint.setColor(CHECK_COLOR_DEFAULT);
    }


    private void initLoadingAnimator() {
        loadingAnimator = ValueAnimator.ofInt(0, 1);
        loadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frame = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        loadingAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                if (toBeLoadingComplete) {
                    isLoadingComplete = true;

                    animation.cancel();
                    completeAnimator.start();
                }
            }
        });
        loadingAnimator.setDuration(LOADING_DURATION_DEFAULT);
        loadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }


    private void initCompleteAnimator() {
        completeAnimator = ValueAnimator.ofInt(0, 1);
        completeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frame = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        completeAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isCheckStarted = true;
                checkAnimator.start();
            }
        });
        completeAnimator.setDuration(COMPLETE_DURATION_DEFAULT);

        circleScaleUpInterpolator = new CubicBezierInterpolator(0.41, 0, 0.46, 1);
        circleScaleDownInterpolator = new CubicBezierInterpolator(0.41, 0, 0.3, 1);
    }


    private void initCheckAnimator() {
        checkAnimator = ValueAnimator.ofInt(0, 1);
        checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frame = animation.getAnimatedFraction();
                postInvalidate();
            }
        });
        checkAnimator.setDuration(CHECK_DURATION_DEFAULT);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        if (width == 0) {
            return;
        }

        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, radius, pathPaint);


        updateLoadingPath(centerX, centerY);
        canvas.drawPath(loadingPath, loadingPaint);
        canvas.drawPath(headerTailPath, headerTailPaint);

        if (!isLoadingComplete || frame <= 0.5) {
            return;
        }

        if (isCheckStarted) {
            canvas.drawCircle(centerX, centerY, radius, circlePaint);

            updateCheckPath(width, height);
            canvas.drawPath(checkPath, checkPaint);
        } else {
            updateCompleteCirclePaint();
            canvas.drawCircle(centerX, centerY, getUpdatedCompleteRadius(), circlePaint);
        }
    }

    private void updateLoadingPath(int centerX, int centerY) {
        if (circleRect == null) {
            circleRect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        }


        loadingPath.reset();
        float tailAngle = isLoadingComplete ? getCompleteTailAngle() : getLoadingTailAngle();
        float headerAngle = isLoadingComplete ? getCompleteHeaderAngle() : getLoadingHeaderAngle();
        float sweepAngle = headerAngle - tailAngle;
        loadingPath.addArc(circleRect, tailAngle, sweepAngle);


        headerTailPath.reset();
        float headerCenterX = centerX + (float) Math.sin((headerAngle + 90) * Math.PI / 180) * radius;
        float headerCenterY = centerY - (float) Math.cos((headerAngle + 90) * Math.PI / 180) * radius;
        headerTailPath.addCircle(headerCenterX, headerCenterY, strokeWidth / 2, Path.Direction.CW);

        float tailCenterX = centerX + (float) Math.sin((tailAngle + 90) * Math.PI / 180) * radius;
        float tailCenterY = centerY - (float) Math.cos((tailAngle + 90) * Math.PI / 180) * radius;
        headerTailPath.addCircle(tailCenterX, tailCenterY, strokeWidth / 2, Path.Direction.CW);
    }

    private float getLoadingHeaderAngle() {
        if (frame < (float) 2 / 3) {
            return -90 + 405 * frame;
        } else {
            return 180 + 90 * ((frame - (float) 2 / 3) * 3);
        }
    }


    private float getLoadingTailAngle() {
        if (frame < (float) 1 / 3) {
            return -90;
        }

        return -90 + 540 * (frame - (float) 1 / 3);
    }

    private float getCompleteHeaderAngle() {
        if (frame > 0.5) {
            return 270;
        }

        return -90 + (360 * frame * 2);
    }

    private float getCompleteTailAngle() {
        return -90;
    }


    private void updateCompleteCirclePaint() {
        circlePaint.reset();

        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(strokeColor);

        int alpha = (int) ((frame - 0.5) * 3 * 255);
        alpha = alpha > 255 ? 255 : alpha;
        circlePaint.setAlpha(alpha);
    }


    private int getUpdatedCompleteRadius() {
        float newFrame = frame - 0.5f;
        if (newFrame < (float) 1 / 3) {
            return (int) (radius * (1 + circleScaleUpInterpolator.getBezierCoordinateY(newFrame * 3) * 0.2));
        } else if (newFrame < (float) 2 / 3) {
            newFrame = newFrame - (float) 1 / 3;
            return (int) (radius * (1 + (1 - circleScaleDownInterpolator.getBezierCoordinateY(newFrame * 3)) * 0.2));
        } else {
            return radius;
        }
    }

    private void updateCheckPath(int width, int height) {
        checkPath.reset();
        int fromX = (int) (width * 0.2074);
        int fromY = (int) (height * 0.3102);
        checkPath.moveTo(fromX, fromY);

        int middleX = (int) (width * 0.2638);
        int middleY = (int) (height * 0.3815);

        // frame 0 -> 0.66 --> (41.48, 62.04) -> 52.76, 76.3)
        if (frame < (float) 2 / 3) {
            float newFrame = frame * 3 / 2;
            checkPath.lineTo(fromX + (middleX - fromX) * newFrame, fromY + (middleY - fromY) * newFrame);
        } else {
            checkPath.lineTo(middleX, middleY);
        }

    }



    public void setLoadingComplete(boolean isComplete) {
        toBeLoadingComplete = isComplete;
    }


    public void start() {
        loadingAnimator.start();
    }

}
