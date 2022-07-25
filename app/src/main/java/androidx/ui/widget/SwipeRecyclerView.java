package androidx.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecyclerView extends RecyclerView {

    private String TAG = SwipeRecyclerView.class.getSimpleName();
    public static boolean DEBUG = false;
    //RecyclerView childView
    private View childView;
    //RecyclerView itemView容器
    private View itemLayout;
    //RecyclerView itemView
    //菜单容器
    private View menuLayout;
    //菜单View
    private View menuView;
    //菜单宽度
    private int menuWidth = 0;
    //滑动菜单是否打开
    private boolean swipeOpen;
    //滑动菜单是否可用
    private boolean swipeEnable = true;
    //是否可长按拖动
    private boolean longPressDragEnabled;
    //触摸滑动监听
    private OnItemTouchSwipedListener onItemTouchSwipedListener;
    //触摸助手
    private ItemTouchHelper touchHelper;
    //侧滑助手
    private SwipeItemTouchHelperCallback callback;
    //侧滑移动百分比
    private float sideRatio = 0.5F;
    //侧滑动画持续事件
    private int sideDuration = 300;

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
        setLayoutManager(new LinearLayoutManager(getContext()));
        callback = new SwipeItemTouchHelperCallback();
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(this);
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
     * 设置Item触摸助手回调
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
     * 设置长按拖拽移动监听
     *
     * @param onItemTouchMoveListener
     */
    public void setOnItemTouchMoveListener(OnItemTouchMoveListener onItemTouchMoveListener) {
        if (callback != null) {
            callback.setOnItemTouchMoveListener(onItemTouchMoveListener);
        }
    }

    public interface OnItemTouchMoveListener {

        /**
         * 长按拖拽移动监听
         *
         * @param recyclerView
         * @param viewHolder
         * @param target
         */
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

        /**
         * 触摸选择改变监听
         *
         * @param viewHolder
         * @param actionState
         */
        void onItemTouchSelectedChanged(@Nullable ViewHolder viewHolder, int actionState);

    }

    /**
     * 触摸横向滑动完成监听
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

        /**
         * 滑动监听
         *
         * @param viewHolder
         * @param direction
         */
        void onItemTouchSwiped(@NonNull ViewHolder viewHolder, int direction);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return touchSwipeEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return touchSwipeEvent(e);
    }

    /**
     * Item触摸监听
     */
    private OnTouchItemListener onTouchItemListener;

    /**
     * 设置Item触摸监听
     *
     * @param onTouchItemListener
     */
    public void setOnTouchItemListener(OnTouchItemListener onTouchItemListener) {
        this.onTouchItemListener = onTouchItemListener;
    }

    public interface OnTouchItemListener {

        /**
         * 设置Item触摸
         *
         * @param e            事件
         * @param recyclerView 列表view
         * @param adapter      适配器
         * @param childView    itemView
         */
        void onTouchItem(MotionEvent e, SwipeRecyclerView recyclerView, SwipeRecyclerAdapter adapter, View childView);

    }

    /**
     * 查找菜单item Layout
     *
     * @param e
     */
    private void findSwipeItemMenu(MotionEvent e) {
        if (isSwipeClose()) {
            childView = findChildViewUnder(e.getX(), e.getY());
            if (childView != null) {
                if (getAdapter() instanceof SwipeRecyclerAdapter) {
                    SwipeRecyclerAdapter adapter = (SwipeRecyclerAdapter) getAdapter();
                    if (adapter.isHasSwipe()) {
                        setSwipeOpen(adapter.isSwipeEnable());
                        itemLayout = adapter.findSwipeItemLayout(childView);
                        menuLayout = adapter.findSwipeMenuLayout(childView);
                        menuView = adapter.findSwipeMenuView(childView);
                        menuWidth = menuView == null ? 0 : menuView.getMeasuredWidth();
                        if (onTouchItemListener != null) {
                            onTouchItemListener.onTouchItem(e, this, adapter, childView);
                        }
                    }
                }
            }
        }
    }

    /**
     * 水平移动item
     *
     * @param e  操作事件
     * @param dx 水平间距
     * @param dy 垂直间距
     */
    private void moveSwipeItemMenu(MotionEvent e, float dx, float dy) {
        if (isSwipeEnable() && e.getPointerCount() < 2) {
            //水平滑动
            if (Math.abs(dx) > Math.abs(dy)) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (DEBUG) {
                    Log.i(TAG, "->" + "dx = " + dx + ",dy = " + dy);
                }
                smoothSwipeLayoutBy(e, false, dx);
            }
            //垂直滑动
            if (Math.abs(dx) < Math.abs(dy)) {
                closeSwipe();
            }
        }
    }

    private float dx, dy;
    private long dt = 0;
    private boolean isMove;
    private float distanceX;
    private float distanceY;
    private float adx;
    private float ady;

    /**
     * 触摸事件处理
     *
     * @param e
     */
    protected boolean touchSwipeEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                findSwipeItemMenu(e);
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
                isMove = adx > ady;
                if (isMove) {
                    moveSwipeItemMenu(e, distanceX, distanceY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float itemX = Math.abs(itemLayout.getTranslationX());
                if (DEBUG) {
                    Log.i(TAG, "->ACTION_UP itemX = " + itemX + ", menuWidth = " + menuWidth + ",distanceX = " + distanceX);
                }
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
     * 滑动菜单是否可用
     *
     * @return
     */
    public boolean isSwipeEnable() {
        return swipeEnable;
    }

    /**
     * 设置滑动菜单是否可用
     *
     * @param swipeEnable
     */
    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    /**
     * @return 滑动菜单是否打开
     */
    public boolean isSwipeOpen() {
        return swipeOpen;
    }

    /**
     * @return 滑动菜单是否关闭
     */
    public boolean isSwipeClose() {
        return !swipeOpen;
    }

    /**
     * 设置滑动菜单是否打开
     *
     * @param swipeOpen
     */
    public void setSwipeOpen(boolean swipeOpen) {
        this.swipeOpen = swipeOpen;
        if (onItemTouchSwipedListener != null && childView != null) {
            onItemTouchSwipedListener.onItemTouchSwiped(getChildViewHolder(childView), swipeOpen ? -1 : 1);
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
     * 打开侧滑菜单
     */
    public void openSwipe() {
        if (isSwipeClose()) {
            if (itemLayout != null) {
                float itemX = itemLayout.getTranslationX();
                float menuX = menuLayout == null ? 0 : menuLayout.getTranslationX();
                setSwipeItemLayoutAnimatorValue(itemX, -menuWidth);
                setSwipeMenuLayoutAnimatorValue(menuX, 0);
            }
            setSwipeOpen(true);
        }
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
     * @return 侧滑百分比（移动距离判断）
     */
    public float getSideRatio() {
        return sideRatio;
    }

    /**
     * 设置侧滑百分比（移动距离判断）
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
     * @param sideDuration
     */
    public void setSideDuration(int sideDuration) {
        this.sideDuration = sideDuration;
    }

}



