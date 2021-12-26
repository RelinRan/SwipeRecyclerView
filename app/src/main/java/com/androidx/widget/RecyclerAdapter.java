package com.androidx.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Relin
 * Describe:Recycler使用的基础Adapter
 * Date:2020/12/26 19:17
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter implements ViewHolder.OnItemClickLister, ViewHolder.OnItemFocusChangeListener {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 数据对象
     */
    private List<T> data;
    /**
     * 空视图
     */
    private View emptyView;
    /**
     * View容器
     */
    private ViewHolder viewHolder;
    /**
     * 是否有侧滑菜单
     */
    private boolean hasSwipeMenu;
    /**
     * 侧滑LayoutId
     */
    private final int ID_SWIPE_ITEM_LAYOUT = 0x100;
    /**
     * 侧滑ItemViewId
     */
    private final int ID_SWIPE_ITEM_VIEW = 0x101;
    /**
     * 侧滑菜单LayoutId
     */
    private final int ID_SWIPE_MENU_LAYOUT = 0x200;
    /**
     * 侧滑菜单ViewId
     */
    private final int ID_SWIPE_MENU_VIEW = 0x201;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 获取Item布局资源
     *
     * @return
     */
    protected abstract int getItemLayoutResId(int viewType);

    /**
     * 获取View容器
     *
     * @return
     */
    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * 是否有菜单
     *
     * @return
     */
    public boolean isHasSwipeMenu() {
        return hasSwipeMenu;
    }

    /**
     * 滑动菜单LayoutResId
     *
     * @param viewType
     * @return
     */
    protected int getItemSwipeMenuLayoutResId(int viewType) {
        return 0;
    }

    /**
     * 找到侧滑ItemLayout
     *
     * @param childView
     * @return
     */
    public View findSwipeItemLayout(View childView) {
        return childView.findViewById(ID_SWIPE_ITEM_LAYOUT);
    }

    /**
     * 找到侧滑ItemView
     *
     * @param childView
     * @return
     */
    public View findSwipeItemView(View childView) {
        return childView.findViewById(ID_SWIPE_ITEM_VIEW);
    }

    /**
     * 找到侧滑菜单
     *
     * @param childView
     * @return
     */
    public View findSwipeMenuLayout(View childView) {
        return childView.findViewById(ID_SWIPE_MENU_LAYOUT);
    }

    /**
     * 找到侧滑菜单View
     *
     * @param childView
     * @return
     */
    public View findSwipeMenuView(View childView) {
        return childView.findViewById(ID_SWIPE_MENU_VIEW);
    }

    /**
     * 获取item菜单View
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected View getItemSwipeMenuView(ViewGroup parent, int viewType) {
        int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
        int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
        FrameLayout swipeLayout = new FrameLayout(getContext());
        swipeLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //Menu
        FrameLayout menuLayout = new FrameLayout(getContext());
        menuLayout.setId(ID_SWIPE_MENU_LAYOUT);
        menuLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        View menuView = LayoutInflater.from(getContext()).inflate(getItemSwipeMenuLayoutResId(viewType), null);
        menuView.setId(ID_SWIPE_MENU_VIEW);
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        menuParams.gravity = Gravity.RIGHT;
        menuLayout.addView(menuView, menuParams);
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        menuView.measure(measureSpec, measureSpec);
        menuLayout.setTranslationX(menuView.getMeasuredWidth());
        swipeLayout.addView(menuLayout);
        //RecyclerView itemView
        FrameLayout leftLayout = new FrameLayout(getContext());
        leftLayout.setId(ID_SWIPE_ITEM_LAYOUT);
        leftLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        View itemView = LayoutInflater.from(getContext()).inflate(getItemLayoutResId(viewType), leftLayout, false);
        itemView.setId(ID_SWIPE_ITEM_VIEW);
        leftLayout.addView(itemView);
        swipeLayout.addView(leftLayout);
        return swipeLayout;
    }

    /**
     * 获取Item布局视图
     *
     * @param parent   父级
     * @param viewType 类型
     * @return
     */
    protected View getItemView(ViewGroup parent, int viewType) {
        if (getItemSwipeMenuLayoutResId(viewType) != 0) {
            hasSwipeMenu = true;
            return getItemSwipeMenuView(parent, viewType);
        }
        hasSwipeMenu = false;
        return LayoutInflater.from(getContext()).inflate(getItemLayoutResId(viewType), parent, false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getItemView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        viewHolder = (ViewHolder) vh;
        viewHolder.setItemPosition(position);
        viewHolder.setOnItemClickLister(this);
        viewHolder.setOnItemFocusChangeListener(this);
        onItemBindViewHolder(viewHolder, position);
    }

    /**
     * 绑定数据
     *
     * @param holder   控件容器
     * @param position 位置
     */
    protected abstract void onItemBindViewHolder(ViewHolder holder, int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = data == null ? 0 : data.size();
        if (emptyView != null) {
            emptyView.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
        }
        return itemCount;
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setItems(List<T> data) {
        setItems(data, true);
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setItems(List<T> data, boolean notify) {
        this.data = data;
        if (emptyView != null) {
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 设置分页数据
     *
     * @param page 页面
     * @param data 数据
     */
    public void setPageItems(int page, List<T> data) {
        if (page == 1) {
            setItems(data);
        } else {
            addItems(data);
        }
    }

    /**
     * 添加Items
     *
     * @param data
     */
    public void addItems(List<T> data) {
        int size = data == null ? 0 : data.size();
        if (size > 0) {
            int positionStart = getItemCount() - 1;
            getItems().addAll(data);
            notifyItemRangeInserted(positionStart + 1, size);
        }
    }

    /**
     * 添加Item
     *
     * @param t
     */
    public void addItem(T t) {
        if (t != null) {
            getItems().add(t);
        }
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 添加item
     *
     * @param position
     * @param t
     */
    public void addItem(int position, T t) {
        if (t != null) {
            getItems().add(position, t);
        }
        notifyItemRangeInserted(position, 1);
    }

    /**
     * 首位添加
     *
     * @param t
     */
    public void addFirst(T t) {
        if (getItemCount() == 0) {
            addItem(t);
        } else {
            addItem(getItemCount() - 1, t);
        }
    }

    /**
     * 删除Item
     *
     * @param position
     */
    public void removeItem(int position) {
        if (getItemCount() > 0) {
            getItems().remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }

    /**
     * 删除Item
     *
     * @param positionStart
     */
    public void removeItems(int positionStart, int itemCount) {
        if (getItemCount() > 0) {
            for (int i = 0; i < getItemCount() && itemCount <= getItemCount(); i++) {
                if (i >= positionStart && i < itemCount) {
                    getItems().remove(i);
                }
            }
            notifyItemRangeChanged(positionStart, itemCount);
        }
    }

    /**
     * 移动Item
     *
     * @param fromPosition 开始位置
     * @param toPosition   目标位置
     */
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getItems(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(getItems(), i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getItems() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    /**
     * 获取Item
     *
     * @param position 位置
     * @return
     */
    public T getItem(int position) {
        if (getItemCount() == 0) {
            return null;
        }
        if (data == null) {
            return null;
        }
        return data.get(position);
    }

    /**
     * 获取空视图
     *
     * @return
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空视图
     *
     * @param emptyView 视图
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public void onItemClick(View v, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this, v, position);
        }
    }

    @Override
    public void onItemFocusChange(View v, int position, boolean hasFocus) {
        if (onItemFocusChangeListener != null) {
            onItemFocusChangeListener.onItemFocusChange(this, v, position, hasFocus);
        }
    }

    /**
     * Item点击事件
     */
    private OnItemClickListener<T> onItemClickListener;

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 获取Item点击事件
     *
     * @return
     */
    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * Item点击事件回调
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {

        /**
         * Item点击
         *
         * @param adapter  适配器
         * @param v        数据
         * @param position 位置
         */
        void onItemClick(RecyclerAdapter<T> adapter, View v, int position);

    }

    /**
     * 设置焦点改变点击事件
     */
    public OnItemFocusChangeListener<T> onItemFocusChangeListener;

    /**
     * 获取焦点改变事件
     *
     * @return
     */
    public OnItemFocusChangeListener<T> getOnItemFocusChangeListener() {
        return onItemFocusChangeListener;
    }

    /**
     * 获取焦点改变事件
     *
     * @param onItemFocusChangeListener
     */
    public void setOnItemFocusChangeListener(OnItemFocusChangeListener<T> onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    /**
     * 焦点改变事件
     *
     * @param <T>
     */
    public interface OnItemFocusChangeListener<T> {

        /**
         * 焦点修改
         *
         * @param adapter  适配器
         * @param v        控件
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onItemFocusChange(RecyclerAdapter<T> adapter, View v, int position, boolean hasFocus);

    }

}
