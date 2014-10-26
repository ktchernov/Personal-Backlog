package nz.co.lazycoder.personalbacklog.view;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import nz.co.lazycoder.personalbacklog.model.DataModelController;

/**
* Created by ktchernov on 26/10/2014.
*/
class BacklogDragSortController extends DragSortController {
    public static final int REMOVE_DELAY_MILLIS = 500;
    public static final int SCROLL_TIMEOUT_MILLIS = 500;
    private DragSortListView dragSortListView;
    private long lastDragScrollTime;
    private int dragPosition = MISS;

    private DataModelController dataModelController;

    private Handler handler = new Handler();
    private Runnable delayedStopAndRemoveRunnable;

    private DragSortListener dragSortListener;

    public interface DragSortListener {
        public void onDragStarted();
        public void onDragFinished();
        public void onScrollWithoutDragging(boolean down);

    }

    public BacklogDragSortController(DragSortListView dragSortListView, DataModelController dataModelController, DragSortListener dragSortListener) {
        super(dragSortListView);
        setBackgroundColor(Color.TRANSPARENT);

        this.dragSortListView = dragSortListView;
        this.dragSortListView.setOnScrollListener(new OnScrollListener());

        this.dataModelController = dataModelController;
        this.dragSortListener = dragSortListener;
    }

    @Override
    public View onCreateFloatView(int dragPosition) {
        this.dragPosition = dragPosition;

        if (dragSortListener != null)
            dragSortListener.onDragStarted();

        return super.onCreateFloatView(dragPosition);
    }

    @Override
    public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
        super.onDragFloatView(floatView, floatPoint, touchPoint);

        boolean needCancel = true;

        if (floatView != null) {
            int floatViewHeightThreshold = floatView.getMeasuredHeight() / 2;

            boolean draggingOutsideUp = floatPoint.y < -floatViewHeightThreshold && dragSortListView.getFirstVisiblePosition() == 0;
            boolean scrollTimedOut = (lastDragScrollTime == 0 || System.currentTimeMillis() - lastDragScrollTime > SCROLL_TIMEOUT_MILLIS);

            if (draggingOutsideUp && scrollTimedOut) {
                needCancel = false;
                if (delayedStopAndRemoveRunnable == null) {
                    delayedStopAndRemoveRunnable = new StopAndRemoveRunnable();
                    handler.postDelayed(delayedStopAndRemoveRunnable, REMOVE_DELAY_MILLIS);
                }
            }
        }

        if (needCancel) {
            stopDelayedCallback();
        }

    }

    @Override
    public void onDestroyFloatView(View floatView) {
        super.onDestroyFloatView(floatView);
        lastDragScrollTime = 0;
        stopDelayedCallback();
        dragPosition = MISS;

        if (dragSortListener != null)
            dragSortListener.onDragFinished();
    }

    private void stopDelayedCallback() {
        handler.removeCallbacks(delayedStopAndRemoveRunnable);
        delayedStopAndRemoveRunnable = null;
    }

    private class StopAndRemoveRunnable implements Runnable {
        private final int dragPosition;

        StopAndRemoveRunnable() {
            this.dragPosition = BacklogDragSortController.this.dragPosition;
        }
        @Override
        public void run() {
            dragSortListView.cancelDrag();
            dataModelController.moveItemFromBacklogToInProgress(this.dragPosition);
            delayedStopAndRemoveRunnable = null;
        }
    }

    private class OnScrollListener implements AbsListView.OnScrollListener {
        private int previousFirstVisible;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (dragPosition == MISS) {
                notifyListenerOfScrollWithoutDrag(firstVisibleItem);
            }
            else if (previousFirstVisible != firstVisibleItem) {
                lastDragScrollTime = System.currentTimeMillis();
                stopDelayedCallback();
            }
            previousFirstVisible = firstVisibleItem;
        }

        private void notifyListenerOfScrollWithoutDrag(int firstVisibleItem) {
            if (dragSortListener != null) {
                if (previousFirstVisible < firstVisibleItem) {
                    dragSortListener.onScrollWithoutDragging(true);
                } else if (previousFirstVisible > firstVisibleItem) {
                    dragSortListener.onScrollWithoutDragging(false);
                }
            }
        }
    }
}
