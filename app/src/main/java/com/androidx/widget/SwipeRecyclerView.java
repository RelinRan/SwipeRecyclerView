package com.androidx.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecyclerView extends RecyclerView {

    private final static String TAG = "SwipeRecyclerView";

    //滑动追踪器
    private OverScroller scroller;
    //速度追踪器
    private VelocityTracker velocityTracker;
    //按钮坐标
    private float downX, downY;
    //RecyclerView childView
    private View childView;
    //RecyclerView itemView容器
    private View itemLayout;
    //RecyclerView itemView
    private View itemView;
    //菜单容器
    private View menuLayout;
    //菜单View
    private View menuView;
    //菜单宽度
    private int menuWidth = 0;
    //滑动菜单是否打开
    private boolean swipeMenuOpen;
    //滑动菜单是否可用
    private boolean swipeMenuEnable = true;
    //是否可长按拖动
    private boolean longPressDragEnabled;
    //触摸滑动监听
    private OnItemTouchSwipedListener onItemTouchSwipedListener;

    private ItemTouchHelper touchHelper;
    private SwipeItemTouchHelperCallback callback;

    public SwipeRecyclerView(@NonNull Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public SwipeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public SwipeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        callback = new SwipeItemTouchHelperCallback();
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(this);
        scroller = new OverScroller(context);
        velocityTracker = VelocityTracker.obtain();
    }

    /**
     * 获取触摸事件助手
     *
     * @return
     */
    public ItemTouchHelper getItemTouchHelper() {
        return touchHelper;
    }

    /**
     * 获取触摸事件助手回调
     *
     * @return
     */
    public SwipeItemTouchHelperCallback getSwipeItemTouchHelperCallback() {
        return callback;
    }

    /**
     * 设置Item触摸监听
     *
     * @param callback
     */
    public void setSwipeItemTouchHelperCallback(SwipeItemTouchHelperCallback callback) {
        this.callback = callback;
    }

    /**
     * 否自动处理移动逻辑
     *
     * @return
     */
    public boolean isDragMoveAuto() {
        return callback.isDragMoveAuto();
    }

    /**
     * 设置是否自动处理移动逻辑
     *
     * @param dragMoveAuto
     */
    public void setDragMoveAuto(boolean dragMoveAuto) {
        callback.setDragMoveAuto(dragMoveAuto);
    }

    /**
     * 否自动处理选择逻辑
     *
     * @return
     */
    public boolean isSelectedAuto() {
        return callback.isSelectedAuto();
    }

    /**
     * 设置是否自动处理选择逻辑
     *
     * @param selectedAuto
     */
    public void setSelectedAuto(boolean selectedAuto) {
        callback.setSelectedAuto(selectedAuto);
    }

    /**
     * 是否可长按拖拽
     *
     * @return
     */
    public boolean isLongPressDragEnabled() {
        return longPressDragEnabled;
    }

    /**
     * 设置是否可长按拖拽
     *
     * @param longPressDragEnabled
     */
    public void setLongPressDragEnabled(boolean longPressDragEnabled) {
        this.longPressDragEnabled = longPressDragEnabled;
        if (callback != null) {
            callback.setLongPressDragEnabled(longPressDragEnabled);
        }
    }

    /**
     * 设置拖动标识
     *
     * @param dragFlags
     */
    public void setDragFlags(int dragFlags) {
        if (callback != null) {
            callback.setDragFlags(dragFlags);
        }
    }

    /**
     * 设置滑动标识
     *
     * @param swipeFlags
     */
    public void setSwipeFlags(int swipeFlags) {
        if (callback != null) {
            callback.setSwipeFlags(swipeFlags);
        }
    }

    /**
     * 设置长按拖拽监听
     *
     * @param onItemTouchMoveListener
     */
    public void setOnItemTouchMoveListener(OnItemTouchMoveListener onItemTouchMoveListener) {
        if (callback != null) {
            callback.setOnItemTouchMoveListener(onItemTouchMoveListener);
        }
    }

    public interface OnItemTouchMoveListener {

        void onItemOnItemTouchMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target);

    }

    /**
     * 设置触摸选中监听
     *
     * @param onItemTouchSelectedChangedListener
     */
    public void setOnItemTouchSelectedChangedListener(OnItemTouchSelectedChangedListener onItemTouchSelectedChangedListener) {
        if (callback != null) {
            callback.setOnItemTouchSelectedChangedListener(onItemTouchSelectedChangedListener);
        }
    }

    public interface OnItemTouchSelectedChangedListener {

        void onItemTouchSelectedChanged(@Nullable ViewHolder viewHolder, int actionState);

    }

    /**
     * 触摸滑动监听
     *
     * @param onItemTouchSwipedListener
     */
    public void setOnItemTouchSwipedListener(OnItemTouchSwipedListener onItemTouchSwipedListener) {
        this.onItemTouchSwipedListener = onItemTouchSwipedListener;
        if (callback != null) {
            callback.setOnItemTouchSwipedListener(onItemTouchSwipedListener);
        }
    }

    public interface OnItemTouchSwipedListener {

        void onItemTouchSwiped(@NonNull ViewHolder viewHolder, int direction);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isSwipeMenuClose()) {
            childView = findChildViewUnder(e.getX(), e.getY());
            if (childView != null && getAdapter() instanceof RecyclerAdapter) {
                RecyclerAdapter adapter = (RecyclerAdapter) getAdapter();
                if (adapter.isHasSwipeMenu()) {
                    itemLayout = adapter.findSwipeItemLayout(childView);
                    itemView = adapter.findSwipeItemView(childView);
                    menuLayout = adapter.findSwipeMenuLayout(childView);
                    menuView = adapter.findSwipeMenuView(childView);
                    menuWidth = menuView.getMeasuredWidth();
                }
            }
        }
        velocityTracker.addMovement(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                scroller = new OverScroller(getContext());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                float mx = e.getX() - downX;
                float my = e.getY() - downY;
                if (isSwipeMenuEnable() && e.getPointerCount() < 2) {
                    velocityTracker.computeCurrentVelocity((int) (menuWidth * 0.1F), menuWidth);
                    //水平滑动
                    if (Math.abs(mx) > Math.abs(my) && Math.abs(mx) > 10) {
                        if (velocityTracker.getXVelocity() < 10) {
                            smoothSwipeLayoutBy(mx);
                        } else {
                            scroller.startScroll(scroller.getFinalX(), 0, (int) mx, 0, 250);
                            invalidate();
                        }
                    }
                    //垂直滑动
                    if (Math.abs(mx) < Math.abs(my) && Math.abs(my) > 10) {
                        closeSwipeMenu();
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    /**
     * 滑动菜单是否可用
     *
     * @return
     */
    public boolean isSwipeMenuEnable() {
        return swipeMenuEnable;
    }

    /**
     * 设置滑动菜单是否可用
     *
     * @param swipeMenuEnable
     */
    public void setSwipeMenuEnable(boolean swipeMenuEnable) {
        this.swipeMenuEnable = swipeMenuEnable;
    }

    /**
     * 滑动菜单是否打开
     *
     * @return
     */
    public boolean isSwipeMenuOpen() {
        return swipeMenuOpen;
    }

    /**
     * 滑动菜单是否关闭
     *
     * @return
     */
    public boolean isSwipeMenuClose() {
        return !swipeMenuOpen;
    }

    /**
     * 设置滑动菜单
     *
     * @param swipeMenuOpen
     */
    public void setSwipeMenuOpen(boolean swipeMenuOpen) {
        this.swipeMenuOpen = swipeMenuOpen;
        if (onItemTouchSwipedListener != null && childView != null) {
            onItemTouchSwipedListener.onItemTouchSwiped(getChildViewHolder(childView), swipeMenuOpen ? -1 : 1);
        }
    }

    /**
     * 滑动菜单位移
     *
     * @param mx 横向移动距离
     */
    public void smoothSwipeLayoutBy(float mx) {
        float itemLayoutX = itemLayout.getTranslationX() + mx;
        if (itemLayoutX > -menuWidth && itemLayoutX < 0) {
            setSwipeItemLayoutTranslationX(itemLayoutX);
            setSwipeMenuOpen(true);
        }
        if (itemLayoutX <= -menuWidth) {
            setSwipeItemLayoutTranslationX(-menuWidth);
            setSwipeMenuOpen(true);
        }
        if (itemLayoutX >= 0) {
            setSwipeItemLayoutTranslationX(0);
            setSwipeMenuOpen(false);
        }
        //菜单布局
        float menuLayoutX = menuLayout.getTranslationX() + mx;
        if (menuLayoutX > 0 && menuLayoutX < menuWidth) {
            setSwipeMenuLayoutTranslationX(menuLayoutX);
            setSwipeMenuOpen(true);
        }
        if (menuLayoutX <= 0) {
            setSwipeMenuLayoutTranslationX(0);
            setSwipeMenuOpen(true);
        }
        if (menuLayoutX >= menuWidth) {
            setSwipeMenuLayoutTranslationX(menuWidth);
            setSwipeMenuOpen(false);
        }
    }

    /**
     * 设置滑动RecyclerView Item布局x位移
     *
     * @param translationX
     */
    public void setSwipeItemLayoutTranslationX(float translationX) {
        itemLayout.setTranslationX(translationX);
    }

    /**
     * 设置滑动RecyclerView Menu布局x位移
     *
     * @param translationX
     */
    public void setSwipeMenuLayoutTranslationX(float translationX) {
        menuLayout.setTranslationX(translationX);
    }

    /**
     * 关闭滑动菜单
     */
    public void closeSwipeMenu() {
        if (isSwipeMenuOpen()) {
            if (itemView != null) {
                setSwipeItemLayoutTranslationX(0);
                setSwipeMenuLayoutTranslationX(menuWidth);
            }
            setSwipeMenuOpen(false);
        }
    }


    /**
     * 打开滑动菜单
     */
    public void openSwipeMenu() {
        if (isSwipeMenuClose()) {
            if (itemView != null) {
                setSwipeItemLayoutTranslationX(-menuWidth);
                setSwipeMenuLayoutTranslationX(0);
            }
            setSwipeMenuOpen(true);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (itemView == null) {
                return;
            }
            int currX = scroller.getCurrX();
            //左滑 - Open
            if (currX < 0 && Math.abs(currX) >= menuWidth) {
                openSwipeMenu();
            }
            //右滑 - Close
            if (currX > 0) {
                closeSwipeMenu();
            }
        }
    }

}



