package nz.co.lazycoder.personalbacklog.view;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/**
* Created by ktchernov on 26/10/2014.
*/
class OutsideBoundsDragSortController extends DragSortController {
    public static final int REMOVE_DELAY_MILLIS = 500;
    public static final int SCROLL_TIMEOUT_MILLIS = 500;
    private DragSortListView dragSortListView;
    private long lastDragScrollTime;
    private int dragPosition = MISS;

    private Handler handler = new Handler();
    private Runnable delayedDraggedOutsideBoundsTask;

    private DragSortControllerListener dragSortControllerListener;

    public interface DragSortControllerListener {
        public void onDragStarted();
        public void onDragFinished();
        public void onScrollWithoutDragging(boolean down);
        public void onDraggedOutsideBounds(int position, boolean down);

    }

    public OutsideBoundsDragSortController(DragSortListView dragSortListView, DragSortControllerListener dragSortControllerListener) {
        super(dragSortListView);
        setBackgroundColor(Color.TRANSPARENT);

        this.dragSortListView = dragSortListView;
        this.dragSortListView.setOnScrollListener(new OnScrollListener());

        this.dragSortControllerListener = dragSortControllerListener;
    }

    @Override
    public View onCreateFloatView(int dragPosition) {
        this.dragPosition = dragPosition;

        if (dragSortControllerListener != null)
            dragSortControllerListener.onDragStarted();

        return super.onCreateFloatView(dragPosition);
    }

    @Override
    public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
        super.onDragFloatView(floatView, floatPoint, touchPoint);

        boolean needCancel = true;

        if (floatView != null) {
            int floatViewHeightThreshold = floatView.getMeasuredHeight() / 2;
            int listBottom = dragSortListView.getMeasuredHeight() - dragSortListView.getPaddingBottom();

            boolean showingFirstItem = dragSortListView.getFirstVisiblePosition() == 0;
            boolean showingLastItem = dragSortListView.getLastVisiblePosition() == dragSortListView.getAdapter().getCount() - 1;

            boolean draggingOutsideUp = floatPoint.y <  dragSortListView.getPaddingTop() - floatViewHeightThreshold && showingFirstItem;
            boolean draggingOutsideDown = floatPoint.y > listBottom - floatViewHeightThreshold && showingLastItem;

            boolean scrollTimedOut = (lastDragScrollTime == 0 || System.currentTimeMillis() - lastDragScrollTime > SCROLL_TIMEOUT_MILLIS);

            if ((draggingOutsideUp || draggingOutsideDown) && scrollTimedOut) {
                needCancel = false;
                if (delayedDraggedOutsideBoundsTask == null) {
                    delayedDraggedOutsideBoundsTask = new DelayedDraggedOutsideBounds(draggingOutsideDown);
                    handler.postDelayed(delayedDraggedOutsideBoundsTask, REMOVE_DELAY_MILLIS);
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

        if (dragSortControllerListener != null)
            dragSortControllerListener.onDragFinished();
    }

    private void stopDelayedCallback() {
        handler.removeCallbacks(delayedDraggedOutsideBoundsTask);
        delayedDraggedOutsideBoundsTask = null;
    }

    private class DelayedDraggedOutsideBounds implements Runnable {
        private final int dragPosition;
        private final boolean draggingDown;

        DelayedDraggedOutsideBounds(boolean draggingDown) {
            this.dragPosition = OutsideBoundsDragSortController.this.dragPosition;
            this.draggingDown = draggingDown;
        }
        @Override
        public void run() {
            dragSortListView.cancelDrag();

            if (dragSortControllerListener != null) {
                dragSortControllerListener.onDraggedOutsideBounds(dragPosition, draggingDown);
            }
            delayedDraggedOutsideBoundsTask = null;
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
            if (dragSortControllerListener != null) {
                if (previousFirstVisible < firstVisibleItem) {
                    dragSortControllerListener.onScrollWithoutDragging(true);
                } else if (previousFirstVisible > firstVisibleItem) {
                    dragSortControllerListener.onScrollWithoutDragging(false);
                }
            }
        }
    }
}
