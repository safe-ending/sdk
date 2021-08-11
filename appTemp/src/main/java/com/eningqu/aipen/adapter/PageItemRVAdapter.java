package com.eningqu.aipen.adapter;

import android.content.Context;
import android.graphics.Point;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.sdk.bean.NQDot;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.R;
import com.eningqu.aipen.qpen.CanvasFrame;
import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.TouchListener;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.bean.OfflinePageItemData;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.db.model.NoteBookData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/23 15:03
 * @Description:
 * @Email: liqiupost@163.com
 */
public class PageItemRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<OfflinePageItemData> mList;//存放数据
    Context context;
    /**
     * 空数据时，显示空布局类型
     */
    public static final int EMPTY_VIEW = 1;
    public static final int VIEW_TYPE_PAGE = 2;

    /**
     * 控制空布局的显隐
     */
    private int mViewType = 0;
    private OnEmptyClickListener listener;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerItemLongListener mOnItemLong = null;
    private OnGlobalLayoutListener onGlobalLayoutListener;

    public PageItemRVAdapter(Context context) {
        this.context = context;
        this.mList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (EMPTY_VIEW == viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_empty_layout, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_horizon_page_layout, parent, false);
            return new MyViewHolder(view, viewType, mOnItemClickListener, mOnItemLong);
        }
    }

    //在这里可以获得每个子项里面的控件的实例，比如这里的TextView,子项本身的实例是itemView，
    // 在这里对获取对象进行操作
    //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
    //position是点击位置
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = getItemViewType(position);
        if (VIEW_TYPE_PAGE == itemViewType) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            //设置textView显示内容为list里的对应项
            if (null != myViewHolder) {
                final OfflinePageItemData data = mList.get(position);
                if (null != myViewHolder.tvPageNum) {
                    myViewHolder.tvPageNum.setText(String.valueOf(data.getPage()));
                }
                if (null != myViewHolder.ivSelectState) {
                    //                boolean sel = (boolean)myViewHolder.ivSelectState.getTag();
                    if (data.isCheck()) {
                        myViewHolder.ivSelectState.setImageResource(R.drawable.selected);
                    } else {
                        myViewHolder.ivSelectState.setImageResource(R.drawable.selected_none);
                    }
                }

                if (null != data) {
                    /*myViewHolder.llPreview.post(new Runnable() {
                        @Override
                        public void run() {
                            myViewHolder.llPreview.getViewTreeObserver().addOnGlobalLayoutListener(
                                    new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            myViewHolder.llPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                        }
                                    });
                        }
                    });*/
                    /*if(null==myViewHolder.llPreview.getBackground()){

                        if(null!=onGlobalLayoutListener){
                            onGlobalLayoutListener.onGlobalLayout(myViewHolder.llPreview, position);
                        }
                    }*/
                    if (null != data.getDrawable()) {
                        myViewHolder.llPreview.setImageBitmap(data.getDrawable());
                    }
                }
                myViewHolder.itemView.setTag(position);
            }

        } else if (EMPTY_VIEW == itemViewType) {

            //空视图布局 - 点击事件的回调
            ((EmptyViewHolder) holder).llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onEmptyClick();
                    }
                }
            });
        }
    }

    //要显示的子项数量
    @Override
    public int getItemCount() {
        //        return list.size();
        //注意，空布局---> mViewType = 1 显示1个布局
        //            ---> mViewType = 0 所有布局都不显示的
        return (mList != null && mList.size() > 0) ? mList.size() : 1;
    }


    @Override
    public int getItemViewType(int position) {

/*        if (mViewType == EMPTY_VIEW) {
            //空布局的类型
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);*/
        return mViewType;
    }

    public interface OnEmptyClickListener {
        /**
         * 空视图的点击
         */
        void onEmptyClick();
    }

    /**
     * 设置空布局控件的点击监听，回调接口
     *
     * @param listener 回调接口
     */
    public void setOnEmptyClickListener(OnEmptyClickListener listener) {
        this.listener = listener;
    }

    public void setOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        this.onGlobalLayoutListener = listener;
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRoot;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);
        }
    }

    //这里定义的是子项的类，不要在这里直接对获取对象进行操作
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private OnRecyclerViewItemClickListener mOnItemClickListener;
        private OnRecyclerItemLongListener mOnItemLong;
        TextView tvPageNum;
        ImageView ivSelectState;
        ImageView llPreview;

        public MyViewHolder(View itemView, int viewType, OnRecyclerViewItemClickListener mListener, OnRecyclerItemLongListener longListener) {
            super(itemView);
            this.mOnItemClickListener = mListener;
            this.mOnItemLong = longListener;
            if (VIEW_TYPE_PAGE == viewType) {
                /*View main = itemView.findViewById(R.id.main);
                main.setOnClickListener(this);
                main.setOnLongClickListener(this);
                View unlock = itemView.findViewById(R.id.tv_unlock);
                unlock.setOnClickListener(this);*/
                tvPageNum = itemView.findViewById(R.id.tv_page_num);
                ivSelectState = itemView.findViewById(R.id.iv_select_state);
                llPreview = itemView.findViewById(R.id.ll_preview);
                //                ivSelectState.setTag(false);
                ivSelectState.setImageResource(R.drawable.selected_none);
                itemView.setTag(0);
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //                boolean sel = (boolean)v.findViewById(R.id.iv_select_state).getTag();
                //                ImageView ivState = v.findViewById(R.id.iv_select_state);
                //                ivState.setTag(!sel);
                final OfflinePageItemData data = mList.get((int) v.getTag());
                data.setCheck(!data.isCheck());
                //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(v, getAdapterPosition(), data.isCheck());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLong != null) {
                mOnItemLong.onItemLongClick(v, getPosition());
            }
            return true;
        }

    }

    /*之下的方法都是为了方便操作，并不是必须的*/

    //在指定位置插入，原位置的向后移动一格
    //    public boolean addItem(int position, String msg) {
    //        if (position < list.size() && position >= 0) {
    //            list.add(position, msg);
    //            notifyItemInserted(position);
    //            return true;
    //        }
    //        return false;
    //    }
    public void setList(List<OfflinePageItemData> list, int viewType) {

        if (!mList.isEmpty()) {
            //子条目布局 --> 恢复默认的布局
            int size = mList.size();
            mList.clear();
            notifyItemRangeRemoved(0, size);
            /*notifyDataSetChanged();*/
        }

        if (null != list && list.size() > 0) {
            if (mViewType == EMPTY_VIEW) {
                //空布局 --> 恢复默认的布局
                notifyItemRemoved(0);
            }
            mViewType = viewType;
            //刷新，新添加的数据
            mList.addAll(list);
            notifyItemRangeInserted(0, list.size());
        } else {
            //如果刷新的数据为空list，则显示空布局
            if (mViewType != EMPTY_VIEW) {
                //当前布局不是空布局，则刷新显示空布局
                mViewType = EMPTY_VIEW;
                notifyItemInserted(0);
            }
        }
    }

    //去除指定位置的子项
    public boolean removeItem(int position) {
        if (position < mList.size() && position >= 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            return true;
        }
        return false;
    }

    //清空显示数据
    public void clearAll() {
        mList.clear();
        notifyDataSetChanged();
    }

    /**
     * 设置为空布局
     * 如果当前布局已经是空布局，则不需要在进行刷新显示
     */
    public void setEmpty() {

        if (!mList.isEmpty()) {
            //如果在设置空布局之前，已经显示了子条目类型的数据，那么需要清空还原
            int size = mList.size();
            mList.clear();
            notifyItemRangeRemoved(0, size);
            /*notifyDataSetChanged();*/
        }

        if (mViewType != EMPTY_VIEW) {
            //当前布局不是空布局，则刷新显示空布局
            mViewType = EMPTY_VIEW;
            notifyItemInserted(0);
        }

    }

    private void loadStrokes(NoteBookData noteBookData, PageStrokesCacheBean pageStrokesCache, SignatureView strokeView) {

        if (null != pageStrokesCache && pageStrokesCache.getStrokesBeans().size() > 0) {
            L.error("paintCurPageStrokes() begin");
            int i;
            //一笔一笔转换，包括每一笔的颜色和粗细大小、坐标
            int book_height = null != noteBookData ? noteBookData.yMax : (int) SDKUtil.PAGER_HEIGHT_A5;
            int book_width = null != noteBookData ? noteBookData.xMax : (int) SDKUtil.PAGER_WIDTH_A5;
            int book_no = null != noteBookData ? noteBookData.noteType : NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
            for (StrokesBean strokesBean : pageStrokesCache.getStrokesBeans()) {
                i = 0;
                List<Point> points = strokesBean.getDots();
                if (null != strokeView) {
                    strokeView.setPenSize(CommandSize.getSizeByLevel(strokesBean.getSizeLevel()));
                    strokeView.setPenColor(strokesBean.getColor());
                }

                for (int j = 0; j < points.size(); j++) {
                    NQDot afDot = new NQDot();
                    if (j == 0) {
                        afDot.type = DotType.PEN_ACTION_DOWN;
                    } else if (j == points.size() - 1) {
                        afDot.type = DotType.PEN_ACTION_UP;
                    } else {
                        afDot.type = DotType.PEN_ACTION_MOVE;
                    }
                    afDot.x = points.get(j).x;
                    afDot.y = points.get(j).y;
                    afDot.book_height = (book_height == 0 ? (int) SDKUtil.PAGER_HEIGHT_A5 : book_height);
                    afDot.book_width = (book_width == 0 ? (int) SDKUtil.PAGER_WIDTH_A5 : book_width);
                    afDot.bookNum = book_no;
                    afDot.page = pageStrokesCache.getPage();

                    if (null != strokeView) {
                        strokeView.addDot(afDot, false);
                    }
                }
            }
            L.error("paintCurPageStrokes() end");
        }
    }

    public interface OnGlobalLayoutListener {
        void onGlobalLayout(View view, int position);
    }

    //点击和长按接口
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data, Object tag);

    }

    public interface OnRecyclerItemLongListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerItemLongListener listener) {
        this.mOnItemLong = listener;
    }
}
