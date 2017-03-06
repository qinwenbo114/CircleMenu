package com.qinwenbo.circlemenulib;

import android.graphics.drawable.Drawable;

/**
 * Created by Qinwenbo on 2017/3/1 15:24.
 */

public class MenuIcon {
    Drawable drawable;
    boolean isVisible;

    public MenuIcon(Drawable drawable) {
        this.drawable = drawable;
        this.isVisible = true;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
