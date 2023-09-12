package androidx.ui.widget;


/**
 * 混合Item
 *
 * @param <T>
 */
public class SwipeItem<T> {

    /**
     * 是否支持侧滑
     */
    private boolean swipe;
    /**
     * 是否打开侧滑
     */
    private boolean open;
    /**
     * 普通item
     */
    private T ordinary;
    /**
     * 额外Item
     */
    private SwipeExpansion expansion;

    public SwipeItem(SwipeExpansion expansion) {
        this.expansion = expansion;
    }

    public SwipeItem(T ordinary, boolean swipe) {
        this.ordinary = ordinary;
        this.swipe = swipe;
    }

    public boolean isSwipe() {
        return swipe;
    }

    public void setSwipe(boolean swipe) {
        this.swipe = swipe;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public T getOrdinary() {
        return ordinary;
    }

    public void setOrdinary(T ordinary) {
        this.ordinary = ordinary;
    }

    public SwipeExpansion getExpansion() {
        return expansion;
    }

    public void setExpansion(SwipeExpansion expansion) {
        this.expansion = expansion;
    }

}