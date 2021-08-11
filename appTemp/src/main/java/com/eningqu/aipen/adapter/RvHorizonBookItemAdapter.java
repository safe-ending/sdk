package com.eningqu.aipen.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.utils.ImageUtil;
import com.eningqu.aipen.common.utils.NumberUtil;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.PageData;

import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/11 11:18
 * desc   :
 * version: 1.0
 */
public class RvHorizonBookItemAdapter extends RecyclerView.Adapter<RvHorizonBookItemAdapter.ViewHolder> implements View.OnClickListener {
    private Context context;
    private List<NoteBookData> datas;
    private OnItemClickListener onItemClickListener;

    public RvHorizonBookItemAdapter(Context context, List<NoteBookData> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_horizon_book_layout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int newPos = position % datas.size();
        String url = datas.get(newPos).noteCover;
        if (position == 0) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = (int) ((ScreenUtils.getScreenWidth() - 178 * ScreenUtils.getScreenDensity()) / 2);
            holder.itemView.setLayoutParams(layoutParams);
        } else {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 0;
            holder.itemView.setLayoutParams(layoutParams);
        }
        if (!TextUtils.isEmpty(url) && NumberUtil.isNumeric(url)) {
            //            if (Integer.valueOf(url) < Constant.BOOK_COVERS.length) {
            holder.img.setImageResource(Constant.BOOK_COVERS[Integer.valueOf(url) % Constant.BOOK_COVERS.length]);
            //            } else {
            //                holder.img.setImageResource(Constant.BOOK_COVERS[Constant.BOOK_COVERS.length - 1]);
            //            }
        } else if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
            ImageUtil.load(context, url, holder.img);
        } else {
            holder.img.setImageResource(Constant.BOOK_COVERS[0]);
        }
        if (datas.get(newPos).isLock) {
            holder.ivSelState.setImageResource(R.drawable.selected);
        } else {
            holder.ivSelState.setImageResource(R.drawable.selected_none);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        //        return Integer.MAX_VALUE;//无限循环
        return datas.size();
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            int position = (Integer) view.getTag();
            if (position >= datas.size()) {
                position = datas.size() - 1;
            }
            NoteBookData noteBookData = datas.get(position);

            List<PageData> pageDatas = AppCommon.loadPageDataList(noteBookData.notebookId, false);
            if (null == pageDatas || pageDatas.size() == 0) {
                ToastUtils.showShort(R.string.notebook_empty);
            }
            noteBookData.isLock = !datas.get(position).isLock;
            onItemClickListener.onItemClick(view, position, datas.get(position).isLock);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int tag, boolean lock);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView ivSelState;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            ivSelState = itemView.findViewById(R.id.iv_select_state);
        }
    }
}
