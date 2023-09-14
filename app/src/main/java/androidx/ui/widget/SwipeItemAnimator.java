package androidx.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * 动画
 */
public class SwipeItemAnimator<T> implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    private ValueAnimator animator;
    private int duration = 300;
    private View animatorView;
    private View itemView;
    private SwipeRecyclerAdapter<SwipeItem<T>> adapter;

    public SwipeItemAnimator(SwipeRecyclerAdapter<SwipeItem<T>> adapter) {
        this.adapter = adapter;
    }

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
    public void start(View itemView,View view, float end) {
        this.itemView = itemView;
        this.animatorView = view;
        animator = ValueAnimator.ofFloat(view.getTranslationX(), end);
        animator.addUpdateListener(this);
        animator.addListener(this);
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

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float value = (Float) valueAnimator.getAnimatedValue();
        animatorView.setTranslationX(value);
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        itemView.setLongClickable(true);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

}
