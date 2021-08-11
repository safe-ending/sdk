package com.eningqu.aipen.adapter;

import android.content.Context;
import android.os.Message;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
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
 * 时间：2018/3/3 11:45
 */

public class PageViewPagerAdapter extends PagerAdapter implements View.OnClickListener, View.OnLongClickListener{

    private final static String TAG = PageViewPagerAdapter.class.getSimpleName();

    private Context mContext;
    private List<PageData> pageDataList = Collections.emptyList();
    protected int changePosition = -1;

    private int scaleWidth = 0;
    private int scaleHeight = 0;
    private ImageView mView;

    public PageViewPagerAdapter(Context mContext, List<PageData> pageDataList) {
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
        mView = (ImageView) view.findViewById(R.id.im_view);
        if(!StringUtils.isEmpty(pageData.picUrl)){
            Picasso.with(mContext)
                    .load(new File(pageData.picUrl))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(mView);
        }

        mView.setTag(position);
        mView.setTag(R.string.page_num, pageData.pageNum);
        mView.setTag(R.string.note_type, pageData.noteType);
        mView.setTag(R.string.page_id, pageData.id);

        mView.setOnLongClickListener(this);
        mView.setOnClickListener(this);

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        ImageView im = ((ImageView)container.getChildAt(position));
//        if(object == null||im==null){
//            return;
//        }
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) im.getDrawable();
//        if (bitmapDrawable != null){
//            Bitmap bm = bitmapDrawable.getBitmap();
//            if (bm!=null && !bm.isRecycled()) {
//                L.debug("...desimg..", "被回收了" + bm.getByteCount());
//                ((ImageView)container.getChildAt(position)).setImageResource(0);
//                bm.recycle();
//                bm= null;
//            }
//        }
//        im.destroyDrawingCache();
//        im = null;
//        bitmapDrawable = null;
        container.removeView((View) object);

    }
    @Override
    public int getItemPosition(Object object) {
        /*ImageView view = (ImageView) object;
        int position = (int) view.getTag();
        if(position >= changePosition){
            return POSITION_NONE;
        }else{
            return POSITION_UNCHANGED;
        }*/
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        getCount();
        super.notifyDataSetChanged();
    }
    private long time;
    @Override
    public void onClick(View v) {
        if ((System.currentTimeMillis() - time)<1000){
            time = System.currentTimeMillis();
            return;
        }
        AppCommon.isClickPageDraw = false;
        Message message = new Message();
        message.arg1 = (int) v.getTag(R.string.note_type);
        message.arg2 = (int) v.getTag(R.string.page_num);
        message.what = Constant.PAGE_CLICK_CODE;
        EventBusUtil.post(message);
    }


    @Override
    public boolean onLongClick(View v) {
        Message message = new Message();
        message.obj = (Long) v.getTag(R.string.page_id);
        message.arg1 = (int) v.getTag(R.string.note_type);
        message.arg2 = (int) v.getTag(R.string.page_num);
        message.what = Constant.PAGE_LONG_DELETE_CODE;
        EventBusUtil.post(message);
        return true;
    }

    public interface PageItemsListener{
        void onItemClick(View v);
        void onItemLongClick(View v);
    }
}
