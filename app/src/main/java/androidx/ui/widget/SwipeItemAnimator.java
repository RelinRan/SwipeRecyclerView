package androidx.ui.widget;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * 动画
 */
public class SwipeItemAnimator {

    private ValueAnimator animator;
    private int duration = 300;

    /**
     * 设置时常
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 设置item布局动画值
     *
     * @param end   结束
     */
    public void start(View view, float end) {
        cancel();
        animator = ValueAnimator.ofFloat(view.getTranslationX(), end);
        animator.addUpdateListener(animation -> {
            float value = (Float) animator.getAnimatedValue();
            view.setTranslationX(value);
        });
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * 取消
     */
    public void cancel() {
        if (animator != null) {
            animator.cancel();
        }
    }

}
