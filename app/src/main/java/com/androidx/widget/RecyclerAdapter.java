package com.androidx.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Relin
 * Describe:Recycler使用的基础Adapter
 * Date:2020/12/26 19:17
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter implements RecyclerScrollHelper.OnScrollListener,
        ViewHolder.OnItemClickLister, ViewHolder.OnItemFocusChangeListener {

    private final String TAG = "RecyclerAdapter";
    private final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * Item视图 - 头部
     */
    public static final int EXTRA_HEADER = -1;
    /**
     * Item视图 - 脚部
     */
    public static final int EXTRA_FOOTER = -2;
    /**
     * Item视图 - 加载更多
     */
    public static final int EXTRA_LOADING = -3;

    /**
     * 侧滑item - ViewGroup
     */
    private final int ID_SWIPE_ITEM_GROUP = 1;
    /**
     * 侧滑菜单 - ViewGroup
     */
    private final int ID_SWIPE_MENU_GROUP = 2;
    /**
     * 侧滑菜单
     */
    private final int ID_SWIPE_MENU = 3;

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 数据对象
     */
    private List<Complex<T>> complexSources;
    private List<Extra> extraSources;
    private List<T> dataSources;
    /**
     * 空视图
     */
    private View placeholder;
    /**
     * 头部View
     */
    private View headerView;
    /**
     * 脚部View
     */
    private View footerView;
    /**
     * 更多View
     */
    private View loadingView;

    /**
     * 头部ViewHolder
     */
    private ViewHolder headerViewHolder;
    /**
     * 脚部ViewHolder
     */
    private ViewHolder footerViewHolder;
    /**
     * 加载更多ViewHolder
     */
    private ViewHolder loadingViewHolder;

    /**
     * 是否显示头部
     */
    private boolean showHeader = true;
    /**
     * 是否显示脚部
     */
    private boolean showFooter = true;
    /**
     * 是否显示侧滑菜单
     */
    private boolean showSwipe = true;
    /**
     * 是否显示加载更多
     */
    private boolean showLoading = false;

    /**
     * 头部参数
     */
    private Bundle headerArgs;
    /**
     * 脚部参数
     */
    private Bundle footerArgs;
    /**
     * 加载更多参数
     */
    private Bundle loadingArgs;

    /**
     * 列表View
     */
    private RecyclerView recyclerView;
    /**
     * 滑动容器
     */
    private NestedScrollView nestedScrollView;
    /**
     * 滑动助手
     */
    private RecyclerScrollHelper scrollHelper;

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 获取适配器对象
     *
     * @return
     */
    public RecyclerAdapter getRecyclerAdapter() {
        return this;
    }

    /**
     * 连接RecyclerView (加载更多必须调用此方法)
     *
     * @param view
     */
    public void attachRecyclerView(RecyclerView view) {
        if (scrollHelper == null) {
            scrollHelper = new RecyclerScrollHelper();
            scrollHelper.setOnScrollListener(this);
        }
        view.addOnScrollListener(scrollHelper);
        recyclerView = view;
    }

    /**
     * 获取列表控件
     *
     * @return
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }


    /**
     * 连接NestedScrollView (父容器有NestedScrollView，加载更多必须调用此方法)
     *
     * @param view
     */
    public void attachNestedScrollView(NestedScrollView view) {
        if (scrollHelper == null) {
            scrollHelper = new RecyclerScrollHelper();
            scrollHelper.setOnScrollListener(this);
        }
        view.setOnScrollChangeListener(scrollHelper);
        nestedScrollView = view;
    }

    /**
     * 获取NestedScrollView
     *
     * @return
     */
    public NestedScrollView getNestedScrollView() {
        return nestedScrollView;
    }

    /**
     * 获取滑动助手
     *
     * @return
     */
    public RecyclerScrollHelper getScrollHelper() {
        return scrollHelper;
    }

    /**
     * 获取加载更多Layout
     *
     * @return
     */
    public SwipeLoadingLayout findSwipeLoadingLayout() {
        if (getLoadingViewHolder() != null) {
            return getLoadingViewHolder().find(R.id.item_loading_more);
        }
        return null;
    }

    @Override
    public void onScrolled(RecyclerView v, int dx, int dy) {
        onScrolledMore(v);
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        onScrolledMore(v);
    }

    /**
     * 处理滑动加载更多
     *
     * @param v
     */
    protected void onScrolledMore(View v) {
        if (isHasLoading() && isShowLoading()) {
            boolean isScrolledTop = v.canScrollVertically(1);
            boolean isScrolledBottom = v.canScrollVertically(-1);
            boolean isNotMore = !isScrolledTop && !isScrolledBottom;
            SwipeLoadingLayout layout = findSwipeLoadingLayout();
            if (layout != null) {
                //数据未填满情况
                if (isNotMore) {
                    layout.setLoading(false);
                }
                //列表中间位置
                if (isScrolledTop && isScrolledBottom) {
                    layout.setLoading(true);
                }
            }
            //滑动到底部情况
            if (isScrolledBottom && !isScrolledTop) {
                if (layout != null) {
                    layout.setLoading(true);
                }
                if (onLoadingListener != null) {
                    onLoadingListener.onLoading();
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    //============================Header=========================

    /**
     * 获取头部LayoutResId
     *
     * @return
     */
    public int getHeaderLayoutResId() {
        return 0;
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
     * 获取头部View
     *
     * @return
     */
    public View getHeaderView() {
        return headerView;
    }

    /**
     * 设置头部参数
     *
     * @param args
     */
    public void setHeaderArgs(Bundle args) {
        this.headerArgs = args;
        notifyDataSetChanged();
    }

    /**
     * 获取脚部参数
     *
     * @return
     */
    public Bundle getHeaderArgs() {
        return headerArgs;
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
     * 获取头部ViewHolder
     *
     * @return
     */
    public ViewHolder getHeaderViewHolder() {
        return headerViewHolder;
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
        setDataSource(dataSources);
    }


    //============================Footer=========================

    /**
     * 获取脚部LayoutResId
     *
     * @return
     */
    public int getFooterLayoutResId() {
        return 0;
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

    /**
     * 获取脚部View
     *
     * @return
     */
    public View getFooterView() {
        return footerView;
    }

    /**
     * 设置脚部参数
     *
     * @param args
     */
    public void setFooterArgs(Bundle args) {
        this.footerArgs = args;
        notifyDataSetChanged();
    }

    /**
     * 获取脚部参数
     *
     * @return
     */
    public Bundle getFooterArgs() {
        return footerArgs;
    }

    /**
     * 获取脚部ViewHolder
     *
     * @return
     */
    public ViewHolder getFooterViewHolder() {
        return footerViewHolder;
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
        setDataSource(dataSources);
    }

    //============================More=========================

    /**
     * 加载更多布局
     *
     * @return
     */
    public int getLoadingLayoutResId() {
        return R.layout.androidx_load_more;
    }

    /**
     * 是否有更多View
     *
     * @return
     */
    public boolean isHasLoading() {
        return getLoadingLayoutResId() != 0 || getLoadingView() != null;
    }

    /**
     * 设置加载更多View
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
        notifyDataSetChanged();
    }

    /**
     * 获取加载更多View
     *
     * @return
     */
    public View getLoadingView() {
        return loadingView;
    }

    /**
     * 设置加载更多参数
     *
     * @param args
     */
    public void setLoadingArgs(Bundle args) {
        this.loadingArgs = args;
        notifyDataSetChanged();
    }

    /**
     * 获取加载更多参数
     *
     * @return
     */
    public Bundle getLoadingArgs() {
        return loadingArgs;
    }

    /**
     * 获取加载更多ViewHolder
     *
     * @return
     */
    public ViewHolder getLoadingViewHolder() {
        return loadingViewHolder;
    }

    /**
     * 是否显示加载更多布局
     *
     * @return
     */
    public boolean isShowLoading() {
        return showLoading;
    }

    /**
     * 设置是否显示加载更多布局
     *
     * @param showLoading
     */
    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        setDataSource(dataSources);
    }

    /**
     * 设置是否有更多数据
     *
     * @param loading
     */
    public void setLoading(boolean loading) {
        SwipeLoadingLayout loadingLayout = findSwipeLoadingLayout();
        if (loadingLayout != null) {
            loadingLayout.setLoading(loading);
        }
    }

    //============================SwipeMenu=========================

    /**
     * 滑动菜单LayoutResId
     *
     * @return
     */
    protected int getItemSwipeMenuLayoutResId() {
        return 0;
    }

    /**
     * 是否有菜单
     *
     * @return
     */
    public boolean isHasSwipe() {
        return getItemSwipeMenuLayoutResId() != 0;
    }


    /**
     * 是否显示侧滑菜单，默认显示
     *
     * @return
     */
    public boolean isShowSwipe() {
        return showSwipe;
    }

    /**
     * 设置是否显示侧滑菜单
     *
     * @param showSwipe
     */
    public void setShowSwipe(boolean showSwipe) {
        this.showSwipe = showSwipe;
        notifyDataSetChanged();
    }

    /**
     * 找到侧滑ItemLayout
     *
     * @param child
     * @return
     */
    public View findSwipeItemLayout(View child) {
        return child.findViewById(ID_SWIPE_ITEM_GROUP);
    }

    /**
     * 找到侧滑MenuLayout
     *
     * @param child
     * @return
     */
    public View findSwipeMenuLayout(View child) {
        return child.findViewById(ID_SWIPE_MENU_GROUP);
    }

    /**
     * 找到MenuView
     *
     * @param child
     * @return
     */
    public View findSwipeMenuView(View child) {
        return child.findViewById(ID_SWIPE_MENU);
    }

    /**
     * 获取item菜单View
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected View getItemSwipeMenuView(ViewGroup parent, int viewType) {
        FrameLayout swipeLayout = new FrameLayout(getContext());
        swipeLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //Menu
        FrameLayout menuLayout = new FrameLayout(getContext());
        menuLayout.setId(ID_SWIPE_MENU_GROUP);
        menuLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        View menuView = LayoutInflater.from(getContext()).inflate(getItemSwipeMenuLayoutResId(), null);
        menuView.setId(ID_SWIPE_MENU);
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        menuParams.gravity = Gravity.RIGHT;
        menuLayout.addView(menuView, menuParams);
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        menuView.measure(measureSpec, measureSpec);
        menuLayout.setTranslationX(menuView.getMeasuredWidth());
        swipeLayout.addView(menuLayout);
        //RecyclerView itemView
        FrameLayout leftLayout = new FrameLayout(getContext());
        leftLayout.setId(ID_SWIPE_ITEM_GROUP);
        leftLayout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        View itemView = LayoutInflater.from(getContext()).inflate(getItemLayoutResId(viewType), leftLayout, false);
        leftLayout.addView(itemView);
        swipeLayout.addView(leftLayout);
        return swipeLayout;
    }

    @Override
    public int getItemViewType(int position) {
        if (isExtra(position)) {
            Extra item = getExtra(position);
            return item.getViewType();
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
     * 获取Item布局视图
     *
     * @param parent   父级
     * @param viewType 类型
     * @return
     */
    protected View getItemView(ViewGroup parent, int viewType) {
        //头部
        if (isHasHeader() && isShowHeader() && viewType == EXTRA_HEADER) {
            return headerView == null ? inflate(parent, getHeaderLayoutResId()) : attachToFrameLayout(headerView);
        }
        //脚部
        if (isHasFooter() && isShowFooter() && viewType == EXTRA_FOOTER) {
            return footerView == null ? inflate(parent, getFooterLayoutResId()) : attachToFrameLayout(footerView);
        }
        //加载更多
        if (isHasLoading() && isShowLoading() && viewType == EXTRA_LOADING) {
            return loadingView == null ? inflate(parent, getLoadingLayoutResId()) : attachToFrameLayout(loadingView);
        }
        //侧滑
        if (isSwipeEnable()) {
            return getItemSwipeMenuView(parent, viewType);
        }
        return inflate(parent, getItemLayoutResId(viewType));
    }

    /**
     * 根据LayoutResourceId获取View
     *
     * @param parent   父级
     * @param resource 资源ID
     * @return
     */
    protected View inflate(ViewGroup parent, @LayoutRes int resource) {
        return LayoutInflater.from(getContext()).inflate(resource, parent, false);
    }

    /**
     * 附加到FrameLayout
     *
     * @param v
     * @return
     */
    protected View attachToFrameLayout(View v) {
        FrameLayout layout = new FrameLayout(getContext());
        layout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        layout.addView(v);
        return layout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getItemView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ViewHolder holder = (ViewHolder) vh;
        addItemClick(holder, position);
        int viewType = getItemViewType(position);
        if (viewType == EXTRA_HEADER) {
            onHeaderBindViewHolder(holder, headerArgs);
        } else if (viewType == EXTRA_FOOTER) {
            onFooterBindViewHolder(holder, footerArgs);
        } else if (viewType == EXTRA_LOADING) {
            onLoadingBindViewHolder(holder, loadingArgs);
        } else {
            if (isSwipeEnable()) {
                onSwipeBindViewHolder(holder, position);
            } else {
                onItemBindViewHolder(holder, position);
            }
        }
    }

    /**
     * 添加item点击事件
     *
     * @param holder
     * @param position
     */
    private void addItemClick(ViewHolder holder, int position) {
        holder.setOnItemClickLister(this);
        holder.setOnItemFocusChangeListener(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (isExtra(position)) {
            if (onExtraItemClickListener != null) {
                onExtraItemClickListener.onExtraItemClick(this, v, position);
            }
        } else {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(this, v, position);
            }
        }
    }

    @Override
    public void onItemFocusChange(View v, int position, boolean hasFocus) {
        if (isExtra(position)) {
            if (onExtraItemClickListener != null) {
                onExtraItemClickListener.onExtraItemClick(this, v, position);
            }
        } else {
            if (onItemFocusChangeListener != null) {
                onItemFocusChangeListener.onItemFocusChange(this, v, position, hasFocus);
            }
        }
    }

    /**
     * 绑定数据
     *
     * @param holder   控件容器
     * @param position 位置
     */
    protected abstract void onItemBindViewHolder(ViewHolder holder, int position);

    /**
     * 头部绑定数据
     *
     * @param holder 控件容器
     * @param args   参数
     */
    protected void onHeaderBindViewHolder(ViewHolder holder, Bundle args) {
        headerViewHolder = holder;
    }

    /**
     * 脚部数据绑定
     *
     * @param holder 控件容器
     * @param args   参数
     */
    protected void onFooterBindViewHolder(ViewHolder holder, Bundle args) {
        footerViewHolder = holder;
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
     * 加载更多数据绑定
     *
     * @param holder 控件容器
     * @param args   参数
     */
    protected void onLoadingBindViewHolder(ViewHolder holder, Bundle args) {
        loadingViewHolder = holder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = complexSources == null ? 0 : complexSources.size();
        if (placeholder != null) {
            placeholder.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
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
     * 是否支持侧滑
     *
     * @return
     */
    public boolean isSwipeEnable() {
        return isHasSwipe() && isShowSwipe();
    }

    /**
     * 设置数据源
     *
     * @param sources
     */
    public void setDataSource(List<T> sources) {
        setDataSource(sources, true);
    }

    /**
     * 设置数据源
     *
     * @param sources
     */
    public void setDataSource(List<T> sources, boolean notify) {
        complexSources = new ArrayList<>();
        dataSources = new ArrayList<>();
        extraSources = new ArrayList<>();
        if (isHasHeader() && isShowHeader()) {
            Extra extra = new Extra(EXTRA_HEADER);
            extraSources.add(extra);
            complexSources.add(new Complex(extra));
        }
        int size = sources == null ? 0 : sources.size();
        for (int i = 0; i < size; i++) {
            complexSources.add(new Complex(sources.get(i), isSwipeEnable()));
        }
        dataSources = sources;
        if (isHasFooter() && isShowFooter()) {
            Extra extra = new Extra(EXTRA_FOOTER);
            extraSources.add(extra);
            complexSources.add(new Complex(extra));
        }
        if (isHasLoading() && isShowLoading()) {
            Extra extra = new Extra(EXTRA_LOADING);
            extraSources.add(extra);
            complexSources.add(new Complex(extra));
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 获取组合的复杂数据源（普通+[Header/Footer/Loading]）
     *
     * @return
     */
    public List<Complex<T>> getComplexSources() {
        return complexSources;
    }

    /**
     * 获取额外数据源
     *
     * @return
     */
    public List<Extra> getExtraSources() {
        return extraSources;
    }

    /**
     * 获取普通源数据
     *
     * @return
     */
    public List<T> getDataSources() {
        return dataSources;
    }

    /**
     * 在复杂数据源中查找数据源
     *
     * @return
     */
    public List<T> findDataSources() {
        List<T> dataSources = new ArrayList<>();
        for (int i = 0; i < complexSources.size(); i++) {
            T item = complexSources.get(i).getItem();
            if (item != null) {
                dataSources.add(item);
            }
        }
        return dataSources;
    }

    /**
     * 是否是普通Item
     *
     * @param position
     * @return
     */
    public boolean isItem(int position) {
        return complexSources.get(position).getItem() != null;
    }

    /**
     * 获取Item数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return complexSources.get(position).getItem();
    }

    /**
     * 是否是额外Item(Header/Footer/More)
     *
     * @param position
     * @return
     */
    public boolean isExtra(int position) {
        return complexSources.get(position).getExtra() != null;
    }

    /**
     * 获取额外Item
     *
     * @param position
     * @return
     */
    public Extra getExtra(int position) {
        return complexSources.get(position).getExtra();
    }

    /**
     * 添加Item
     *
     * @param item
     */
    public void addItem(T item) {
        dataSources.add(item);
        setDataSource(dataSources, false);
        int position = dataSources.size();
        if (isHasHeader() && isShowHeader()) {
            position = dataSources.size() + 1;
        }
        notifyItemInserted(position);
    }

    /**
     * 添加Item
     *
     * @param position 位置
     * @param item     数据item
     */
    public void addItem(int position, T item) {
        complexSources.add(position, new Complex(item, isSwipeEnable()));
        setDataSource(findDataSources(), false);
        int insertPosition = dataSources.size();
        if (isHasHeader() && isShowHeader()) {
            insertPosition = dataSources.size() + 1;
        }
        notifyItemInserted(insertPosition);
    }

    /**
     * 添加第一个Item
     *
     * @param item
     */
    public void addFirst(T item) {
        dataSources.add(0, item);
        setDataSource(dataSources, false);
        int position = 0;
        if (isHasHeader() && isShowHeader()) {
            position = 1;
        }
        notifyItemInserted(position);
    }

    /**
     * 设置分页数据
     *
     * @param page    页面
     * @param sources 来源
     */
    public void setPageDataSource(int page, List<T> sources) {
        setPageDataSource(1, page, sources);
    }

    /**
     * 设置分页数据
     *
     * @param init    初始
     * @param page    页面
     * @param sources 来源
     */
    public void setPageDataSource(int init, int page, List<T> sources) {
        if (page == init) {
            setDataSource(sources);
        } else {
            addItems(sources);
        }
    }

    /**
     * 添加多个Item
     *
     * @param sources 来源
     */
    public void addItems(List<T> sources) {
        int count = sources == null ? 0 : sources.size();
        for (int i = 0; i < count; i++) {
            dataSources.add(sources.get(i));
        }
        setDataSource(dataSources, false);
        int positionStart = getItemCount() - 1;
        if (count > 0) {
            positionStart = getItemCount() - 1;
        }
        notifyItemRangeInserted(positionStart + 1, count);
    }

    /**
     * 删除item
     *
     * @param position 位置
     */
    public void removeItem(int position) {
        complexSources.remove(position);
        setDataSource(findDataSources(), false);
        notifyItemRemoved(position);
    }

    /**
     * 删除多个Item
     *
     * @param positionStart 开始位置
     * @param itemCount     个数
     */
    public void removeItems(int positionStart, int itemCount) {
        if (positionStart < getItemCount() && (itemCount - 1 + positionStart) < getItemCount()) {
            complexSources.removeAll(complexSources.subList(positionStart, positionStart + itemCount));
            setDataSource(findDataSources(), false);
            notifyItemRangeRemoved(positionStart, itemCount);
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
                Collections.swap(complexSources, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(complexSources, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 获取空视图
     *
     * @return
     */
    public View getPlaceholder() {
        return placeholder;
    }

    /**
     * 设置空视图
     *
     * @param placeholder 占位视图
     */
    public void setPlaceholder(View placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Item点击事件
     */
    private OnItemClickListener onItemClickListener;

    /**
     * 获取Item点击事件
     *
     * @return
     */
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Item点击事件回调
     */
    public interface OnItemClickListener {

        /**
         * Item点击
         *
         * @param adapter  适配器
         * @param v        数据
         * @param position 位置
         */
        void onItemClick(RecyclerAdapter adapter, View v, int position);

    }

    /**
     * 设置焦点改变点击事件
     */
    public OnItemFocusChangeListener onItemFocusChangeListener;

    /**
     * 获取焦点改变事件
     *
     * @return
     */
    public OnItemFocusChangeListener getOnItemFocusChangeListener() {
        return onItemFocusChangeListener;
    }

    /**
     * 获取焦点改变事件
     *
     * @param onItemFocusChangeListener
     */
    public void setOnItemFocusChangeListener(OnItemFocusChangeListener onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    /**
     * 焦点改变事件
     */
    public interface OnItemFocusChangeListener {

        /**
         * 焦点修改
         *
         * @param adapter  适配器
         * @param v        控件
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onItemFocusChange(RecyclerAdapter adapter, View v, int position, boolean hasFocus);

    }

    /**
     * 附加Item点击
     */
    private OnExtraItemClickListener onExtraItemClickListener;

    /**
     * 获取附加Item点击
     *
     * @return
     */
    public OnExtraItemClickListener getOnExtraItemClickListener() {
        return onExtraItemClickListener;
    }

    /**
     * 设置附加Item点击<br/>
     * Header、Footer、Loading<br/>
     *
     * @param onExtraItemClickListener
     */
    public void setOnExtraItemClickListener(OnExtraItemClickListener onExtraItemClickListener) {
        this.onExtraItemClickListener = onExtraItemClickListener;
    }

    public interface OnExtraItemClickListener {

        /**
         * 附加Item点击
         *
         * @param adapter  适配器
         * @param v        数据
         * @param position 位置
         */
        void onExtraItemClick(RecyclerAdapter adapter, View v, int position);


    }

    /**
     * 附加焦点改变事件
     */
    private OnExtraItemFocusChangeListener onExtraItemFocusChangeListener;

    /**
     * 获取附加焦点改变事件
     *
     * @return
     */
    public void setOnExtraItemFocusChangeListener(OnExtraItemFocusChangeListener onExtraItemFocusChangeListener) {
        this.onExtraItemFocusChangeListener = onExtraItemFocusChangeListener;
    }
    /**
     * 设置附加焦点改变事件
     *
     * @param onAttachFocusChangeListener
     */

    /**
     * 附加焦点改变事件
     */
    public interface OnExtraItemFocusChangeListener {

        /**
         * 附加焦点改变
         *
         * @param adapter  适配器
         * @param v        控件
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onExtraItemFocusChange(RecyclerAdapter adapter, View v, int position, boolean hasFocus);

    }

    /**
     * 加载更多监听
     */
    private OnLoadingListener onLoadingListener;

    /**
     * 获取加载更多监听
     *
     * @return
     */
    public OnLoadingListener getOnLoadingListener() {
        return onLoadingListener;
    }

    /**
     * 设置加载更多监听
     *
     * @param onLoadingListener
     */
    public void setOnLoadListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    public interface OnLoadingListener {

        /**
         * 加载更多
         */
        void onLoading();

    }

    /**
     * 混合Item
     *
     * @param <T>
     */
    public static class Complex<T> {

        /**
         * 是否支持侧滑
         */
        private boolean swipe;
        /**
         * 普通item
         */
        private T item;
        /**
         * 额外Item
         */
        private Extra extra;

        public Complex(Extra extra) {
            this.extra = extra;
        }

        public Complex(T item, boolean swipe) {
            this.item = item;
            this.swipe = swipe;
        }

        public boolean isSwipe() {
            return swipe;
        }

        public void setSwipe(boolean swipe) {
            this.swipe = swipe;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public Extra getExtra() {
            return extra;
        }

        public void setExtra(Extra extra) {
            this.extra = extra;
        }

    }

    public static class Extra {

        /**
         * 视图类型 {@link #EXTRA_HEADER} or {@link #EXTRA_FOOTER} or {@link #EXTRA_LOADING}
         */
        private int viewType;

        public Extra(int viewType) {
            this.viewType = viewType;
        }

        public int getViewType() {
            return viewType;
        }
    }

}
