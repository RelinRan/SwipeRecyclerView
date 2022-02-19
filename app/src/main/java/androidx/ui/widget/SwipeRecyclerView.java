package androidx.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
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
    //移动极限
    private int scaledTouchSlop;
    //item位置
    private int itemCount = 0;
    //触摸助手
    private ItemTouchHelper touchHelper;
    //侧滑助手
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
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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
        touchSwipeEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        touchSwipeEvent(e);
        return super.onTouchEvent(e);
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
     * 触摸事件处理
     *
     * @param e
     */
    protected void touchSwipeEvent(MotionEvent e) {
        velocityTracker.addMovement(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
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
                downX = e.getX();
                downY = e.getY();
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                scroller = new OverScroller(getContext());
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = e.getX() - downX;
                float distanceY = e.getY() - downY;
                if (isSwipeEnable() && e.getPointerCount() < 2) {
                    velocityTracker.computeCurrentVelocity((int) (menuWidth * 0.1F), menuWidth);
                    //水平滑动
                    if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > scaledTouchSlop) {
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        if (velocityTracker.getXVelocity() < 10) {
                            smoothSwipeLayoutBy(distanceX);
                        } else {
                            scroller.startScroll(scroller.getFinalX(), 0, (int) distanceX, 0, 250);
                            invalidate();
                        }
                    }
                    //垂直滑动
                    if (Math.abs(distanceX) < Math.abs(distanceY) && Math.abs(distanceY) > scaledTouchSlop) {
                        closeSwipe();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
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
     * 滑动菜单是否打开
     *
     * @return
     */
    public boolean isSwipeOpen() {
        return swipeOpen;
    }

    /**
     * 滑动菜单是否关闭
     *
     * @return
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
     * @param mx 横向移动距离
     */
    public void smoothSwipeLayoutBy(float mx) {
        if (itemLayout != null) {
            float itemLayoutX = itemLayout.getTranslationX() + mx;
            if (itemLayoutX > -menuWidth && itemLayoutX < 0) {
                setSwipeItemLayoutTranslationX(itemLayoutX);
                setSwipeOpen(true);
            }
            if (itemLayoutX <= -menuWidth) {
                setSwipeItemLayoutTranslationX(-menuWidth);
                setSwipeOpen(true);
            }
            if (itemLayoutX >= 0) {
                setSwipeItemLayoutTranslationX(0);
                setSwipeOpen(false);
            }
        }
        //菜单布局
        if (menuLayout != null) {
            float menuLayoutX = menuLayout.getTranslationX() + mx;
            if (menuLayoutX > 0 && menuLayoutX < menuWidth) {
                setSwipeMenuLayoutTranslationX(menuLayoutX);
                setSwipeOpen(true);
            }
            if (menuLayoutX <= 0) {
                setSwipeMenuLayoutTranslationX(0);
                setSwipeOpen(true);
            }
            if (menuLayoutX >= menuWidth) {
                setSwipeMenuLayoutTranslationX(menuWidth);
                setSwipeOpen(false);
            }
        }
    }

    /**
     * 设置滑动RecyclerView Item布局x位移
     *
     * @param translationX
     */
    public void setSwipeItemLayoutTranslationX(float translationX) {
        if (itemLayout == null) {
            return;
        }
        itemLayout.setTranslationX(translationX);
    }

    /**
     * 设置滑动RecyclerView Menu布局x位移
     *
     * @param translationX
     */
    public void setSwipeMenuLayoutTranslationX(float translationX) {
        if (menuLayout == null) {
            return;
        }
        menuLayout.setTranslationX(translationX);
    }

    /**
     * 关闭侧滑菜单
     */
    public void closeSwipe() {
        if (isSwipeOpen()) {
            if (itemLayout != null) {
                setSwipeItemLayoutTranslationX(0);
                setSwipeMenuLayoutTranslationX(menuWidth);
            }
            setSwipeOpen(false);
        }
    }

    /**
     * 打开侧滑菜单
     */
    public void openSwipeMenu() {
        if (isSwipeClose()) {
            if (itemLayout != null) {
                setSwipeItemLayoutTranslationX(-menuWidth);
                setSwipeMenuLayoutTranslationX(0);
            }
            setSwipeOpen(true);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (itemLayout == null) {
                return;
            }
            int currX = scroller.getCurrX();
            //左滑 - Open
            if (currX < 0 && Math.abs(currX) >= menuWidth) {
                openSwipeMenu();
            }
            //右滑 - Close
            if (currX > 0) {
                closeSwipe();
            }
        }
    }

}



