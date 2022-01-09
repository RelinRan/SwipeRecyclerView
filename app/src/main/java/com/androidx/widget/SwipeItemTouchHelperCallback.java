package com.androidx.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


public class SwipeItemTouchHelperCallback extends ItemTouchHelper.Callback {

    //列表控件
    private RecyclerView recyclerView;
    private int dragPosition = 0;
    //开始位置
    private int fromPosition = 0;
    //目标位置
    private int toPosition = 0;
    //长按拖拽是否可用
    private boolean longPressDragEnabled;
    //拖动标识
    private int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    //滑动标识
    private int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    //长按拖动移动监听
    private SwipeRecyclerView.OnItemTouchMoveListener onItemTouchMoveListener;
    //是否自动处理移动逻辑
    private boolean dragMoveAuto = true;
    //触摸选中监听
    private SwipeRecyclerView.OnItemTouchSelectedChangedListener onItemTouchSelectedChangedListener;
    //是否自动处理选中逻辑
    private boolean selectedAuto = true;
    //触摸滑动监听
    private SwipeRecyclerView.OnItemTouchSwipedListener onItemTouchSwipedListener;

    /**
     * 否自动处理移动逻辑
     *
     * @return
     */
    public boolean isDragMoveAuto() {
        return dragMoveAuto;
    }

    /**
     * 设置是否自动处理移动逻辑
     *
     * @param dragMoveAuto
     */
    public void setDragMoveAuto(boolean dragMoveAuto) {
        this.dragMoveAuto = dragMoveAuto;
    }

    /**
     * 否自动处理选择逻辑
     *
     * @return
     */
    public boolean isSelectedAuto() {
        return selectedAuto;
    }

    /**
     * 设置是否自动处理选择逻辑
     *
     * @param selectedAuto
     */
    public void setSelectedAuto(boolean selectedAuto) {
        this.selectedAuto = selectedAuto;
    }

    /**
     * 设置长按拖拽是否可用
     *
     * @param longPressDragEnabled
     */
    public void setLongPressDragEnabled(boolean longPressDragEnabled) {
        this.longPressDragEnabled = longPressDragEnabled;
    }

    /**
     * 设置拖动标识
     *
     * @param dragFlags
     */
    public void setDragFlags(int dragFlags) {
        this.dragFlags = dragFlags;
    }

    /**
     * 设置滑动标识
     *
     * @param swipeFlags
     */
    public void setSwipeFlags(int swipeFlags) {
        this.swipeFlags = swipeFlags;
    }

    /**
     * 长按拖动移动监听
     *
     * @param onItemTouchMoveListener
     */
    public void setOnItemTouchMoveListener(SwipeRecyclerView.OnItemTouchMoveListener onItemTouchMoveListener) {
        this.onItemTouchMoveListener = onItemTouchMoveListener;
    }

    /**
     * 设置触摸选中监听
     *
     * @param onItemTouchSelectedChangedListener
     */
    public void setOnItemTouchSelectedChangedListener(SwipeRecyclerView.OnItemTouchSelectedChangedListener onItemTouchSelectedChangedListener) {
        this.onItemTouchSelectedChangedListener = onItemTouchSelectedChangedListener;
    }

    /**
     * 设置滑动监听
     *
     * @param onItemTouchSwipedListener
     */
    public void setOnItemTouchSwipedListener(SwipeRecyclerView.OnItemTouchSwipedListener onItemTouchSwipedListener) {
        this.onItemTouchSwipedListener = onItemTouchSwipedListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return longPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        dragFlags = longPressDragEnabled ? dragFlags : 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        this.recyclerView = recyclerView;
        if (isDragMoveAuto()) {
            fromPosition = viewHolder.getAdapterPosition();
            toPosition = target.getAdapterPosition();
            if (recyclerView.getAdapter() instanceof RecyclerAdapter) {
                RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();
                adapter.swapItem(fromPosition, toPosition);
            }
        }
        if (onItemTouchMoveListener != null) {
            onItemTouchMoveListener.onItemOnItemTouchMove(recyclerView, viewHolder, target);
        }
        return longPressDragEnabled;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (isSelectedAuto()) {
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                dragPosition = viewHolder.getAdapterPosition();
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                if (recyclerView != null && recyclerView.getAdapter() != null) {
                    int from = 0, to = 0;
                    if (fromPosition < toPosition) {
                        from = dragPosition;
                        to = toPosition;
                    }
                    if (fromPosition > toPosition) {
                        from = toPosition;
                        to = dragPosition;
                    }
                    recyclerView.getAdapter().notifyItemRangeChanged(from, from == fromPosition ? 1 : to + 1);
                }
            }
        }
        if (onItemTouchSelectedChangedListener != null) {
            onItemTouchSelectedChangedListener.onItemTouchSelectedChanged(viewHolder, actionState);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchSwipedListener != null) {
            onItemTouchSwipedListener.onItemTouchSwiped(viewHolder, direction);
        }
    }

}
