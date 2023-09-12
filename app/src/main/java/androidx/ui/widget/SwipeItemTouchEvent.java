package androidx.ui.widget;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

public class SwipeItemTouchEvent<T> implements View.OnTouchListener {

    private SwipeRecyclerAdapter<SwipeItem<T>> adapter;
    private ViewHolder holder;
    private int position;
    //itemView容器
    private View itemLayout;
    //菜单容器
    private View menuLayout;
    //菜单View
    private View menuView;
    private float dx, dy;
    private long dt = 0;
    private boolean isMove;
    private float distanceX;
    private float distanceY;
    private float adx;
    private float ady;
    //菜单宽度
    private int menuWidth;

    //横向滑动阈值
    private int swipeThreshold = 30;
    //侧滑移动百分比
    private float sideRatio = 0.5F;
    //侧滑动画持续事件
    private int sideDuration = 300;


    public SwipeItemTouchEvent(SwipeRecyclerAdapter<SwipeItem<T>> adapter, ViewHolder holder, int position) {
        this.adapter = adapter;
        this.holder = holder;
        this.position = position;
        itemLayout = adapter.findSwipeItemLayout(holder.itemView);
        menuLayout = adapter.findSwipeMenuLayout(holder.itemView);
        menuView = adapter.findSwipeMenuView(holder.itemView);
        menuWidth = menuView == null ? 0 : menuView.getMeasuredWidth();
        SwipeItem item = adapter.getItems().get(position);
        boolean isOpen = item.isOpen();
        if (menuLayout != null && itemLayout != null) {
            if (isOpen) {
                itemLayout.setTranslationX(-menuWidth);
                menuLayout.setTranslationX(0);
            } else {
                itemLayout.setTranslationX(0);
                menuLayout.setTranslationX(0);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        touchSwipeEvent(event);
        return true;
    }

    /**
     * 触摸事件处理
     *
     * @param e
     */
    protected boolean touchSwipeEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = e.getX();
                dy = e.getY();
                isMove = false;
                dt = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                distanceX = e.getX() - dx;
                distanceY = e.getY() - dy;
                adx = Math.abs(distanceX);
                ady = Math.abs(distanceY);
                isMove = adx > ady && adx > swipeThreshold;
                if (isMove) {
                    moveSwipeItemMenu(e, distanceX, distanceY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float itemX = Math.abs(itemLayout.getTranslationX());
                if (itemX < menuWidth / 2) {
                    closeSwipeEnforce();
                }
                if (itemX >= menuWidth / 2) {
                    openSwipeEnforce();
                }
                if (System.currentTimeMillis() - dt < 30 && isMove) {
                    isMove = false;
                }
                break;
        }
        return isMove;
    }

    /**
     * 水平移动item
     *
     * @param e  操作事件
     * @param dx 水平间距
     * @param dy 垂直间距
     */
    private void moveSwipeItemMenu(MotionEvent e, float dx, float dy) {
        if (adapter.isSwipeEnable() && e.getPointerCount() < 2) {
            //水平滑动
            if (Math.abs(dx) > Math.abs(dy)) {
                ViewParent parent = holder.itemView.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                smoothSwipeLayoutBy(e, false, dx);
            }
            //垂直滑动
            if (Math.abs(dx) < Math.abs(dy)) {
                closeSwipe();
            }
        }
    }

    /**
     * @return 滑动菜单是否打开
     */
    public boolean isSwipeOpen() {
        return adapter.getItems().get(position).isOpen();
    }

    /**
     * 设置是否滑开
     *
     * @param open
     */
    public void setSwipeOpen(boolean open) {
        adapter.setItemSwipeOpen(position,open);
    }

    /**
     * 关闭侧滑菜单
     */
    public void closeSwipe() {
        if (isSwipeOpen()) {
            if (itemLayout != null) {
                float itemX = itemLayout.getTranslationX();
                float menuX = menuLayout == null ? 0 : menuLayout.getTranslationX();
                setSwipeItemLayoutAnimatorValue(itemX, 0);
                setSwipeMenuLayoutAnimatorValue(menuX, menuWidth);
            }
        }
    }

    /**
     * 滑动菜单位移
     *
     * @param animator 是否动画
     * @param mx       横向移动距离
     */
    public void smoothSwipeLayoutBy(MotionEvent e, boolean animator, float mx) {
        mx *= sideRatio;
        if (itemLayout != null) {
            float itemLayoutX = itemLayout.getTranslationX() + mx;
            if (itemLayoutX > -menuWidth && itemLayoutX < 0) {
                setSwipeItemLayoutTranslationX(animator, itemLayoutX);
                setSwipeOpen(true);
            }
            if (itemLayoutX <= -menuWidth) {
                setSwipeItemLayoutTranslationX(animator, -menuWidth);
                setSwipeOpen(true);
                dx = e.getX();
                dy = e.getY();
            }
            if (itemLayoutX >= 0) {
                setSwipeItemLayoutTranslationX(animator, 0);
                setSwipeOpen(false);
                dx = e.getX();
                dy = e.getY();
            }
        }
        //菜单布局
        if (menuLayout != null) {
            float menuLayoutX = menuLayout.getTranslationX() + mx;
            if (menuLayoutX > 0 && menuLayoutX < menuWidth) {
                setSwipeMenuLayoutTranslationX(animator, menuLayoutX);
                setSwipeOpen(true);
            }
            if (menuLayoutX <= 0) {
                setSwipeMenuLayoutTranslationX(animator, 0);
                setSwipeOpen(true);
                dx = e.getX();
                dy = e.getY();
            }
            if (menuLayoutX >= menuWidth) {
                setSwipeMenuLayoutTranslationX(animator, menuWidth);
                setSwipeOpen(false);
                dx = e.getX();
                dy = e.getY();
            }
        }
    }

    /**
     * 强制关闭侧滑
     */
    public void closeSwipeEnforce() {
        if (itemLayout != null) {
            float itemX = itemLayout.getTranslationX();
            float menuX = menuLayout == null ? 0 : menuLayout.getTranslationX();
            setSwipeItemLayoutAnimatorValue(itemX, 0);
            setSwipeMenuLayoutAnimatorValue(menuX, menuWidth);
        }
    }

    /**
     * 强制打开侧滑菜单
     */
    public void openSwipeEnforce() {
        if (itemLayout != null) {
            float itemX = itemLayout.getTranslationX();
            float menuX = menuLayout == null ? 0 : menuLayout.getTranslationX();
            setSwipeItemLayoutAnimatorValue(itemX, -menuWidth);
            setSwipeMenuLayoutAnimatorValue(menuX, 0);
        }
        setSwipeOpen(true);
    }

    /**
     * 设置item布局动画值
     *
     * @param start 开始
     * @param end   结束
     */
    private void setSwipeItemLayoutAnimatorValue(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(animation -> {
            float value = (Float) animator.getAnimatedValue();
            setSwipeItemLayoutTranslationX(false, value);
            if (value == 0) {
                setSwipeOpen(false);
            }
        });
        animator.setDuration(sideDuration);
        animator.start();
    }

    /**
     * 设置菜单布局动画值
     *
     * @param start 开始
     * @param end   结束
     */
    private void setSwipeMenuLayoutAnimatorValue(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(animation -> {
            float value = (Float) animator.getAnimatedValue();
            setSwipeMenuLayoutTranslationX(false, value);
            if (value == menuWidth) {
                setSwipeOpen(false);
            }
        });
        animator.setDuration(sideDuration);
        animator.start();
    }

    /**
     * 设置滑动RecyclerView Item布局x位移
     *
     * @param animator     是否动画
     * @param translationX x轴移动距离
     */
    public void setSwipeItemLayoutTranslationX(boolean animator, float translationX) {
        if (itemLayout == null) {
            return;
        }
        if (animator) {
            setSwipeItemLayoutAnimatorValue(itemLayout.getTranslationX(), translationX);
        } else {
            itemLayout.setTranslationX(translationX);
        }
    }

    /**
     * 设置滑动RecyclerView Menu布局x位移
     *
     * @param animator     是否动画
     * @param translationX x轴移动距离
     */
    public void setSwipeMenuLayoutTranslationX(boolean animator, float translationX) {
        if (menuLayout == null) {
            return;
        }
        if (animator) {
            setSwipeMenuLayoutAnimatorValue(menuLayout.getTranslationX(), translationX);
        } else {
            menuLayout.setTranslationX(translationX);
        }
    }

    /**
     * @return 侧滑百分比（移动距离判断）
     */
    public float getSideRatio() {
        return sideRatio;
    }

    /**
     * 设置侧滑百分比（移动距离判断）
     *
     * @param sideRatio
     */
    public void setSideRatio(float sideRatio) {
        this.sideRatio = sideRatio;
    }

    /**
     * @return 侧滑持续动画时长（移动距离判断）
     */
    public int getSideDuration() {
        return sideDuration;
    }

    /**
     * 设置侧滑持续动画时长（移动距离判断）
     *
     * @param sideDuration
     */
    public void setSideDuration(int sideDuration) {
        this.sideDuration = sideDuration;
    }

    /**
     * 滑动阈值
     * @param swipeThreshold
     */
    public void setSwipeThreshold(int swipeThreshold) {
        this.swipeThreshold = swipeThreshold;
    }
}
