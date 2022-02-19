package androidx.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.androidx.widget.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 占位View
 */
public class Placeholder extends FrameLayout {

    protected int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    protected int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    /**
     * 占位 - 空数据
     */
    int PLACEHOLDER_EMPTY = 110;
    /**
     * 占位 - 错误
     */
    int PLACEHOLDER_ERROR = 111;
    /**
     * 占位 - 网络
     */
    int PLACEHOLDER_NET = 112;
    /**
     * 占位 - 消息
     */
    int PLACEHOLDER_MSG = 113;


    private float textSize = 14 * Resources.getSystem().getDisplayMetrics().density;
    private String text;
    private int src = R.mipmap.androidx_placeholder_empty;
    private int textColor = Color.parseColor("#ACABAB");
    private int textMarginTop = (int) (Resources.getSystem().getDisplayMetrics().density * 10);
    private int textMarginBottom = 0;
    private int textMarginVertical = -1;

    private View placeholderView;
    private ImageView imageView;
    private TextView textView;
    private Map<Integer, String> textMap;
    private Map<Integer, Integer> drawableMap;

    public Placeholder(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public Placeholder(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public Placeholder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        initPlaceholderMap();
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Placeholder);
            text = array.getString(R.styleable.Placeholder_android_text);
            text = text == null ? "没有找到您要的信息" : text;
            textSize = array.getDimension(R.styleable.Placeholder_android_textSize, textSize);
            textColor = array.getColor(R.styleable.Placeholder_android_textColor, textColor);
            src = array.getResourceId(R.styleable.Placeholder_android_src, src);
            textMarginTop = array.getDimensionPixelSize(R.styleable.Placeholder_textMarginTop, textMarginTop);
            textMarginBottom = array.getDimensionPixelSize(R.styleable.Placeholder_textMarginBottom, textMarginBottom);
            textMarginVertical = array.getDimensionPixelSize(R.styleable.Placeholder_textMarginVertical, textMarginVertical);
            array.recycle();
        }
        onCreateView(context);
    }

    /**
     * 获取占位参数
     *
     * @return
     */
    protected LayoutParams obtainPlaceholderParams() {
        LayoutParams placeholderParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        placeholderParams.gravity = Gravity.CENTER;
        return placeholderParams;
    }

    /**
     * 创建View
     *
     * @param context 上下文对象
     */
    protected void onCreateView(Context context) {
        LinearLayout placeholderView = new LinearLayout(context);
        placeholderView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        placeholderView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        imageParams.gravity = Gravity.CENTER;
        //图片
        imageView = new ImageView(context);
        imageView.setImageResource(src);
        placeholderView.addView(imageView, imageParams);
        //文字
        textView = new TextView(context);
        textView.setTextColor(textColor);
        textView.setTextSize(textSize / getResources().getDisplayMetrics().density);
        textView.setText(text);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER;
        textParams.topMargin = textMarginVertical == -1 ? textMarginTop : textMarginVertical;
        textParams.bottomMargin = textMarginVertical == -1 ? textMarginBottom : textMarginVertical;
        placeholderView.addView(textView, textParams);
        addView(placeholderView, obtainPlaceholderParams());
        onViewCreated(this, placeholderView);
    }

    /**
     * 创建View完成
     *
     * @param parent      父级，即当前
     * @param contentView 占位视图
     */
    protected void onViewCreated(ViewGroup parent, View contentView) {
        placeholderView = contentView;
    }

    /**
     * 设置顶部间距
     *
     * @param topMargin 顶部间距
     */
    public void setTextMarginTop(int topMargin) {
        MarginLayoutParams params = (MarginLayoutParams) textView.getLayoutParams();
        params.topMargin = topMargin;
    }

    /**
     * 设置底部间距
     *
     * @param bottomMargin 底部间距
     */
    public void setTextMarginBottom(int bottomMargin) {
        MarginLayoutParams params = (MarginLayoutParams) textView.getLayoutParams();
        params.bottomMargin = bottomMargin;
    }

    /**
     * 获取图片View
     *
     * @return
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 获取文字View
     *
     * @return
     */
    public TextView getTextView() {
        return textView;
    }

    /**
     * 设置图片资源
     *
     * @param resId
     */
    public void setImageResource(@DrawableRes int resId) {
        imageView.setImageResource(resId);
    }

    /**
     * 设置文字
     *
     * @param text
     */
    public void setText(String text) {
        textView.setText(text);
    }

    /**
     * 设置文字大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setTextColor(@ColorInt int color) {
        textView.setTextColor(color);
    }

    /**
     * 初始化占位数据Map
     */
    protected void initPlaceholderMap() {
        drawableMap = new HashMap<>();
        textMap = new HashMap<>();
        setPlaceholder(PLACEHOLDER_EMPTY, R.mipmap.androidx_placeholder_empty, "没有找到您要的信息");
        setPlaceholder(PLACEHOLDER_ERROR, R.mipmap.androidx_placeholder_error, "数据加载失败！请刷新重试");
        setPlaceholder(PLACEHOLDER_NET, R.mipmap.androidx_placeholder_net, "暂无网络连接");
        setPlaceholder(PLACEHOLDER_MSG, R.mipmap.androidx_placeholder_msg, "暂无消息");
    }

    /**
     * 删除内容View
     */
    public void removePlaceholderView() {
        if (placeholderView.getParent() != null) {
            removeView(placeholderView);
        }
    }

    /**
     * 设置占位View
     *
     * @param placeholderView 占位视图
     */
    public void setPlaceholderView(View placeholderView) {
        removePlaceholderView();
        this.placeholderView = placeholderView;
        addView(placeholderView, obtainPlaceholderParams());
    }

    /**
     * 设置占位视图
     *
     * @param layoutResId 资源布局ID
     */
    public void setPlaceholderView(@LayoutRes int layoutResId) {
        removePlaceholderView();
        placeholderView = LayoutInflater.from(getContext()).inflate(layoutResId, this, false);
        addView(placeholderView, obtainPlaceholderParams());
    }

    /**
     * 设置占位参数
     *
     * @param type  类型{@link #PLACEHOLDER_ERROR}等
     * @param resId 图片资源
     * @param text  文字
     */
    public void setPlaceholder(int type, int resId, String text) {
        drawableMap.put(type, resId);
        textMap.put(type, text);
    }

    /**
     * 设置占位参数
     *
     * @param type  类型{@link #PLACEHOLDER_ERROR}等
     * @param resId 图片资源
     */
    public void setPlaceholder(int type, int resId) {
        drawableMap.put(type, resId);
    }

    /**
     * 设置占位参数
     *
     * @param type 类型{@link #PLACEHOLDER_ERROR}等
     * @param text 文字
     */
    public void setPlaceholder(int type, String text) {
        textMap.put(type, text);
    }


}
