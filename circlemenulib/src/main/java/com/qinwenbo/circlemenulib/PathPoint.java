package com.qinwenbo.circlemenulib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Qinwenbo on 2017/2/28 15:26.
 */

public class PathPoint implements Parcelable {

    float x, y, fraction;
    double radian;

    public PathPoint(float x, float y, double radian) {
        this.radian = radian;
        this.x = x;
        this.y = y;
    }

    public PathPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PathPoint(float x, float y, float fraction) {
        this.x = x;
        this.y = y;
        this.fraction = fraction;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getRadian() {
        return radian;
    }

    public void setRadian(double radian) {
        this.radian = radian;
    }

    public static PathPoint moveTo(float x, float y) {
        return new PathPoint(x, y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(fraction);
        dest.writeDouble(radian);
    }

    public PathPoint(Parcel parcel) {
        x = parcel.readFloat();
        y = parcel.readFloat();
        fraction = parcel.readFloat();
        radian = parcel.readDouble();
    }

    public static final Creator<PathPoint> CREATOR = new Creator<PathPoint>() {

        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public PathPoint[] newArray(int size) {
            return new PathPoint[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public PathPoint createFromParcel(Parcel source) {
            return new PathPoint(source);
        }
    };
}
