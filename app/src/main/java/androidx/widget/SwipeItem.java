package androidx.widget;


/**
 * 混合Item
 *
 * @param <T>
 */
public class SwipeItem<T> {

    /**
     * item动画
     */
    private SwipeItemAnimator<T> itemAnimator;
    /**
     * menu动画
     */
    private SwipeItemAnimator<T> menuAnimator;
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
    private T data;
    /**
     * 额外Item
     */
    private SwipeExpansion expansion;
    private SwipeItemTouch swipeItemTouch;

    public SwipeItem(SwipeExpansion expansion) {
        this.expansion = expansion;
    }

    public SwipeItem(T data,boolean swipe,SwipeItemAnimator<T> itemAnimator,SwipeItemAnimator<T> menuAnimator) {
        this.itemAnimator = itemAnimator;
        this.menuAnimator = menuAnimator;
        this.data = data;
        this.swipe = swipe;
    }

    public SwipeItem(T data, boolean swipe,boolean open,SwipeItemAnimator<T> itemAnimator,SwipeItemAnimator<T> menuAnimator) {
        this.itemAnimator = itemAnimator;
        this.menuAnimator = menuAnimator;
        this.data = data;
        this.swipe = swipe;
        this.open = open;
    }

    public void setSwipeItemTouch(SwipeItemTouch swipeItemTouch) {
        this.swipeItemTouch = swipeItemTouch;
    }

    public SwipeItemTouch getSwipeItemTouch() {
        return swipeItemTouch;
    }

    public SwipeItemAnimator<T> getItemAnimator() {
        return itemAnimator;
    }

    public void setItemAnimator(SwipeItemAnimator<T> itemAnimator) {
        this.itemAnimator = itemAnimator;
    }

    public SwipeItemAnimator<T> getMenuAnimator() {
        return menuAnimator;
    }

    public void setMenuAnimator(SwipeItemAnimator<T> menuAnimator) {
        this.menuAnimator = menuAnimator;
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

    public T getData() {
        return data;
    }

    public void setData(T ordinary) {
        this.data = data;
    }

    public SwipeExpansion getExpansion() {
        return expansion;
    }

    public void setExpansion(SwipeExpansion expansion) {
        this.expansion = expansion;
    }

}