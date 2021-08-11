package com.eningqu.aipen.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eningqu.aipen.R;

import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/11 20:55
 * desc   :
 * version: 1.0
 */
public class BookItemDotAdapter extends PagerAdapter {
    private Context mContext;
    private List<Integer> mData;

    public BookItemDotAdapter(Context context ,List<Integer> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.item_book_dot,null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_dot);
        iv.setImageResource(mData.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container,position,object); 这一句要删除，否则报错
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
