package androidx.widget;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;


public class SwipeRecyclerScrollListener extends RecyclerView.OnScrollListener implements NestedScrollView.OnScrollChangeListener {

    private OnScrollListener onScrollListener;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (onScrollListener!=null){
            onScrollListener.onScrolled(recyclerView,dx,dy);
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (onScrollListener!=null){
            onScrollListener.onScrollStateChanged(recyclerView,newState);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (onScrollListener!=null){
            onScrollListener.onScrollChange(v,scrollX,scrollY,oldScrollX,oldScrollY);
        }
    }

    public interface OnScrollListener{

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

}
