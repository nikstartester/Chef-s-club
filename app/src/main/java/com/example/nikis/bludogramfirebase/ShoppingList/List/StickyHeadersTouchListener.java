package com.example.nikis.bludogramfirebase.ShoppingList.List;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.List;

public class StickyHeadersTouchListener implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "StickyHeadersTouchListe";
    private final GestureDetector mTapDetector;
    private final RecyclerView mRecyclerView;
    private final StickyHeadersDecoration mDecor;
    private StickyHeadersTouchListener.OnHeaderClickListener mOnHeaderClickListener;
    private EventHook mEventHook;

    public interface OnHeaderClickListener {
        void onHeaderClick(View header, int position, long headerId);
    }

    public interface EventHook {
        @NonNull
        List<View> onBindViews(@NonNull View header);

        void onClick(View header, View view, int position, long headerId);
    }

    public StickyHeadersTouchListener(final RecyclerView recyclerView,
                                      final StickyHeadersDecoration decor) {
        mTapDetector = new GestureDetector(recyclerView.getContext(), new StickyHeadersTouchListener.SingleTapDetector());
        mRecyclerView = recyclerView;
        mDecor = decor;
    }

    public StickyRecyclerHeadersAdapter getAdapter() {
        if (mRecyclerView.getAdapter() instanceof StickyRecyclerHeadersAdapter) {
            return (StickyRecyclerHeadersAdapter) mRecyclerView.getAdapter();
        } else {
            throw new IllegalStateException("A RecyclerView with " +
                    StickyRecyclerHeadersTouchListener.class.getSimpleName() +
                    " requires a " + StickyRecyclerHeadersAdapter.class.getSimpleName());
        }
    }


    public StickyHeadersTouchListener setOnHeaderClickListener(StickyHeadersTouchListener.OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;

        return this;
    }

    public StickyHeadersTouchListener setEventHook(EventHook eventHook) {
        mEventHook = eventHook;

        return this;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        if (this.mOnHeaderClickListener != null || mEventHook != null) {
            return clickAction(e);
        }
        return false;
    }

    private boolean clickAction(MotionEvent e) {
        boolean tapDetectorResponse = this.mTapDetector.onTouchEvent(e);
        if (tapDetectorResponse) {
            // Don't return false if a single tap is detected
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int position = mDecor.findHeaderPositionUnder((int) e.getX(), (int) e.getY());
            return position != -1;
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent e) { /* do nothing? */ }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }

    private class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
        private int mPosition;
        private long mHeaderId;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mPosition = mDecor.findHeaderPositionUnder((int) e.getX(), (int) e.getY());

            if (mPosition != -1) {
                View headerView = mDecor.getHeaderView(mRecyclerView, mPosition);

                mHeaderId = getAdapter().getHeaderId(mPosition);

                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onHeaderClick(headerView, mPosition, mHeaderId);
                }

                detectViewsClick(e, headerView);

                headerView.onTouchEvent(e);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        protected void detectViewsClick(MotionEvent e, View headerView) {
            if (mEventHook == null || mPosition == -1) return;

            Rect headerRect = mDecor.getHeaderRect(mPosition);

            for (View view : mEventHook.onBindViews(headerView)) {
                Rect viewRect = new Rect(view.getLeft(), headerRect.top + view.getTop(),
                        view.getRight(), headerRect.top + view.getBottom());

                if (viewRect.contains((int) e.getX(), (int) e.getY()) && headerRect.contains(viewRect)) {
                    view.onTouchEvent(e);
                    mEventHook.onClick(headerView, view, mPosition, mHeaderId);
                }
            }
        }
    }
}
