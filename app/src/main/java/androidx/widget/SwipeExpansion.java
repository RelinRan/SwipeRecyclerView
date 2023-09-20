package androidx.widget;

public class SwipeExpansion {

    /**
     * Item视图 - 头部
     */
    public static final int HEADER = -1;
    /**
     * Item视图 - 脚部
     */
    public static final int FOOTER = -2;
    /**
     * Item视图 - 加载更多
     */
    public static final int LOADING = -3;

    /**
     * 视图类型 {@link #HEADER} or {@link #FOOTER} or {@link #LOADING}
     */
    private int viewType;

    public SwipeExpansion(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }
}