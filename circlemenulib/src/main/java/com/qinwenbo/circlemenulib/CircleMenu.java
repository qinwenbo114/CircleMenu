package com.qinwenbo.circlemenulib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

/**
 * Created by Qinwenbo on 2017/2/28 9:49.
 */

public class CircleMenu extends View {

    float menuCircleRadius, circlePathRadius;
    int menuCount;
    float startAngle, openAngle;
    float viewCircleCenterX, viewCircleCenterY;
    double startRadian, openRadian;
    Paint bitmapPaint;
    PathPoint[] runningPoints;
    PathPoint[] startPathPoints;
    PathPoint[] endPathPoints;
    List<MenuIcon> menuIcons;
    int currentIndex, lastIndex, clickIndex, period;
    int menuStatus;
    final private static int OPENING = 0;
    final private static int OPENED = 1;
    final private static int CLOSING = 2;
    final private static int CLOSED = 3;
    boolean isClockwise;
    float[] smallCircleBorder, bigCircleBorder, viewBorder;
    Paint examplePaint;
    RectF exampleRectF;
    Rect bitmapRect;
    RectF mDestRect;
    OnMenuSwitchListener onMenuSwitchListener;

    public CircleMenu(Context context) {
        super(context);
        init();
        prepareSize();
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        // get init data from xml
        init();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleMenu);
        menuCircleRadius = typedArray.getDimensionPixelOffset(R.styleable.CircleMenu_menuIconRadius, 100);
        circlePathRadius = typedArray.getDimensionPixelOffset(R.styleable.CircleMenu_circlePathRadius, 500);
        period = typedArray.getInt(R.styleable.CircleMenu_period, 500);
        isClockwise = typedArray.getBoolean(R.styleable.CircleMenu_isClockwise, false);
        openAngle = typedArray.getFloat(R.styleable.CircleMenu_openAngle, 180);
        startAngle = typedArray.getFloat(R.styleable.CircleMenu_startAngle, 0);
        startAngle = startAngle % 360;
        if (startAngle < 0) startAngle += 360;
        openAngle = openAngle % 360;
        if (openAngle < 0) openAngle += 360;
        startRadian = startAngle / 180 * Math.PI;
        openRadian = openAngle / 180 * Math.PI;
        typedArray.recycle();
        prepareSize();
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        prepareSize();
    }

    private void init() {

        circlePathRadius = 500;
        menuCircleRadius = 100;
        period = 500;
        menuCount = 0;
        startRadian = 0;
        currentIndex = 0;
        lastIndex = 0;
        openRadian = Math.PI * 2;
        menuStatus = CLOSED;
        isClockwise = false;
        exampleRectF = new RectF();
        bitmapRect = new Rect();
        mDestRect = new RectF();

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);

        examplePaint = new Paint();
        examplePaint.setAntiAlias(true);
        examplePaint.setColor(Color.BLACK);
        examplePaint.setStyle(Paint.Style.FILL);

    }

    private void prepareSize() {

        smallCircleBorder = calculateSmallCircleBorder(isClockwise, startRadian, circlePathRadius / 2);
        bigCircleBorder = calculateBigCircleBorder(isClockwise, startRadian, openRadian, circlePathRadius);
        viewBorder = new float[4];
        viewBorder[0] = Math.min(Math.min(smallCircleBorder[0], bigCircleBorder[0]), -menuCircleRadius);
        viewBorder[1] = Math.min(Math.min(smallCircleBorder[1], bigCircleBorder[1]), -menuCircleRadius);
        viewBorder[2] = Math.max(Math.max(smallCircleBorder[2], bigCircleBorder[2]), menuCircleRadius);
        viewBorder[3] = Math.max(Math.max(smallCircleBorder[3], bigCircleBorder[3]), menuCircleRadius);
    }

    /**
     * Set menu icons, this function should be called by user once this view is initialized.
     * @param menuIcons menu icons list, there should be two menus at least
     */
    public void setMenuIcons(List<MenuIcon> menuIcons) {
        this.menuIcons = menuIcons;
        menuCount = menuIcons.size();

        startPathPoints = new PathPoint[menuCount - 1];
        endPathPoints = new PathPoint[menuCount - 1];
        runningPoints = new PathPoint[menuCount - 1];
        double eachRadian;
        if (menuCount > 2) {
            eachRadian = openRadian / (menuCount - 2);
        } else {
            eachRadian = openRadian;
        }
        for (int i = 0; i < menuCount - 1; i++) {
            startPathPoints[i] = new PathPoint(0, 0, startRadian);
            endPathPoints[i] = new PathPoint(0, 0, eachRadian * i + startRadian);
            runningPoints[i] = new PathPoint(0, 0, 0);
        }

        clickIndex = menuCount - 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("measure", "width" + widthSize + " height:" + heightSize);

        int width, height;

        if (widthMode == MeasureSpec.AT_MOST) {
            // wrap_content
            width = (int) (viewBorder[2] - viewBorder[0]);
            Log.d("width", width+"");

        } else {
            // math_parents
            width = widthSize;
            Log.d("width1", width+"");
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            // wrap_content
            height = (int) (viewBorder[3] - viewBorder[1]);
            Log.d("height", height+"");
        } else {
            // math_parents
            height = heightSize;
            Log.d("height1", height+"");
        }

        setMeasuredDimension(width, height);
    }

    /**
     * Calculate small sector border
     * @param isClockwise sweep direction
     * @param startRadian start radian of sweep area
     * @param radius big circle radius
     * @return the coordinates of min rect which surround the small sector. float[left, top, right, bottom]
     */
    private float[] calculateSmallCircleBorder(boolean isClockwise, double startRadian, float radius) {
        float[] coordinate = new float[4]; // left, top, right, bottom
        double modeStartRadian = startRadian % (.5 * Math.PI);
        int quadrant = (int) (startRadian / (.5 * Math.PI));
        float modeLeft, modeTop, modeRight, modeBottom;
        if (isClockwise) {
            modeLeft = 0;
            modeTop = (float) (radius * (Math.sin(modeStartRadian) - 1));
            modeRight = (float) (radius * (Math.cos(modeStartRadian) + 1));
            modeBottom = (float) (radius * 2 * Math.sin(modeStartRadian));
        } else {
            modeLeft = (float) (radius * (Math.cos(modeStartRadian) - 1));
            modeTop = 0;
            modeRight = (float) (radius * 2 * Math.sin(modeStartRadian));
            modeBottom = (float) (radius * (Math.sin(modeStartRadian) + 1));
        }
        coordinate[(quadrant) % 4] = (modeLeft - menuCircleRadius) * (quadrant > 1 ? -1 : 1);
        coordinate[(1 + quadrant) % 4] = (modeTop - menuCircleRadius) * ((quadrant == 1 || quadrant == 2) ? -1 : 1);
        coordinate[(2 + quadrant) % 4] = (modeRight + menuCircleRadius) * (quadrant > 1 ? -1 : 1);
        coordinate[(3 + quadrant) % 4] = (modeBottom + menuCircleRadius) * ((quadrant == 1 || quadrant == 2) ? -1 : 1);
        return coordinate;
    }

    /**
     * Calculate big sector border
     * @param isClockwise sweep direction
     * @param startRadian start radian of sweep area
     * @param openRadian open radian of sweep area
     * @param radius big circle radius
     * @return the coordinates of min rect which surround the big sector. float[left, top, right, bottom]
     */
    private float[] calculateBigCircleBorder(boolean isClockwise, double startRadian, double openRadian, float radius) {
        float[] coordinate = new float[4]; // left, top, right, bottom
        double modeStartRadian = startRadian % (.5 * Math.PI);
        double modeEndRadian = isClockwise ? (modeStartRadian + openRadian) : (modeStartRadian - openRadian);
        int quadrant = (int) (startRadian / (.5 * Math.PI));
        float modeLeft, modeTop, modeRight, modeBottom;
        if (isClockwise) {
            if (modeEndRadian <= .5 * Math.PI) {
                modeLeft = 0;
                modeRight = (float) (radius * Math.cos(modeStartRadian));
                modeTop = 0;
                modeBottom = (float) (radius * Math.sin(modeEndRadian));
            } else if (modeEndRadian <= Math.PI) {
                modeLeft = (float) (radius * Math.cos(modeEndRadian));
                modeTop = 0;
                modeRight = (float) (radius * Math.cos(modeStartRadian));
                modeBottom = radius;
            } else if (modeEndRadian <= 1.5 * Math.PI) {
                modeLeft = - radius;
                modeTop = (float) (radius * Math.sin(modeEndRadian));
                modeRight = (float) (radius * Math.cos(modeStartRadian));
                modeBottom = radius;
            } else if (modeEndRadian <= 2 * Math.PI) {
                modeLeft = - radius;
                modeTop = - radius;
                modeRight = (float) Math.max(radius * Math.cos(modeStartRadian), radius * Math.cos(modeEndRadian));
                modeBottom = radius;
            } else {
                modeBottom = radius;
                modeLeft = -radius;
                modeRight = radius;
                modeTop = -radius;
            }
        } else {
            if (modeEndRadian > 0) {
                modeLeft = 0;
                modeTop = 0;
                modeRight = (float) (radius * Math.cos(modeEndRadian));
                modeBottom = (float) (radius * Math.sin(modeStartRadian));
            } else if (modeEndRadian > -.5 * Math.PI) {
                modeLeft = 0;
                modeTop = (float) (radius * Math.sin(modeEndRadian));
                modeRight = radius;
                modeBottom = (float) (radius * Math.sin(modeStartRadian));
            } else if (modeEndRadian > - Math.PI) {
                modeLeft = (float) (radius * Math.cos(modeEndRadian));
                modeTop = - radius;
                modeRight = radius;
                modeBottom = (float) (radius * Math.sin(modeStartRadian));
            } else if (modeEndRadian > - 1.5 * Math.PI) {
                modeLeft = - radius;
                modeTop = - radius;
                modeBottom = (float) Math.max(radius * Math.sin(modeStartRadian), radius * Math.sin(modeEndRadian));
                modeRight = radius;
            } else {
                modeLeft = - radius;
                modeTop = - radius;
                modeRight = radius;
                modeBottom = radius;
            }
        }
        coordinate[(quadrant) % 4] = (modeLeft - menuCircleRadius) * (quadrant > 1 ? -1 : 1);
        coordinate[(1 + quadrant) % 4] = (modeTop - menuCircleRadius) * ((quadrant == 1 || quadrant == 2) ? -1 : 1);
        coordinate[(2 + quadrant) % 4] = (modeRight + menuCircleRadius) * (quadrant > 1 ? -1 : 1);
        coordinate[(3 + quadrant) % 4] = (modeBottom + menuCircleRadius) * ((quadrant == 1 || quadrant == 2) ? -1 : 1);
        return coordinate;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //viewCircleCenterX = getWidth() / 2;
        //viewCircleCenterY = getHeight() / 2;
        viewCircleCenterX = getWidth() * (- viewBorder[0]) / (viewBorder[2] - viewBorder[0]);
        viewCircleCenterY = getHeight() * (- viewBorder[1]) / (viewBorder[3] - viewBorder[1]);
        if (runningPoints != null && menuStatus == CLOSED) {
            for (int i = 0; i < runningPoints.length; i++) {
                runningPoints[i].set(viewCircleCenterX, viewCircleCenterY);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawCircle(viewCircleCenterX, viewCircleCenterY, menuCircleRadius, paint);
        if (menuCount == 0) {
            exampleRectF.top = viewCircleCenterY - circlePathRadius - menuCircleRadius;
            exampleRectF.left = viewCircleCenterX - circlePathRadius - menuCircleRadius;
            exampleRectF.bottom = viewCircleCenterY + circlePathRadius + menuCircleRadius;
            exampleRectF.right = viewCircleCenterX + circlePathRadius + menuCircleRadius;
            if (isClockwise) {
                canvas.drawArc(exampleRectF, startAngle, openAngle, true, examplePaint);
            } else {
                canvas.drawArc(exampleRectF, startAngle - openAngle, openAngle, true, examplePaint);
            }
            exampleRectF.top = (float) (viewCircleCenterY + circlePathRadius * .5 * (Math.sin(startRadian) - 1) - menuCircleRadius);
            exampleRectF.left = (float) (viewCircleCenterX + circlePathRadius * .5 * (Math.cos(startRadian) - 1) - menuCircleRadius);
            exampleRectF.right = (float) (viewCircleCenterX + circlePathRadius * .5 * (Math.cos(startRadian) + 1) + menuCircleRadius);
            exampleRectF.bottom = (float) (viewCircleCenterY + circlePathRadius * .5 * (Math.sin(startRadian) + 1) + menuCircleRadius);
            if (isClockwise) {
                canvas.drawArc(exampleRectF, startAngle - 180, 180, true, examplePaint);
            } else {
                canvas.drawArc(exampleRectF, startAngle, 180, true, examplePaint);
            }
            canvas.drawCircle(viewCircleCenterX, viewCircleCenterY, menuCircleRadius, examplePaint);
        } else {
            if (menuStatus == OPENING || menuStatus == OPENED) {
                for (int i = 0; i < runningPoints.length; i++) {
                    //canvas.drawCircle(runningPoints[i].x, runningPoints[i].y, menuCircleRadius * runningPoints[i].fraction, paints[i]);
                    //Log.v("fraction "+ i, runningPoints[i].fraction + "");
                    Bitmap bitmap;
                    if (i < currentIndex) {
                        bitmap = ((BitmapDrawable) menuIcons.get(i).getDrawable()).getBitmap();
                    } else {
                        bitmap = ((BitmapDrawable) menuIcons.get(i + 1).getDrawable()).getBitmap();
                    }
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();
                    bitmapRect.set(0, 0, bitmapWidth, bitmapHeight);
                    float mLeft = runningPoints[i].x - menuCircleRadius * runningPoints[i].fraction;
                    float mTop = runningPoints[i].y - menuCircleRadius * runningPoints[i].fraction;
                    mDestRect.set(mLeft, mTop, mLeft + menuCircleRadius * 2 * runningPoints[i].fraction, mTop + menuCircleRadius * 2 * runningPoints[i].fraction);
                    canvas.drawBitmap(bitmap, bitmapRect, mDestRect, bitmapPaint);
                }

                Bitmap bitmap = ((BitmapDrawable) menuIcons.get(currentIndex).getDrawable()).getBitmap();
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                bitmapRect.left = 0;
                bitmapRect.top = 0;
                bitmapRect.right = bitmapWidth;
                bitmapRect.bottom = bitmapHeight;
                float mLeft = viewCircleCenterX - menuCircleRadius;
                float mTop = viewCircleCenterY - menuCircleRadius;
                mDestRect.top = mTop;
                mDestRect.left = mLeft;
                mDestRect.right = mLeft + menuCircleRadius * 2;
                mDestRect.bottom = mTop + menuCircleRadius * 2;
                canvas.drawBitmap(bitmap, bitmapRect, mDestRect, bitmapPaint);
            } else if (menuStatus == CLOSING || menuStatus == CLOSED) {
                for (int i = 0; i < runningPoints.length; i++) {
                    Bitmap bitmap;
                    if (i < lastIndex) {
                        bitmap = ((BitmapDrawable) menuIcons.get(i).getDrawable()).getBitmap();
                    } else {
                        bitmap = ((BitmapDrawable) menuIcons.get(i + 1).getDrawable()).getBitmap();
                    }
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();
                    bitmapRect.set(0, 0, bitmapWidth, bitmapHeight);
                    float mLeft = runningPoints[i].x - menuCircleRadius * runningPoints[i].fraction;
                    float mTop = runningPoints[i].y - menuCircleRadius * runningPoints[i].fraction;
                    if (i == clickIndex) {
                        mDestRect.set(runningPoints[i].x - menuCircleRadius, runningPoints[i].y - menuCircleRadius, runningPoints[i].x + menuCircleRadius, runningPoints[i].y + menuCircleRadius);
                    } else {
                        mDestRect.set(mLeft, mTop, mLeft + menuCircleRadius * 2 * runningPoints[i].fraction, mTop + menuCircleRadius * 2 * runningPoints[i].fraction);
                    }
                    canvas.drawBitmap(bitmap, bitmapRect, mDestRect, bitmapPaint);
                }

                Bitmap bitmap = ((BitmapDrawable) menuIcons.get(lastIndex).getDrawable()).getBitmap();
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                bitmapRect.set(0, 0, bitmapWidth, bitmapHeight);
                float mLeft = viewCircleCenterX - menuCircleRadius;
                float mTop = viewCircleCenterY - menuCircleRadius;
                if (lastIndex == currentIndex) {
                    mDestRect.set(mLeft, mTop, mLeft + menuCircleRadius * 2, mTop + menuCircleRadius * 2);
                } else {
                    mDestRect.set(viewCircleCenterX - menuCircleRadius * runningPoints[0].fraction, viewCircleCenterY - menuCircleRadius * runningPoints[0].fraction, viewCircleCenterX + menuCircleRadius * runningPoints[0].fraction, viewCircleCenterY + menuCircleRadius * runningPoints[0].fraction);
                }
                canvas.drawBitmap(bitmap, bitmapRect, mDestRect, bitmapPaint);
            }
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // save menu index and open status
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("currentIndex", currentIndex);
        bundle.putInt("menuStatus", menuStatus);
        bundle.putInt("lastIndex", lastIndex);
        bundle.putInt("clickIndex", clickIndex);
        bundle.putParcelableArray("runningPoints", runningPoints);
        bundle.putParcelableArray("endPoints", endPathPoints);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            currentIndex = bundle.getInt("currentIndex");
            menuStatus = bundle.getInt("menuStatus");
            lastIndex = bundle.getInt("lastIndex");
            clickIndex = bundle.getInt("clickIndex");
            state = bundle.getParcelable("superState");
            runningPoints = (PathPoint[]) bundle.getParcelableArray("runningPoints");
            endPathPoints = (PathPoint[]) bundle.getParcelableArray("endPoints");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (inCircleArea(menuCircleRadius, viewCircleCenterX, viewCircleCenterY, event.getX(), event.getY())) {
                    if (menuStatus == CLOSED) {
                        openMenu();
                        lastIndex = currentIndex;
                    } else if (menuStatus == OPENED) {
                        closeMenu();
                    }
                    clickIndex = endPathPoints.length;
                } else if (menuStatus == OPENED) {
                    for (int i = 0; i < endPathPoints.length; i++) {
                        if (inCircleArea(menuCircleRadius, endPathPoints[i].x, endPathPoints[i].y, event.getX(), event.getY())) {
                            if (i < currentIndex) {
                                currentIndex = i;
                            } else {
                                currentIndex = i + 1;
                            }
                            clickIndex = i;
                            closeMenu();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    /**
     * Checking if the specified point is in the specified circle area.
     * @param circleRadius circle radius
     * @param circleCenterX X-coordinate of circle origin
     * @param circleCenterY Y-coordinate of circle origin
     * @param x X-coordinate of specified point
     * @param y Y-coordinate of specified point
     * @return True: n area, False: not in area
     */
    private boolean inCircleArea(float circleRadius, float circleCenterX, float circleCenterY, float x, float y) {
        float x1 = x - circleCenterX;
        float y1 = y - circleCenterY;
        return (x1 * x1 + y1 * y1 < circleRadius * circleRadius);
    }

    /**
     * Call this function to open menu.
     */
    private void openMenu() {

        ObjectAnimator animator = ObjectAnimator.ofObject(this, "path", new CirclePathEvaluator(circlePathRadius, viewCircleCenterX, viewCircleCenterY, CirclePathEvaluator.OPEN, isClockwise), startPathPoints, endPathPoints);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(period);
        animator.start();
        //menuOpening = true;
        menuStatus = OPENING;
        if (onMenuSwitchListener != null) {
            onMenuSwitchListener.onMenuSwitch(OPENING, currentIndex);
        }
    }

    /**
     * Call this function to close menu.
     */
    private void closeMenu() {

        ObjectAnimator animator = ObjectAnimator.ofObject(this, "path", new CirclePathEvaluator(circlePathRadius, viewCircleCenterX, viewCircleCenterY, CirclePathEvaluator.CLOSE, isClockwise), startPathPoints, endPathPoints);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(period);
        animator.start();
        //menuOpening = false;
        menuStatus = CLOSING;
        if (onMenuSwitchListener != null) {
            onMenuSwitchListener.onMenuSwitch(CLOSING, currentIndex);
        }
    }

    /**
     * Reflection method called by openMenu or closeMenu.
     * @param pathPoints start and end points of animation.
     */
    public void setPath(PathPoint[] pathPoints) {

        runningPoints = pathPoints;
        if (pathPoints[0].fraction == 1) {
            if (menuStatus == OPENING) {
                menuStatus = OPENED;
                for (int i = 0; i < pathPoints.length; i ++) {
                    endPathPoints[i].set(pathPoints[i].getX(), pathPoints[i].getY());
                }

                if (onMenuSwitchListener != null) {
                    onMenuSwitchListener.onMenuSwitch(OPENED, currentIndex);
                }
            }
        } else if (pathPoints[0].fraction == 0) {
            if (menuStatus == CLOSING) {
                menuStatus = CLOSED;
                if (onMenuSwitchListener != null) {
                    onMenuSwitchListener.onMenuSwitch(CLOSED, currentIndex);
                }
            }
        }
        postInvalidate();

    }

    /**
     * Switch menu.
     * @param menuIndex the index you added in MenuIcon List
     */
    public void switchMenu(int menuIndex) {
        if (menuStatus == CLOSED) {
            lastIndex = menuIndex;
            currentIndex = menuIndex;
            postInvalidate();
            if (onMenuSwitchListener != null) {
                onMenuSwitchListener.onMenuSwitch(CLOSED, currentIndex);
            }
        } else if (menuStatus == OPENED) {
            if (menuIndex > currentIndex && menuIndex < menuCount) {
                clickIndex = menuIndex - 1;
            } else if (menuIndex < currentIndex) {
                clickIndex = menuIndex;
            } else {
                clickIndex = endPathPoints.length;
            }
            currentIndex = menuIndex;
            closeMenu();
        }
    }

    /**
     * Interface definition for a callback to be invoked when menu is switched.
     */
    public interface OnMenuSwitchListener {
        /**
         * Called when menu is switched.
         * @param menuStatus 0:OPENING, 2:OPENED, 3:CLOSING, 4:CLOSED
         * @param currentMenuIndex The index you added in MenuIcon List.
         */
        void onMenuSwitch(int menuStatus, int currentMenuIndex);
    }

    /**
     * Register a callback to be invoked when menu is switched.
     * @param onMenuSwitchListener The callback that will run
     */
    public void setOnMenuSwitchListener(OnMenuSwitchListener onMenuSwitchListener) {
        this.onMenuSwitchListener = onMenuSwitchListener;
    }

    /**
     * Get current menu index.
     * @return current menu index
     */
    public int getCurrentMenuIndex() {
        return currentIndex;
    }
}
