package com.eningqu.aipen.common.utils;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：15:11
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    public SpacesItemDecoration(int space) {
        this.space = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        // Add top margin only for the first item to avoid double space between items
//        if(itemPosition == 0)
//            outRect.top = space;
    }
}

