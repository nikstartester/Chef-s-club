package com.xando.chefsclub.shoppinglist.List;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timehop.stickyheadersrecyclerview.HeaderPositionCalculator;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider;
import com.timehop.stickyheadersrecyclerview.caching.HeaderViewCache;
import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator;
import com.timehop.stickyheadersrecyclerview.rendering.HeaderRenderer;
import com.timehop.stickyheadersrecyclerview.util.LinearLayoutOrientationProvider;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

import org.jetbrains.annotations.NotNull;

public class StickyHeadersDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = "StickyHeadersTouchListe";

    private final StickyRecyclerHeadersAdapter mAdapter;
    private final SparseArray<Rect> mHeaderRects = new SparseArray<>();
    private final HeaderProvider mHeaderProvider;
    private final OrientationProvider mOrientationProvider;
    private final HeaderPositionCalculator mHeaderPositionCalculator;
    private final HeaderRenderer mRenderer;
    private final DimensionCalculator mDimensionCalculator;
    private LinearLayoutManager mLinearLayoutManager;

    /**
     * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private final Rect mTempRect = new Rect();

    // TODO: Consider passing in orientation to simplify orientation accounting within calculation
    public StickyHeadersDecoration(StickyRecyclerHeadersAdapter adapter) {
        this(adapter, new LinearLayoutOrientationProvider(), new DimensionCalculator());
    }

    private StickyHeadersDecoration(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider,
                                    DimensionCalculator dimensionCalculator) {
        this(adapter, orientationProvider, dimensionCalculator, new HeaderRenderer(orientationProvider),
                new HeaderViewCache(adapter, orientationProvider));
    }

    private StickyHeadersDecoration(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider,
                                    DimensionCalculator dimensionCalculator, HeaderRenderer headerRenderer, HeaderProvider headerProvider) {
        this(adapter, headerRenderer, orientationProvider, dimensionCalculator, headerProvider,
                new HeaderPositionCalculator(adapter, headerProvider, orientationProvider,
                        dimensionCalculator));
    }

    private StickyHeadersDecoration(StickyRecyclerHeadersAdapter adapter, HeaderRenderer headerRenderer,
                                    OrientationProvider orientationProvider, DimensionCalculator dimensionCalculator, HeaderProvider headerProvider,
                                    HeaderPositionCalculator headerPositionCalculator) {
        mAdapter = adapter;
        mHeaderProvider = headerProvider;
        mOrientationProvider = orientationProvider;
        mRenderer = headerRenderer;
        mDimensionCalculator = dimensionCalculator;
        mHeaderPositionCalculator = headerPositionCalculator;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mLinearLayoutManager == null)
            mLinearLayoutManager = (LinearLayoutManager) parent.getLayoutManager();

        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        if (mHeaderPositionCalculator.hasNewHeader(itemPosition, mOrientationProvider.isReverseLayout(parent))) {
            View header = getHeaderView(parent, itemPosition);
            setItemOffsetsForHeader(outRect, header, mOrientationProvider.getOrientation(parent));
        }
    }

    /**
     * Sets the offsets for the first item in a section to make room for the header view
     *
     * @param itemOffsets rectangle to define offsets for the item
     * @param header      view used to calculate offset for the item
     * @param orientation used to calculate offset for the item
     */
    private void setItemOffsetsForHeader(Rect itemOffsets, View header, int orientation) {
        mDimensionCalculator.initMargins(mTempRect, header);
        if (orientation == LinearLayoutManager.VERTICAL) {
            itemOffsets.top = header.getHeight() + mTempRect.top + mTempRect.bottom;
        } else {
            itemOffsets.left = header.getWidth() + mTempRect.left + mTempRect.right;
        }
    }

    @Override
    public void onDrawOver(@NotNull Canvas canvas, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        final int childCount = parent.getChildCount();
        if (childCount <= 0 || mAdapter.getItemCount() <= 0) {
            return;
        }

        mHeaderRects.clear();

        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);
            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            boolean hasStickyHeader = mHeaderPositionCalculator.hasStickyHeader(itemView, mOrientationProvider.getOrientation(parent), position);
            if (hasStickyHeader || mHeaderPositionCalculator.hasNewHeader(position, mOrientationProvider.isReverseLayout(parent))) {
                View header = mHeaderProvider.getHeader(parent, position);

                //re-use existing Rect, if any.
                /*Rect headerOffset = mHeaderRects.get(position);
                if (headerOffset == null) {
                    headerOffset = new Rect();

                    mHeaderRects.put(position, headerOffset);
                }*/

                Rect headerOffset = new Rect();
                mHeaderRects.put(position, headerOffset);

                mHeaderPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader);
                mRenderer.drawHeader(parent, canvas, header, headerOffset);
            }
        }
    }

    /**
     * Gets the position of the header under the specified (x, y) coordinates.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return position of header, or -1 if not found
     */
    int findHeaderPositionUnder(int x, int y) {
        /*for (int i = 0; i < mHeaderRects.size(); i++) {
            Rect rect = mHeaderRects.get(mHeaderRects.keyAt(i));
            if (rect.contains(x, y)) {
                return mHeaderRects.keyAt(i);
            }
        }
        return -1;*/

        int tempPosition = -1;
        for (int i = 0; i < mHeaderRects.size(); i++) {
            Rect rect = mHeaderRects.get(mHeaderRects.keyAt(i));
            if (rect.contains(x, y)) {
                int position = mHeaderRects.keyAt(i);
                if (mAdapter == null || isPositionVisible(position)) {
                    tempPosition = position;
                } else {
                    if (tempPosition != -1) {
                        break;
                    }
                }
            }
        }
        return tempPosition;
    }

    Rect getHeaderRect(int position) {
        return mHeaderRects.get(position);
    }

    private boolean isPositionVisible(final int position) {
        return mLinearLayoutManager.findFirstVisibleItemPosition() <= position
                && mLinearLayoutManager.findLastVisibleItemPosition() >= position;
    }

    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     */
    View getHeaderView(RecyclerView parent, int position) {
        return mHeaderProvider.getHeader(parent, position);
    }

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    public void invalidateHeaders() {
        mHeaderProvider.invalidate();
        mHeaderRects.clear();
    }
}
