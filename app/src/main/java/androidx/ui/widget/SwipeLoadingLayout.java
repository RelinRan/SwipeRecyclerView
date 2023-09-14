package androidx.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;


/**
 * 加载更多
 */
public class SwipeLoadingLayout extends LinearLayout implements NestedScrollView.OnScrollChangeListener {

    public final String MORE = "加载更多";
    public final String EMPTY = "无更多数据";
    /**
     * 是否正在加载
     */
    private boolean loading;
    /**
     * 文字
     */
    private TextView textView;
    /**
     * 加载loading
     */
    private LoadingView loadingView;

    /**
     * Loading半径
     */
    private int radius = dip(7);
    /**
     * 单位角度
     */
    private float angle = 20;
    /**
     * 笔锋宽度
     */
    private int lineWidth = dip(1);
    /**
     * 笔锋颜色
     */
    private int lineColor = Color.parseColor("#DADADA");
    /**
     * 笔锋长度
     */
    private float lineLength = dip(2);
    /**
     * Loading右边间距
     */
    private int loadingMarginRight = dip(5);
    /**
     * 文字大小
     */
    private int textSize = 12;
    /**
     * 文字颜色
     */
    private int textColor = Color.parseColor("#DADADA");
    /**
     * 加载更多文字
     */
    private String more;
    /**
     * 无更多数据文字
     */
    private String empty;

    /**
     * 父级滑动控件
     */
    private NestedScrollView nestedScrollView;
    /**
     * 加载更多监听
     */
    private OnLoadingListener onLoadingListener;


    public SwipeLoadingLayout(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public SwipeLoadingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public SwipeLoadingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param attrs   xml参数
     */
    protected void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeLoadingLayout);
            radius = array.getDimensionPixelSize(R.styleable.SwipeLoadingLayout_android_radius, radius);
            angle = array.getFloat(R.styleable.SwipeLoadingLayout_android_angle, angle);
            lineWidth = array.getDimensionPixelSize(R.styleable.SwipeLoadingLayout_lineWidth, lineWidth);
            lineColor = array.getColor(R.styleable.SwipeLoadingLayout_lineColor, lineColor);
            lineLength = array.getDimension(R.styleable.SwipeLoadingLayout_lineLength, lineLength);
            loadingMarginRight = array.getDimensionPixelOffset(R.styleable.SwipeLoadingLayout_loadingMarginRight, loadingMarginRight);
            more = array.getString(R.styleable.SwipeLoadingLayout_more);
            more = more == null ? MORE : more;
            empty = array.getString(R.styleable.SwipeLoadingLayout_empty);
            empty = empty == null ? EMPTY : empty;
            textSize = array.getDimensionPixelSize(R.styleable.SwipeLoadingLayout_android_textSize, textSize);
            textColor = array.getColor(R.styleable.SwipeLoadingLayout_android_textColor, textColor);
            array.recycle();
        }
        onCreateView();
    }

    /**
     * 创建View
     */
    protected void onCreateView() {
        //脚部父级控件
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        LinearLayout parent = new LinearLayout(getContext());
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        //脚部控件
        loadingView = new LoadingView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(radius * 2, radius * 2);
        loadingView.setLayoutParams(params);
        loadingView.setLineWidth(lineWidth);
        loadingView.setAngle(angle);
        loadingView.setLineLength(lineLength);
        loadingView.setBackgroundColor(Color.TRANSPARENT);
        loadingView.setLineColor(lineColor);
        parent.addView(loadingView);
        MarginLayoutParams marginParams = (MarginLayoutParams) loadingView.getLayoutParams();
        marginParams.rightMargin = loadingMarginRight;
        //文字
        textView = new TextView(getContext());
        textView.setTextColor(textColor);
        textView.setText(more);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        parent.addView(textView);
        addView(parent);
    }

    /**
     * dp转px
     *
     * @param value
     * @return
     */
    protected int dip(float value) {
        return (int) (value * getResources().getSystem().getDisplayMetrics().density);
    }


    /**
     * 获取滑动View
     *
     * @return
     */
    public NestedScrollView getNestedScrollView() {
        return nestedScrollView;
    }

    /**
     * 设置滑动View
     *
     * @param view
     */
    public void attachNestedScrollView(NestedScrollView view) {
        view.setOnScrollChangeListener(this);
        this.nestedScrollView = view;
    }

    /**
     * 是在加载
     *
     * @return
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * 设置是否加载
     *
     * @param loading
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
        if (loading) {
            setCircleVisibility(VISIBLE);
            setText(more);
            loadingView.start();
        } else {
            setCircleVisibility(GONE);
            setText(empty);
        }
    }

    /**
     * 设置圆圈是否可见
     *
     * @param visibility
     */
    public void setCircleVisibility(int visibility) {
        loadingView.setVisibility(visibility);
    }

    /**
     * 设置半径
     *
     * @param radius
     */
    public void setRadius(int radius) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) loadingView.getLayoutParams();
        params.width = radius * 2;
        params.height = radius * 2;
    }

    /**
     * 设置单位角度
     *
     * @param angle 角度
     */
    public void setAngle(float angle) {
        loadingView.setAngle(angle);
    }

    /**
     * 设置笔锋宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(int lineWidth) {
        loadingView.setLineWidth(lineWidth);
    }

    /**
     * 设置笔锋颜色
     *
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        loadingView.setLineColor(lineColor);
    }

    /**
     * 设置笔锋长度
     *
     * @param lineLength
     */
    public void setLineLength(float lineLength) {
        loadingView.setLineLength(lineLength);
    }

    /**
     * 设置Loading右边间距
     *
     * @param loadingMarginRight
     */
    public void setLoadingMarginRight(int loadingMarginRight) {
        MarginLayoutParams marginParams = (MarginLayoutParams) loadingView.getLayoutParams();
        marginParams.rightMargin = loadingMarginRight;
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置文字颜色
     *
     * @param textColor
     */
    public void setTextColor(@ColorInt int textColor) {
        textView.setTextColor(textColor);
    }

    /**
     * 设置加载更多显示的文字
     *
     * @param more
     */
    public void setMore(String more) {
        this.more = more;
    }

    /**
     * 设置无更多数据的文字
     *
     * @param empty
     */
    public void setEmpty(String empty) {
        this.empty = empty;
    }

    /**
     * 设置加载文字
     *
     * @param text
     */
    public void setText(String text) {
        textView.setText(text);
    }


    /**
     * 设置加载监听
     *
     * @param onLoadingListener
     */
    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }
    
    public interface OnLoadingListener {

        /**
         * 加载更多
         */
        void onLoading();

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
        boolean isScrolledTop = v.canScrollVertically(1);
        boolean isScrolledBottom = v.canScrollVertically(-1);
        boolean isNotMore = !isScrolledTop && !isScrolledBottom;
        //数据未填满情况
        if (isNotMore) {
            setLoading(false);
        }
        //列表中间位置
        if (isScrolledTop && isScrolledBottom) {
            setLoading(true);
        }
        //滑动到底部情况
        if (isScrolledBottom && !isScrolledTop) {
            setLoading(true);
            if (onLoadingListener != null) {
                onLoadingListener.onLoading();
            }
        }
    }

}