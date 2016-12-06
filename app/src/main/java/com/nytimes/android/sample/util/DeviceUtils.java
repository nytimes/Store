package com.nytimes.android.sample.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DeviceUtils {

    private WindowManager windowManager;

    public DeviceUtils(Application context) {
        windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public int getScreenWidth() {
        return getScreenSize().x;
    }

    public int getScreenHeight() {
        return getScreenSize().y;
    }

    public Point getScreenSize() {
        Display display = windowManager.getDefaultDisplay();
        Point result = new Point();
        display.getSize(result);
        return result;
    }
}
