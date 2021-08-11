package com.eningqu.aipen.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.activity.PageShowActivity;

import java.util.Collections;
import java.util.List;


/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/2 11:59
 */

public class ViewPagerAdapter extends PagerAdapter implements View.OnLongClickListener, View.OnClickListener{

    private Context mContext;
    private List<NoteBookData> noteBookDatas = Collections.emptyList();
    private SparseArray<View> mViewSparseArray;
    //提示是否是笔记本添加的封面
    private boolean isNoteAdd = false;

    public ViewPagerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatas(List<NoteBookData> noteBookDatas, boolean isNoteAdd){
        this.noteBookDatas.clear();
        this.noteBookDatas = noteBookDatas;
        this.isNoteAdd = isNoteAdd;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return noteBookDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        NoteBookData noteBookData = noteBookDatas.get(position);
        ImageView imageView = (ImageView) View.inflate(mContext, R.layout.viewpager_item, null);
        if(!isNoteAdd){
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.notebook_2));
            imageView.setOnLongClickListener(this);
            imageView.setOnClickListener(this);
            imageView.setTag(R.string.note_name, noteBookData.noteName);
            imageView.setTag(R.string.note_type, noteBookData.noteType);
        }else{
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.note_book_add_cover));
            imageView.setOnClickListener(this);
        }
        //第2个参数一定得是0
        ((ViewPager)container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((ImageView)object);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean onLongClick(View v) {
        Message msg = new Message();
        msg.what = Constant.NOTE_LONG_CLICK_CODE;
        msg.arg1 = (int) v.getTag(R.string.note_type);
        EventBusUtil.post(msg);
        return true;
    }
    private long time;
    @Override
    public void onClick(View v) {
        if ((System.currentTimeMillis() - time)<1000){
            time = System.currentTimeMillis();
            return;
        }
        if(!isNoteAdd){
            Intent intent = new Intent(mContext, PageShowActivity.class);
            intent.putExtra("noteName", (String) v.getTag(R.string.note_name));
            intent.putExtra("noteType", (int) v.getTag(R.string.note_type));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }else{
            Message message = new Message();
            message.what = Constant.START_REAL_WRITE_CODE;
            EventBusUtil.post(message);
        }

    }
}
