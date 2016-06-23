package com.pointrfsystems.mtu;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by an.kovalev on 04.05.2016.
 */
public class DiagramAnimator {

    private int animDuration = 0;
    private int defaultHeight = 0;
    private View current_rssi;
    private View max_rssi;
    private TextView curr_value;
    private TextView max_value;
    private int max_rssi_value = -93;

    public DiagramAnimator(View current_rssi, View max_rssi, int animDuration, TextView curr_value, TextView max_value) {
        this.current_rssi = current_rssi;
        this.max_rssi = max_rssi;
        this.animDuration = animDuration;
        this.curr_value = curr_value;
        this.max_value = max_value;
        this.defaultHeight = (int) (200 * Resources.getSystem().getDisplayMetrics().density);
    }

    private int transformRssi(int rrsi) {
        float res = (rrsi + 93) / 63f * defaultHeight;
        return (int) res;
    }

    public void clearMaxValue() {
        max_rssi_value = -93;
        max_value.setText("");
        ViewGroup.LayoutParams layoutParams = max_rssi.getLayoutParams();
        layoutParams.height = 0;
        max_rssi.setLayoutParams(layoutParams);
    }

    private int getColorbyRssi(int rssi) {

        float n = rssi;

        n += 93;
        n /= 63f;
        n *= 100;

        int r = (int) (255 - (255.0 * n / 100));
        int g = (int) (255 - (255.0 * (100 - n)) / 100);
        int b = 0;

        return Color.rgb(r, g, b);
    }


    private GradientDrawable getGradientDrawale(int rssi) {

        int[] colors = {
                current_rssi.getContext().getResources().getColor(R.color.red),
                getColorbyRssi(rssi)};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                colors);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);


        return gradientDrawable;
    }


    public void animateView(final int rssi) {

        if (rssi > max_rssi_value) {
            max_rssi_value = rssi;
            max_value.setText("MAX " + max_rssi_value);
            ValueAnimator anim = ValueAnimator.ofInt(max_rssi.getMeasuredHeight(), transformRssi(max_rssi_value));
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = max_rssi.getLayoutParams();
                    layoutParams.height = val;
                    max_rssi.setLayoutParams(layoutParams);
                    max_rssi.setBackground(getGradientDrawale(max_rssi_value));
                }
            });
            anim.setDuration(animDuration);
            anim.start();
        }

        curr_value.setText("CURRENT " + rssi);
        ValueAnimator anim = ValueAnimator.ofInt(current_rssi.getMeasuredHeight(), transformRssi(rssi));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = current_rssi.getLayoutParams();
                layoutParams.height = val;
                current_rssi.setLayoutParams(layoutParams);
                current_rssi.setBackground(getGradientDrawale(rssi));
            }
        });
        anim.setDuration(animDuration);
        anim.start();
    }


}
