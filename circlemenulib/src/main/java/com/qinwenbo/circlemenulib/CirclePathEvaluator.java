package com.qinwenbo.circlemenulib;

import android.animation.TypeEvaluator;

/**
 * Created by Qinwenbo on 2017/3/2 10:15.
 */

public class CirclePathEvaluator implements TypeEvaluator<PathPoint[]> {

    private float circleRadius, circleCenterX, circleCenterY;
    private boolean isClockwise, option;
    public static final boolean OPEN = true;
    public static final boolean CLOSE = false;
    public static final boolean ANTI_CLOCKWISE = true;
    public static final boolean CLOCKWISE = false;

    public CirclePathEvaluator(float circleRadius, float circleCenterX, float circleCenterY, boolean option, boolean isClockwise) {
        this.circleRadius = circleRadius;
        this.circleCenterX = circleCenterX;
        this.circleCenterY = circleCenterY;
        this.option = option;
        this.isClockwise = isClockwise;
    }

    @Override
    public PathPoint[] evaluate(float fraction, PathPoint[] startValue, PathPoint[] endValue) {

        float x, y;
        float t;
        if (option == OPEN) {
            t = fraction;
        } else {
            t = 1 - fraction;
        }
        PathPoint[] pathPoints = new PathPoint[startValue.length];
        if (startValue.length == endValue.length) {
            for (int i = 0; i < startValue.length; i ++) {
                // calculate the length of big arc
                double bigArcLength = circleRadius * (endValue[i].getRadian() - startValue[i].getRadian());
                double smallArcLength = circleRadius * Math.PI * 0.5;
                double totalLength = bigArcLength + smallArcLength;

                float bigCircleRadius = circleRadius;
                float smallCircleRadius = circleRadius * 0.5f;

                if (t < (smallArcLength / totalLength)) {
                    // In small sector
                    float firstCircleX = (float) (circleCenterX + smallCircleRadius * Math.cos(startValue[i].getRadian()));
                    float firstCircleY = (float) (circleCenterY + smallCircleRadius * Math.sin(startValue[i].getRadian()));
                    double openRadian = t * totalLength / smallCircleRadius;
                    if (!isClockwise) {
                        x = firstCircleX - (float) (smallCircleRadius * Math.cos(startValue[i].getRadian() - openRadian));
                        y = firstCircleY - (float) (smallCircleRadius * Math.sin(startValue[i].getRadian() - openRadian));
                    } else {
                        x = firstCircleX - (float) (smallCircleRadius * Math.cos(openRadian + startValue[i].getRadian()));
                        y = firstCircleY - (float) (smallCircleRadius * Math.sin(openRadian + startValue[i].getRadian()));
                    }
                    pathPoints[i] = new PathPoint(x, y, t);

                } else {
                    // In big sector
                    float thirdCircleX = circleCenterX;
                    float thirdCircleY = circleCenterY;
                    double openRadian = (t * totalLength - smallArcLength) / bigCircleRadius;
                    if (!isClockwise) {
                        x = thirdCircleX + (float) (bigCircleRadius * Math.cos(openRadian - startValue[i].getRadian()));
                        y = thirdCircleY - (float) (bigCircleRadius * Math.sin(openRadian - startValue[i].getRadian()));
                    } else {
                        x = thirdCircleX + (float) (bigCircleRadius * Math.cos(openRadian + startValue[i].getRadian()));
                        y = thirdCircleY + (float) (bigCircleRadius * Math.sin(openRadian + startValue[i].getRadian()));
                    }

                    pathPoints[i] = new PathPoint(x, y, t);
                }
            }
            return pathPoints;
        } else {
            return null;
        }

    }
}
