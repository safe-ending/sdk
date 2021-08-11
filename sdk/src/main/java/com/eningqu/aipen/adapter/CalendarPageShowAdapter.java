package com.eningqu.aipen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.db.model.PageData;

import java.util.List;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/14 9:53
 */

public class CalendarPageShowAdapter extends BaseAdapter  {

    private Context mContext;

    private List<PageData> datas;

    public CalendarPageShowAdapter(Context mContext, List<PageData> dataList) {
        this.mContext = mContext;
        this.datas = dataList;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final PageData calendarPage = datas.get(i);
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_calendar_page_show, viewGroup, false);
            holder = new ViewHolder();
            holder.pageNum = view.findViewById(R.id.tv_page_num);
            // 通过setTag将ViewHolder和View绑定
            view.setTag(holder);
        } else {
            // 获取，通过ViewHolder找到相应的控件
            holder = (ViewHolder) view.getTag();
        }
        holder.pageNum.setText(mContext.getString(R.string.label_text) + calendarPage.pageNum + "");

        view.setTag(R.string.page_id, calendarPage.noteBookId);

        return view;
    }

    static class ViewHolder {
        TextView noteName;
        TextView pageNum;
    }

    public static class CalendarPage {
        private String id;
        private String noteName;
        private int PageNum;
        private String createTime;
        private boolean isLock;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNoteName() {
            return noteName;
        }

        public void setNoteName(String noteName) {
            this.noteName = noteName;
        }

        public int getPageNum() {
            return PageNum;
        }

        public void setPageNum(int pageNum) {
            PageNum = pageNum;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isLock() {
            return isLock;
        }

        public void setLock(boolean lock) {
            isLock = lock;
        }
    }
}
