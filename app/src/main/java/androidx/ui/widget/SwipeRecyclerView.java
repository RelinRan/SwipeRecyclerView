package androidx.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecyclerView extends RecyclerView {


    //是否可长按拖动
    private boolean longPressDragEnabled;
    //触摸滑动监听
    private OnItemTouchSwipedListener onItemTouchSwipedListener;
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
        setLayoutManager(new LinearLayoutManager(getContext()));
        callback = new SwipeItemTouchHelperCallback();
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(this);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof SwipeRecyclerAdapter && adapter != null) {
            SwipeRecyclerAdapter swipeRecyclerAdapter = (SwipeRecyclerAdapter) adapter;
            swipeRecyclerAdapter.attachRecyclerView(this);
        }
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

}



