package com.xw.repo;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class BubbleUtils {

    private static final String KEY_MIUI_MANE = "ro.miui.ui.version.name";
    private static Properties sProperties = new Properties();
    private static Boolean miui;

    static boolean isMIUI() {
        if (miui != null) {
            return miui;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                sProperties.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            miui = sProperties.containsKey(KEY_MIUI_MANE);
        } else {
            Class<?> clazz;
            try {
                clazz = Class.forName("android.os.SystemProperties");
                Method getMethod = clazz.getDeclaredMethod("get", String.class);
                String name = (String) getMethod.invoke(null, KEY_MIUI_MANE);
                miui = !TextUtils.isEmpty(name);
            } catch (Exception e) {
                miui = false;
            }
        }

        return miui;
    }

    static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 判断两个浮点数是否相等
     * @param a
     * @param b
     * @param deviation  误差范围 例如 0.001f
     * @return
     */
    static boolean isFloatEqual(float a, float b, float deviation) {
        if (Float.isNaN(a) || Float.isNaN(b) || Float.isInfinite(a) || Float.isInfinite(b)) {
            return false;
        }
        return Math.abs(a - b) < deviation;
    }

    /**
     * 颜色和进度条之间的转换关系
     * 参考资料
     *     利用SeekBar制作颜色拾取器和SeekBar进度转为RGB
     *     https://stackoverflow.com/questions/4342757/how-to-make-a-color-gradient-in-a-seekbar
     * 理论上没有完整的反函数，因为progress的取值范围为 0 ~ 256 * 7 - 1
     * 而RGB的取值范围为 0 ~ 256 * 256 * 256 - 1
     * 所以，我们计算从progress => color的过程中，顺便需要记下progress，即要保存4元组 (r, g, b, progress)
     * @param progress
     * @return
     */
    public static int progressToColor(int progress) {
        int r = 0;
        int g = 0;
        int b = 0;

        if (progress < 256) {
            b = progress;
        } else if (progress < 256 * 2) {
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 3) {
            g = 255;
            b = progress % 256;
        } else if (progress < 256 * 4) {
            r = progress % 256;
            g = 256 - progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 5) {
            r = 255;
            g = 0;
            b = progress % 256;
        } else if (progress < 256 * 6) {
            r = 255;
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 7) {
            r = 255;
            g = 255;
            b = progress % 256;
        }

        SLog.info("progress[%d], r[%d], g[%d], b[%d]", progress, r, g, b);

        int color = Color.rgb(r, g, b);
        return color;
    }
}
