package androidx.widget;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/**
 * 侧滑触摸事件
 *
 * @param <T>
 */
public class SwipeItemTouch<T> implements View.OnTouchListener {

    private SwipeRecyclerAdapter<T> adapter;
    private ViewHolder holder;
    private int position;
    private View itemView;
    //itemView容器
    private View itemLayout;
    //菜单容器
    private View menuLayout;
    //菜单View
    private View menuView;
    private float dx, dy;
    private int directionX;
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
    //item动画
    private SwipeItemAnimator itemAnimator;
    //菜单动画
    private SwipeItemAnimator menuAnimator;

    public SwipeItemTouch(SwipeRecyclerAdapter<T> adapter) {
        this.adapter = adapter;
    }

    /**
     * 初始化
     *
     * @param holder   View容器
     * @param position 位置
     */
    public void initialize(ViewHolder holder, int position) {
        this.holder = holder;
        this.position = position;
        itemView = holder.itemView;
        SwipeItem<T> item = adapter.getSwipeItem(position);
        itemAnimator = item.getItemAnimator();
        menuAnimator = item.getMenuAnimator();
        itemLayout = adapter.findSwipeItemLayout(itemView);
        menuLayout = adapter.findSwipeMenuLayout(itemView);
        menuView = adapter.findSwipeMenuView(itemView);
        menuWidth = menuView == null ? 0 : menuView.getMeasuredWidth();
        boolean isOpen = item.isOpen();
        if (isOpen) {
            openSwipe(false);
        } else {
            closeSwipe(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        touchItem(event);
        return true;
    }

    /**
     * 不允许打断触摸时间
     *
     * @param disallow
     */
    public void requestDisallowInterceptTouchEvent(boolean disallow) {
        ViewParent parent = holder.itemView.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallow);
        }
    }

    public void setItemLongClickable(boolean enable) {
        holder.itemView.setLongClickable(enable);
    }

    /**
     * 触摸事件处理
     *
     * @param e
     */
    protected void touchItem(MotionEvent e) {
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
                directionX = distanceX >= 0 ? 1 : -1;
                isMove = adx > ady && adx > swipeThreshold;
                itemView.setLongClickable(false);
                moveSwipeItemMenu(e, distanceX, distanceY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float itemTransX = itemLayout.getTranslationX();
                float boundary = Math.abs(itemTransX / menuWidth);
                boolean open = isSwipeOpen();
                //左滑动
                if (directionX == -1 && open == false) {
                    open = boundary >= 0.10F;
                }
                //右滑动
                if (directionX == 1 && open == true) {
                    open = 1 - boundary <= 0.10F;
                }
                if (open) {
                    openSwipe(true);
                } else {
                    closeSwipe(true);
                }
                if (System.currentTimeMillis() - dt < 30 && isMove) {
                    isMove = false;
                }
                break;
        }
    }

    /**
     * 水平移动item
     *
     * @param e   操作事件
     * @param dtx 水平间距
     * @param dty 垂直间距
     */
    private void moveSwipeItemMenu(MotionEvent e, float dtx, float dty) {
        if (adapter.isSwipeEnable() && e.getPointerCount() < 2) {
            //水平滑动
            if (Math.abs(dtx) > Math.abs(dty)) {
                requestDisallowInterceptTouchEvent(true);
                setItemLongClickable(false);
                translationSwipeBy(dtx);
                dx = e.getX();
                dy = e.getY();
            }
            //垂直滑动
            if (Math.abs(dtx) < Math.abs(dty)) {

            }
        }
    }

    /**
     * @return 滑动菜单是否打开
     */
    public boolean isSwipeOpen() {
        return adapter.getSwipeItem(position).isOpen();
    }

    /**
     * 设置是否滑开
     *
     * @param open
     */
    public void setSwipeOpen(boolean open) {
        adapter.getSwipeItem(position).setOpen(open);
    }

    /**
     * 滑动菜单位移
     *
     * @param mx 横向移动距离
     */
    public void translationSwipeBy(float mx) {
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
    }

    /**
     * 强制打开侧滑菜单
     */
    public void openSwipe(boolean animator) {
        if (itemLayout != null) {
            if (animator) {
                itemAnimator.start(holder.itemView, itemLayout, -menuWidth);
            } else {
                itemLayout.setTranslationX(-menuWidth);
            }
        }
        if (menuLayout != null) {
            if (animator) {
                menuAnimator.start(itemView, menuLayout, 0);
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
                itemAnimator.start(itemView, itemLayout, 0);
            } else {
                itemLayout.setTranslationX(0);
            }
        }
        if (menuLayout != null) {
            if (animator) {
                menuAnimator.start(itemView, menuLayout, menuWidth);
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
