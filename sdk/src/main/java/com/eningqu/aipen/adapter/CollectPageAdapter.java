package com.eningqu.aipen.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.db.model.PageData;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/6 15:54
 */

public class CollectPageAdapter extends PagerAdapter implements View.OnClickListener{

    private final static String TAG = PageViewPagerAdapter.class.getSimpleName();

    private Context mContext;
    private List<PageData> pageDataList = Collections.emptyList();

    private int scaleWidth = 0;
    private int scaleHeight = 0;

    public CollectPageAdapter(Context mContext, List<PageData> pageDataList) {
        this.mContext = mContext;
        this.pageDataList = pageDataList;
        this.scaleWidth = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
        this.scaleHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 5.0f / 8.0f);
    }

    @Override
    public int getCount() {
        return pageDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        PageData pageData = pageDataList.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewpager_page_item, null);
        ImageView mView = (ImageView) view.findViewById(R.id.im_view);
        File file = new File(pageData.picUrl);
        if(pageData != null&& file.exists()){
            Picasso.with(mContext)
                    .load(file)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(mView);
            mView.setOnClickListener(this);
            mView.setTag(pageData.id);
        }
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
    private long time;
    @Override
    public void onClick(View view) {
        if ((System.currentTimeMillis() - time)<1000){
            time = System.currentTimeMillis();
            return;
        }
        String pageId = (String) view.getTag();
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.PAGE_NUM_ID, pageId);
        message.what = Constant.PAGE_LABEL_CLICK_CODE;
        message.setData(bundle);
        EventBusUtil.post(message);
    }
}
