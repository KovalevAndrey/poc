package com.pointrfsystems.poc;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by an.kovalev on 04.05.2016.
 */
public class DiagramAnimator {

    private int animDuration = 0;
    private int defaultHeight = 0;
    private View view;

    public DiagramAnimator(View view, int animDuration) {
        this.view = view;
        this.animDuration = animDuration;
        this.defaultHeight = view.getMeasuredHeight();
    }

    private int transformRssi(int rrsi) {
        return (rrsi + 93) * 10;
    }

    public void animateView(int rrsi) {
        if (view == null) {
            return;
        }
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), transformRssi(rrsi));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(animDuration);
        anim.start();
    }


}
