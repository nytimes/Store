package com.nytimes.android.sample.activity;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class EmptyItemDecoration extends RecyclerView.ItemDecoration {

    private final int size;

    EmptyItemDecoration(Context context, @DimenRes int dimenRes) {
        size = context.getResources().getDimensionPixelSize(dimenRes);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = size;
        }
    }
}
