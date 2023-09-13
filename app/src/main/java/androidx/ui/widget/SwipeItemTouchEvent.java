package androidx.ui.widget;

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
    private int swipeThreshold = 25;
    //侧滑移动百分比
    private float swipeRatio = 1.0F;
    //侧滑动画持续事件
    private int swipeDuration = 300;
    private SwipeItemAnimator itemAnimator;
    private SwipeItemAnimator menuAnimator;

    public SwipeItemTouchEvent(SwipeRecyclerAdapter<SwipeItem<T>> adapter, ViewHolder holder, int position) {
        this.adapter = adapter;
        this.holder = holder;
        this.position = position;
        itemAnimator = new SwipeItemAnimator();
        menuAnimator = new SwipeItemAnimator();
        itemLayout = adapter.findSwipeItemLayout(holder.itemView);
        menuLayout = adapter.findSwipeMenuLayout(holder.itemView);
        menuView = adapter.findSwipeMenuView(holder.itemView);
        menuWidth = menuView == null ? 0 : menuView.getMeasuredWidth();
        SwipeItem item = adapter.getItems().get(position);
        boolean isOpen = item.isOpen();
        if (isOpen) {
            openSwipe(false);
        } else {
            closeSwipe(false);
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
                float value = e.getX() - dx;
                float itemTransX = itemLayout.getTranslationX();
                float abs = Math.abs(itemTransX);
                if (value > 0) {
                    if (abs <= menuWidth * 0.99F) {
                        closeSwipe(true);
                    } else {
                        openSwipe(true);
                    }
                } else {
                    if (abs >= menuWidth * 0.01F) {
                        openSwipe(true);
                    } else {
                        closeSwipe(true);
                    }
                }
                if (System.currentTimeMillis() - dt < 30 && isMove) {
                    isMove = false;
                }
                dx = e.getX();
                dy = e.getY();
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
                smoothSwipeLayoutBy(e, dx);
            }
            //垂直滑动
            if (Math.abs(dx) < Math.abs(dy)) {
                if (isSwipeOpen()) {
                    closeSwipe(true);
                }
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
        adapter.getItems().get(position).setOpen(open);
    }

    /**
     * 滑动菜单位移
     *
     * @param mx 横向移动距离
     */
    public void smoothSwipeLayoutBy(MotionEvent e, float mx) {
        mx *= swipeRatio;
        if (itemLayout != null) {
            float itemLayoutX = itemLayout.getTranslationX() + mx;
            itemLayout.setTranslationX(itemLayoutX);
        }
        //菜单布局
        if (menuLayout != null) {
            float menuLayoutX = menuLayout.getTranslationX() + mx;
            menuLayout.setTranslationX(menuLayoutX);
        }
        dx = e.getX();
        dy = e.getY();
    }

    /**
     * 强制打开侧滑菜单
     */
    public void openSwipe(boolean animator) {
        if (itemLayout != null) {
            if (animator) {
                itemAnimator.start(itemLayout, -menuWidth);
            } else {
                itemLayout.setTranslationX(-menuWidth);
            }
        }
        if (menuLayout != null) {
            if (animator) {
                menuAnimator.start(menuLayout, 0);
            } else {
                menuLayout.setTranslationX(0);
            }
        }
        setSwipeOpen(true);
    }

    /**
     * 强制关闭侧滑
     */
    public void closeSwipe(boolean animator) {
        if (itemLayout != null) {
            if (animator) {
                itemAnimator.start(itemLayout, 0);
            } else {
                itemLayout.setTranslationX(0);
            }
        }
        if (menuLayout != null) {
            if (animator) {
                menuAnimator.start(menuLayout, menuWidth);
            } else {
                menuLayout.setTranslationX(menuWidth);
            }
        }
        setSwipeOpen(false);
    }


    /**
     * @return 侧滑百分比（移动距离判断）
     */
    public float getSwipeRatio() {
        return swipeRatio;
    }

    /**
     * 设置侧滑百分比（移动距离判断）
     *
     * @param swipeRatio
     */
    public void setSwipeRatio(float swipeRatio) {
        this.swipeRatio = swipeRatio;
    }

    /**
     * @return 侧滑持续动画时长（移动距离判断）
     */
    public int getSwipeDuration() {
        return swipeDuration;
    }

    /**
     * 设置侧滑持续动画时长（移动距离判断）
     *
     * @param swipeDuration
     */
    public void setSwipeDuration(int swipeDuration) {
        this.swipeDuration = swipeDuration;
        if (itemAnimator != null) {
            itemAnimator.setDuration(swipeDuration);
        }
        if (menuAnimator != null) {
            menuAnimator.setDuration(swipeDuration);
        }
    }

    /**
     * 滑动阈值
     *
     * @param swipeThreshold
     */
    public void setSwipeThreshold(int swipeThreshold) {
        this.swipeThreshold = swipeThreshold;
    }
}
