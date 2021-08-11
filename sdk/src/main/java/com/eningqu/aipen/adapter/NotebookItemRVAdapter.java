package com.eningqu.aipen.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.db.model.NoteBookData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/23 15:03
 * @Description:
 * @Email: liqiupost@163.com
 */
public class NotebookItemRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<NoteBookData> mList;//存放数据
    Context context;
    /**
     * 空数据时，显示空布局类型
     */
    public static final int EMPTY_VIEW = 1;
    public static final int VIEW_TYPE_NOTEBOOK_COLLECT = 2;
    public static final int VIEW_TYPE_NOTEBOOK_SEARCH = 3;
    public static final int VIEW_TYPE_NOTEBOOK_VERTICAL = 4;

    /**
     * 控制空布局的显隐
     */
    private int mViewType = 0;
    private OnEmptyClickListener listener;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerItemLongListener mOnItemLong = null;

    private Integer[] thums = {R.drawable.thumbtack_blue,
            R.drawable.thumbtack_green,
            R.drawable.thumbtack_pink,
            R.drawable.thumbtack_orange};

    public NotebookItemRVAdapter(Context context) {
        this.context = context;
        this.mList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (EMPTY_VIEW == viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_empty_layout, parent, false);
            return new EmptyViewHolder(view);
        } else if (VIEW_TYPE_NOTEBOOK_COLLECT == viewType||
                VIEW_TYPE_NOTEBOOK_VERTICAL==viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notebook_collect, parent, false);
            return new MyViewHolder(view, viewType, mOnItemClickListener, mOnItemLong);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_notebook_search, parent, false);
            return new MyViewHolder(view, viewType, mOnItemClickListener, mOnItemLong);
        }
    }

    //在这里可以获得每个子项里面的控件的实例，比如这里的TextView,子项本身的实例是itemView，
// 在这里对获取对象进行操作
    //holder.itemView是子项视图的实例，holder.textView是子项内控件的实例
    //position是点击位置
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (VIEW_TYPE_NOTEBOOK_SEARCH == itemViewType) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            //设置textView显示内容为list里的对应项
            final NoteBookData noteBook = (NoteBookData)mList.get(position);
            if (noteBook.noteName != null) {
                myViewHolder.textViewName.setText(noteBook.noteName);
            }
            int res = position % 4;
            myViewHolder.ivThum.setImageResource(thums[res]);

            holder.itemView.setTag(R.string.note_book_id, noteBook.notebookId);
            holder.itemView.setTag(R.string.note_name, noteBook.noteName);
            holder.itemView.setTag(R.string.note_lock, noteBook.isLock);
            holder.itemView.setTag(R.string.note_type, noteBook.noteType);
            holder.itemView.setTag(R.string.note_time, noteBook.createTime);
        } else if (VIEW_TYPE_NOTEBOOK_COLLECT == itemViewType||
                VIEW_TYPE_NOTEBOOK_VERTICAL==itemViewType) {
            final NoteBookData noteBook = (NoteBookData)mList.get(position);
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            //设置textView显示内容为list里的对应项
            if (noteBook != null) {
                myViewHolder.textViewName.setText(noteBook.noteName);
            }
            holder.itemView.setTag(R.string.note_book_id, noteBook.notebookId);
            holder.itemView.setTag(R.string.note_name, noteBook.noteName);
            holder.itemView.setTag(R.string.note_lock, noteBook.isLock);
            holder.itemView.setTag(R.string.note_type, noteBook.noteType);
            holder.itemView.setTag(R.string.note_time, noteBook.createTime);
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
        TextView textViewName;
        ImageView ivThum;

        public MyViewHolder(View itemView, int viewType, OnRecyclerViewItemClickListener mListener, OnRecyclerItemLongListener longListener) {
            super(itemView);
            this.mOnItemClickListener = mListener;
            this.mOnItemLong = longListener;
            textViewName = itemView.findViewById(R.id.textViewName);
            ivThum = itemView.findViewById(R.id.iv_thumbtack);
            if(VIEW_TYPE_NOTEBOOK_SEARCH == viewType){
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }else if(VIEW_TYPE_NOTEBOOK_COLLECT ==viewType ||
                    VIEW_TYPE_NOTEBOOK_VERTICAL==viewType){
                View main = itemView.findViewById(R.id.main);
                main.setOnClickListener(this);
                main.setOnLongClickListener(this);

                View unlock = itemView.findViewById(R.id.tv_unlock);
                unlock.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(v, getAdapterPosition(), v.getTag(R.string.note_lock));
            }
        }

        @Override
        public boolean onLongClick(View v) {
//            if (mOnItemLong != null) {
//                mOnItemLong.onItemLongClick(v, getPosition());
//            }
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
    public void setList(List<NoteBookData> list, int viewType) {

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
