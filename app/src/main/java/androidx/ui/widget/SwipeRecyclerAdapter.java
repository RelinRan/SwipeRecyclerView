package androidx.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidx.widget.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Recycler使用的基础Adapter
 */
public abstract class SwipeRecyclerAdapter<T> extends RecyclerView.Adapter implements SwipeRecyclerScrollListener.OnScrollListener, ViewHolder.OnItemClickLister, ViewHolder.OnItemFocusChangeListener {

    private final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
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
     * 混合数据集合
     */
    private List<SwipeItem<T>> items;
    /**
     * 扩展数据集合
     */
    private List<SwipeExpansion> expansions;
    /**
     * 普通数据集合
     */
    private List<T> ordinaries;
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
    private boolean showSwipe = false;
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
    private SwipeRecyclerScrollListener scrollListener;
    /**
     * 横向滑动阈值
     */
    private int swipeThreshold = 30;
    /**
     * 侧滑移动百分比
     */
    private float sideRatio = 0.5F;
    /**
     * 侧滑动画持续事件
     */
    private int sideDuration = 300;

    public SwipeRecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * @param id 颜色Id
     * @return 颜色
     */
    public int getColor(@ColorRes int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * @param color 颜色字符
     * @return 颜色
     */
    public int parseColor(String color) {
        return Color.parseColor(color);
    }

    /**
     * 获取适配器对象
     *
     * @return
     */
    public SwipeRecyclerAdapter getRecyclerAdapter() {
        return this;
    }

    /**
     * 连接RecyclerView (加载更多必须调用此方法)
     *
     * @param view
     */
    public void attachRecyclerView(RecyclerView view) {
        if (scrollListener == null) {
            scrollListener = new SwipeRecyclerScrollListener();
            scrollListener.setOnScrollListener(this);
        }
        view.addOnScrollListener(scrollListener);
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
        if (scrollListener == null) {
            scrollListener = new SwipeRecyclerScrollListener();
            scrollListener.setOnScrollListener(this);
        }
        view.setOnScrollChangeListener(scrollListener);
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
    public SwipeRecyclerScrollListener getRecyclerScrollListener() {
        return scrollListener;
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
        setDataSource(ordinaries);
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
        setDataSource(ordinaries);
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
        setDataSource(ordinaries);
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
        if (isExpansion(position)) {
            return getExpansion(position).getViewType();
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
        if (isHasHeader() && isShowHeader() && viewType == SwipeExpansion.HEADER) {
            return headerView == null ? inflate(parent, getHeaderLayoutResId()) : attachToFrameLayout(headerView);
        }
        //脚部
        if (isHasFooter() && isShowFooter() && viewType == SwipeExpansion.FOOTER) {
            return footerView == null ? inflate(parent, getFooterLayoutResId()) : attachToFrameLayout(footerView);
        }
        //加载更多
        if (isHasLoading() && isShowLoading() && viewType == SwipeExpansion.LOADING) {
            return loadingView == null ? inflate(parent, getLoadingLayoutResId()) : attachToFrameLayout(loadingView);
        }
        //侧滑
        if (isSwipeEnable()) {
            return getItemSwipeMenuView(parent, viewType);
        }
        return inflate(parent, getItemLayoutResId(viewType));
    }

    /**
     * @param parent   父级
     * @param resource 资源ID
     * @return 根据LayoutResourceId获取View
     */
    protected View inflate(ViewGroup parent, @LayoutRes int resource) {
        return LayoutInflater.from(getContext()).inflate(resource, parent, false);
    }

    /**
     * @param v 控件
     * @return 附加到FrameLayout
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
        if (viewType == SwipeExpansion.HEADER) {
            onHeaderBindViewHolder(holder, headerArgs);
        } else if (viewType == SwipeExpansion.FOOTER) {
            onFooterBindViewHolder(holder, footerArgs);
        } else if (viewType == SwipeExpansion.LOADING) {
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
     * @param holder   控件容器
     * @param position 位置
     */
    private void addItemClick(ViewHolder holder, int position) {
        holder.setOnItemClickLister(this);
        holder.setOnItemFocusChangeListener(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (isExpansion(position)) {
            if (onExpansionItemClickListener != null) {
                onExpansionItemClickListener.onExpansionItemClick(this, v, position);
            }
        } else {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(this, v, position);
            }
        }
    }

    @Override
    public void onItemFocusChange(View v, int position, boolean hasFocus) {
        if (isExpansion(position)) {
            if (onExpansionItemClickListener != null) {
                onExpansionItemClickListener.onExpansionItemClick(this, v, position);
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
    protected void onItemBindViewHolder(ViewHolder holder, int position) {

    }

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
     * @param holder   控件容器
     * @param position 位置
     */
    protected void onSwipeBindViewHolder(ViewHolder holder, int position) {
        if (getItems().get(position).isSwipe()) {
            SwipeItemTouchEvent touchEvent = new SwipeItemTouchEvent(this, holder, position);
            touchEvent.setSideDuration(sideDuration);
            touchEvent.setSideRatio(sideRatio);
            touchEvent.setSwipeThreshold(swipeThreshold);
            holder.itemView.setOnTouchListener(touchEvent);
        }
    }

    /**
     * 设置item侧滑是否已打开
     *
     * @param position 位置
     * @param open     是否打开
     */
    public void setItemSwipeOpen(int position, boolean open) {
        getItems().get(position).setOpen(open);
    }

    /**
     * 侧滑菜单是否打开
     *
     * @param position
     * @return
     */
    public boolean isItemSwipeOpen(int position) {
        return getItems().get(position).isSwipe();
    }

    /**
     * 设置侧滑菜单状态
     *
     * @param position 位置
     * @param open     是否打开
     */
    public void setSwipeStatus(int position, boolean open) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
        View itemLayout = findSwipeItemLayout(holder.itemView);
        View menuLayout = findSwipeMenuLayout(holder.itemView);
        View menuView = findSwipeMenuView(holder.itemView);
        int menuWidth = menuView == null ? 0 : menuView.getMeasuredWidth();
        getItems().get(position).setOpen(open);
        if (menuLayout != null && itemLayout != null) {
            if (open) {
                itemLayout.setTranslationX(-menuWidth);
                menuLayout.setTranslationX(0);
            } else {
                itemLayout.setTranslationX(0);
                menuLayout.setTranslationX(0);
            }
        }
    }

    /**
     * 打开侧滑菜单
     * @param position 位置
     */
    public void openSwipe(int position) {
        setSwipeStatus(position, true);
    }

    /**
     * 关闭侧滑菜单
     * @param position 位置
     */
    public void closeSwipe(int position) {
        setSwipeStatus(position, false);
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
        int itemCount = items == null ? 0 : items.size();
        if (placeholder != null) {
            placeholder.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
        }
        return itemCount;
    }

    /**
     * @return 获取上下文对象
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return 是否支持侧滑
     */
    public boolean isSwipeEnable() {
        return isHasSwipe() && isShowSwipe();
    }

    /**
     * 设置数据源
     *
     * @param sources 数据源
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
        items = new ArrayList<>();
        ordinaries = new ArrayList<>();
        expansions = new ArrayList<>();
        if (isHasHeader() && isShowHeader()) {
            SwipeExpansion expansion = new SwipeExpansion(SwipeExpansion.HEADER);
            expansions.add(expansion);
            items.add(new SwipeItem(expansion));
        }
        int size = sources == null ? 0 : sources.size();
        for (int i = 0; i < size; i++) {
            items.add(new SwipeItem(sources.get(i), isSwipeEnable()));
        }
        ordinaries = sources;
        if (isHasFooter() && isShowFooter()) {
            SwipeExpansion expansion = new SwipeExpansion(SwipeExpansion.FOOTER);
            expansions.add(expansion);
            items.add(new SwipeItem(expansion));
        }
        if (isHasLoading() && isShowLoading()) {
            SwipeExpansion expansion = new SwipeExpansion(SwipeExpansion.LOADING);
            expansions.add(expansion);
            items.add(new SwipeItem(expansion));
        }
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * @return 组合的复杂数据源（普通+[Header/Footer/Loading]）
     */
    public List<SwipeItem<T>> getItems() {
        return items;
    }

    /**
     * @return 扩展数据源
     */
    public List<SwipeExpansion> getExpansions() {
        return expansions;
    }

    /**
     * @return 普通源数据
     */
    public List<T> getDataSources() {
        return ordinaries;
    }

    /**
     * @return 在复杂数据源中查找数据源
     */
    public List<T> findDataSources() {
        List<T> dataSources = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i).getOrdinary();
            if (item != null) {
                dataSources.add(item);
            }
        }
        return dataSources;
    }

    /**
     * @param position
     * @return 是否是普通Item
     */
    public boolean isItem(int position) {
        return items.get(position).getOrdinary() != null;
    }

    /**
     * @param position 位置
     * @return Item数据
     */
    public T getItem(int position) {
        return items.get(position).getOrdinary();
    }

    /**
     * @param position
     * @return 是否是扩展Item(Header / Footer / More)
     */
    public boolean isExpansion(int position) {
        return items.get(position).getExpansion() != null;
    }

    /**
     * @param position 位置
     * @return 扩展Item
     */
    public SwipeExpansion getExpansion(int position) {
        return items.get(position).getExpansion();
    }

    /**
     * @param position 位置
     * @return 扩展视图类型
     */
    public int getExpansionViewType(int position) {
        return getExpansion(position).getViewType();
    }

    /**
     * 添加Item
     *
     * @param item
     */
    public void addItem(T item) {
        ordinaries.add(item);
        setDataSource(ordinaries, false);
        int position = ordinaries.size();
        notifyItemInserted(position);
    }

    /**
     * 添加Item
     *
     * @param position 位置
     * @param item     数据item
     */
    public void addItem(int position, T item) {
        items.add(position, new SwipeItem(item, isSwipeEnable()));
        setDataSource(findDataSources(), false);
        notifyItemInserted(position);
    }

    /**
     * 添加第一个Item
     *
     * @param item
     */
    public void addFirst(T item) {
        ordinaries.add(0, item);
        setDataSource(ordinaries, false);
        int position = isHasHeader() && isShowHeader() ? 1 : 0;
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
            ordinaries.add(sources.get(i));
        }
        if (count > 0) {
            setDataSource(ordinaries, true);
        }
    }

    /**
     * 删除item
     *
     * @param position 位置
     */
    public void removeItem(int position) {
        items.remove(position);
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
            items.removeAll(items.subList(positionStart, positionStart + itemCount));
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
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
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
     * Item点击事件回调
     */
    public interface OnItemClickListener<T> {

        /**
         * Item点击
         *
         * @param adapter  适配器
         * @param v        数据
         * @param position 位置
         */
        void onItemClick(SwipeRecyclerAdapter<T> adapter, View v, int position);

    }

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
     * 设置焦点改变点击事件
     */
    public OnItemFocusChangeListener onItemFocusChangeListener;

    /**
     * 焦点改变事件
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
        void onItemFocusChange(SwipeRecyclerAdapter<T> adapter, View v, int position, boolean hasFocus);

    }

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
     * 附加Item点击
     */
    private OnExpansionItemClickListener onExpansionItemClickListener;

    public interface OnExpansionItemClickListener<T> {

        /**
         * 附加Item点击
         *
         * @param adapter  适配器
         * @param v        数据
         * @param position 位置
         */
        void onExpansionItemClick(SwipeRecyclerAdapter<T> adapter, View v, int position);

    }

    /**
     * 获取附加Item点击
     *
     * @return
     */
    public OnExpansionItemClickListener getOnExpansionItemClickListener() {
        return onExpansionItemClickListener;
    }

    /**
     * 设置附加Item点击<br/>
     * Header、Footer、Loading<br/>
     *
     * @param onExpansionItemClickListener
     */
    public void setOnExpansionItemClickListener(OnExpansionItemClickListener onExpansionItemClickListener) {
        this.onExpansionItemClickListener = onExpansionItemClickListener;
    }

    /**
     * 附加焦点改变事件
     */
    private OnExpansionItemFocusChangeListener onExpansionItemFocusChangeListener;

    /**
     * 附加焦点改变事件
     */
    public interface OnExpansionItemFocusChangeListener<T> {

        /**
         * 附加焦点改变
         *
         * @param adapter  适配器
         * @param v        控件
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onExpansionItemFocusChange(SwipeRecyclerAdapter<T> adapter, View v, int position, boolean hasFocus);

    }

    /**
     * 获取附加焦点改变事件
     *
     * @return
     */
    public void setOnExpansionItemFocusChangeListener(OnExpansionItemFocusChangeListener onExpansionItemFocusChangeListener) {
        this.onExpansionItemFocusChangeListener = onExpansionItemFocusChangeListener;
    }
    /**
     * 设置附加焦点改变事件
     *
     * @param onAttachFocusChangeListener
     */

    /**
     * 加载更多监听
     */
    private OnLoadingListener onLoadingListener;

    /**
     * 加载更多监听
     */
    public interface OnLoadingListener {

        /**
         * 加载更多
         */
        void onLoading();

    }

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

    public void setSwipeThreshold(int swipeThreshold) {
        this.swipeThreshold = swipeThreshold;
    }

    public void setSideRatio(float sideRatio) {
        this.sideRatio = sideRatio;
    }

    public void setSideDuration(int sideDuration) {
        this.sideDuration = sideDuration;
    }
}
