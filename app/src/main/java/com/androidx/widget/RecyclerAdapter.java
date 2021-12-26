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

    public static final int ITEM_HEADER = -1;
    public static final int ITEM_FOOTER = -2;
    public static final int ITEM_SWIPE = -3;
    private final int ID_SWIPE_ITEM_LAYOUT = 0x100;
    private final int ID_SWIPE_ITEM_VIEW = 0x101;
    private final int ID_SWIPE_MENU_LAYOUT = 0x200;
    private final int ID_SWIPE_MENU_VIEW = 0x201;
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
     * 初始页面
     */
    private int initPage = 1;

    private View headerView;
    private View footerView;
    private boolean showHeader = true;
    private boolean showFooter = true;
    private boolean showSwipeMenu = true;


    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置头部View
     *
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyDataSetChanged();
    }

    /**
     * 设置脚部View
     *
     * @param footerView
     */
    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHasHeader() && position == 0) {
            return ITEM_HEADER;
        }
        if (isHasFooter() && position == getItemCount() - 1) {
            return ITEM_FOOTER;
        }
        if (isHasSwipeMenu() && isShowSwipeMenu()) {
            return ITEM_SWIPE;
        }
        return super.getItemViewType(position);
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
        return getItemSwipeMenuLayoutResId() != 0;
    }

    /**
     * 是否显示侧滑菜单，默认显示
     *
     * @return
     */
    public boolean isShowSwipeMenu() {
        return showSwipeMenu;
    }

    /**
     * 设置是否显示侧滑菜单
     *
     * @param showSwipeMenu
     */
    public void setShowSwipeMenu(boolean showSwipeMenu) {
        this.showSwipeMenu = showSwipeMenu;
        notifyDataSetChanged();
    }

    /**
     * 滑动菜单LayoutResId
     *
     * @return
     */
    protected int getItemSwipeMenuLayoutResId() {
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
     * 找到侧滑MenuLayout
     *
     * @param childView
     * @return
     */
    public View findSwipeMenuLayout(View childView) {
        return childView.findViewById(ID_SWIPE_MENU_LAYOUT);
    }

    /**
     * 找到MenuView
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
        View menuView = LayoutInflater.from(getContext()).inflate(getItemSwipeMenuLayoutResId(), null);
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
     * 是否有头部
     *
     * @return
     */
    public boolean isHasHeader() {
        return getHeaderLayoutResId() != 0 || getHeaderView() != null;
    }

    /**
     * 是否显示头部
     *
     * @return
     */
    public boolean isShowHeader() {
        return showHeader;
    }

    /**
     * 设置是否显示头部
     *
     * @param showHeader
     */
    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
        notifyDataSetChanged();
    }

    /**
     * 是否有脚部
     *
     * @return
     */
    public boolean isHasFooter() {
        return getFooterLayoutResId() != 0 || getFooterView() != null;
    }

    /**
     * 是否显示脚部
     *
     * @return
     */
    public boolean isShowFooter() {
        return showFooter;
    }

    /**
     * 设置是否显示脚部
     *
     * @param showFooter
     */
    public void setShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
        notifyDataSetChanged();
    }

    /**
     * 获取头部LayoutResId
     *
     * @return
     */
    public int getHeaderLayoutResId() {
        return 0;
    }

    /**
     * 获取脚部LayoutResId
     *
     * @return
     */
    public int getFooterLayoutResId() {
        return 0;
    }

    /**
     * 获取头部View
     *
     * @return
     */
    public View getHeaderView() {
        return headerView;
    }

    /**
     * 获取脚部View
     *
     * @return
     */
    public View getFooterView() {
        return footerView;
    }

    /**
     * 获取Item布局视图
     *
     * @param parent   父级
     * @param viewType 类型
     * @return
     */
    protected View getItemView(ViewGroup parent, int viewType) {
        //头部
        if (isHasHeader() && isShowHeader() && viewType == ITEM_HEADER) {
            return headerView == null ? LayoutInflater.from(getContext()).inflate(getHeaderLayoutResId(), parent, false) : headerView;
        }
        //侧滑
        if (isHasSwipeMenu() && isShowSwipeMenu() && viewType == ITEM_SWIPE) {
            return getItemSwipeMenuView(parent, viewType);
        }
        //脚部
        if (isHasFooter() && isShowFooter() && viewType == ITEM_FOOTER) {
            return footerView == null ? LayoutInflater.from(getContext()).inflate(getFooterLayoutResId(), parent, false) : footerView;
        }
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
        int viewType = getItemViewType(position);
        if (viewType == ITEM_HEADER) {
            onHeaderBindViewHolder(viewHolder, position);
        } else if (viewType == ITEM_FOOTER) {
            onFooterBindViewHolder(viewHolder, position);
        } else if (viewType == ITEM_SWIPE) {
            onSwipeBindViewHolder(viewHolder, position);
        } else {
            onItemBindViewHolder(viewHolder, position);
        }
    }

    /**
     * 头部绑定数据
     *
     * @param holder   控件容器
     * @param position 位置
     */
    protected void onHeaderBindViewHolder(ViewHolder holder, int position) {

    }

    /**
     * 脚部数据绑定
     *
     * @param holder   控件容器
     * @param position 位置
     */
    protected void onFooterBindViewHolder(ViewHolder holder, int position) {

    }

    /**
     * 侧滑数据绑定
     *
     * @param holder
     * @param position
     */
    protected void onSwipeBindViewHolder(ViewHolder holder, int position) {

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
        int size = data == null ? 0 : data.size();
        if (isHasHeader() && isShowHeader()) {
            data.add(0, size == 0 ? (T) new Object() : data.get(0));
        }
        if (isHasFooter() && isShowFooter()) {
            data.add(size == 0 ? 0 : size - 1, size == 0 ? (T) new Object() : data.get(size - 1));
        }
        this.data = data;
        if (emptyView != null) {
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 设置初始页面
     *
     * @param initPage
     */
    public void setInitPage(int initPage) {
        this.initPage = initPage;
    }

    /**
     * 设置分页数据
     *
     * @param page 页面
     * @param data 数据
     */
    public void setPageItems(int page, List<T> data) {
        if (page == initPage) {
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
        int positionStart = getItemCount() - 1;
        if (size > 0) {
            positionStart = getItemCount() - 1;
        }
        getItems().addAll(data);
        notifyItemRangeInserted(positionStart + 1, size);
    }

    /**
     * 添加Item
     *
     * @param t
     */
    public void addItem(T t) {
        if (t != null) {
            getItems().add(t);
            notifyItemInserted(getItemCount() - 1);
        }
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
            notifyItemRangeInserted(position, 1);
        }
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
            if (isHasHeader() && isShowHeader()) {
                addItem(1, t);
            } else {
                addItem(0, t);
            }
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
        if ((isHasHeader() && isHasHeader()) && !(isHasFooter() && isShowFooter())) {
            if (data.size() > 1) {
                data.subList(1, getItemCount() - 1);
            }
        }
        if (!(isHasHeader() && isHasHeader()) && (isHasFooter() && isShowFooter())) {
            if (data.size() > 2) {
                data.subList(0, getItemCount() - 2);
            }
        }
        if ((isHasHeader() && isHasHeader()) && (isHasFooter() && isShowFooter())) {
            if (data.size() > 2) {
                data.subList(1, getItemCount() - 2);
            }
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
     * @param <ITEM>
     */
    public interface OnItemFocusChangeListener<ITEM> {

        /**
         * 焦点修改
         *
         * @param adapter  适配器
         * @param v        控件
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onItemFocusChange(RecyclerAdapter<ITEM> adapter, View v, int position, boolean hasFocus);

    }

}
