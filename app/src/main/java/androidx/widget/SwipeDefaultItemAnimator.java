package androidx.widget;

import android.os.Handler;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeDefaultItemAnimator<T> extends DefaultItemAnimator implements Runnable {

    private long delay = 300;
    private Handler handler;
    private SwipeRecyclerAdapter<T> adapter;

    public SwipeDefaultItemAnimator(SwipeRecyclerAdapter<T> adapter) {
        super();
        this.adapter = adapter;
        handler = new Handler();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void onRemoveFinished(RecyclerView.ViewHolder item) {
        super.onRemoveFinished(item);
        handler.postDelayed(this, delay);
    }

    @Override
    public void onAddFinished(RecyclerView.ViewHolder item) {
        super.onAddFinished(item);
        handler.postDelayed(this, delay);
    }

    @Override
    public void onMoveFinished(RecyclerView.ViewHolder item) {
        super.onMoveFinished(item);
        handler.postDelayed(this, delay);
    }

    @Override
    public void run() {
        adapter.notifySwipeItemChanged();
    }

    public void release(){
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

}
