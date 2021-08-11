package com.eningqu.aipen.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.qpen.bean.AudioRecordBean;
import com.eningqu.aipen.common.utils.AudioUtil;
import com.eningqu.aipen.common.utils.TimeUtil;
import com.eningqu.aipen.databinding.ItemRecordHistoryBinding;
import com.eningqu.aipen.qpen.service.MediaService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/5/10 21:44
 * @Description: 录音列表
 * @Email: liqiupost@163.com
 */
public class RecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 空数据时，显示空布局类型
     */
    public static final int EMPTY_VIEW = 1;
    public static final int VIEW_TYPE_RECORD = 2;
    /**
     * 控制空布局的显隐
     */
    private int mViewType = 0;

    List<AudioRecordBean> records;
    Context context;
    int curIndex = -1;
    AudioRecordBean curBean = null;
    private RecordListDeleteListener deleteListener;
    private SeekBar seekBar;
    private boolean isPlay;
    private TextView curTime;

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int index) {
        curIndex = index;
    }

    public interface RecordListDeleteListener {
        void onDelete(int position);
    }

    public RecordListAdapter(Context context) {
        this.context = context;
        this.records = new ArrayList<>();
    }

    public void setRecords(List<AudioRecordBean> list) {
        if (null != records && !records.isEmpty()) {
            //子条目布局 --> 恢复默认的布局
            int size = records.size();
            records.clear();
            notifyItemRangeRemoved(0, size);
            /*notifyDataSetChanged();*/
        }

        if (null != list && list.size() > 0) {
            if (mViewType == EMPTY_VIEW) {
                //空布局 --> 恢复默认的布局
                notifyItemRemoved(0);
            }
            mViewType = VIEW_TYPE_RECORD;
            //刷新，新添加的数据
            records.addAll(list);
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

    public void setDeleteListener(RecordListDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_RECORD == viewType) {
            ItemRecordHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_record_history, parent, false);
            return new HistoryViewHolder(binding);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_empty_layout, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        int itemViewType = getItemViewType(i);
        if (VIEW_TYPE_RECORD == itemViewType) {
            final AudioRecordBean bean = records.get(i);
            final HistoryViewHolder historyViewHolder = (HistoryViewHolder) holder;
            historyViewHolder.binding.recordHistoryName.setText(context.getString(R.string.str_record) + bean.postion);
            historyViewHolder.binding.recordHistoryTime.setText(TimeUtil.convertTime(context.getString(R.string.str_format_time), bean.createTime * 1000));
            historyViewHolder.binding.recordHistoryTotalTime.setText(TimeUtil.recordTime(bean.duration));
            historyViewHolder.binding.recordHistoryCurTime.setText(TimeUtil.recordTime(bean.curTime));

            historyViewHolder.binding.recordHistorySeeBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            historyViewHolder.binding.recordHistorySeeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (curIndex != i) {
                        historyViewHolder.binding.recordHistorySeeBar.setProgress(0);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            if (curIndex == i) {
                historyViewHolder.binding.recordHistorySeeBar.setProgress((int) (bean.curTime * 100 / bean.duration));
                if (isPlay) {
                    historyViewHolder.binding.recordHistoryStatus.setImageResource(R.drawable.icon_play);
                } else {
                    historyViewHolder.binding.recordHistoryStatus.setImageResource(R.drawable.icon_stop2);
                }
                curTime = historyViewHolder.binding.recordHistoryCurTime;
                seekBar = historyViewHolder.binding.recordHistorySeeBar;
            } else {
                if (curIndex == -1) {
                    if (seekBar != null) {
                        seekBar.setProgress(0);
                    }
                    if (curTime != null) {
                        curTime.setText(TimeUtil.recordTime(0));
                    }
                }
                historyViewHolder.binding.recordHistorySeeBar.setProgress(0);
                historyViewHolder.binding.recordHistoryStatus.setImageResource(R.drawable.icon_stop2);
            }

            historyViewHolder.binding.tvDelete.setTag(i);
            historyViewHolder.binding.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete record
                    int i = (int) v.getTag();

                    if (null != deleteListener) {
                        deleteListener.onDelete(i);
                    }
                }
            });

            historyViewHolder.binding.recordHistoryStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHandler.removeMessages(1);
                    if (curIndex == i) {
                        if (isPlay) {
                            MediaService.getInstance().pause();
                        } else {
                            mHandler.sendEmptyMessage(1);
                            MediaService.getInstance().play();
                        }
                        isPlay = !isPlay;
                        notifyDataSetChanged();
                        return;
                    } else {
                        isPlay = true;
                        MediaService.getInstance().stop();
                        curIndex = i;
                        if (curBean != null)
                            curBean.curTime = 0;
                        curBean = bean;
                    }

                    mHandler.sendEmptyMessageDelayed(1, 1000);
                    notifyDataSetChanged();
                    String name = curBean.filePath.replace("pcm", "wav");
                    AudioUtil.getInstance().play(curBean.filePath, name);
                    AudioUtil.getInstance().setAudioCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlay = false;
                            curIndex = -1;
                            if (curBean != null)
                                curBean.curTime = 0;
                            curBean = null;
                            notifyDataSetChanged();
                            mHandler.removeMessages(1);
                        }
                    });
                }
            });
        } else {
            //空视图布局 - 点击事件的回调
            ((EmptyViewHolder) holder).llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != curBean) {
                curBean.curTime = MediaService.getInstance().getCurrentPosition();
                if (curBean.curTime <= curBean.duration) {
                    if (seekBar != null) {
                        seekBar.setProgress((int) (curBean.curTime * 100 / curBean.duration));
                    }
                    if (curTime != null) {
                        curTime.setText(TimeUtil.recordTime(curBean.curTime));
                    }
                }
            }

            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
    };

    public void stopHandler() {
        mHandler.removeMessages(1);
    }

    public void cleanHandler() {
        if (seekBar != null)
            seekBar.setProgress(0);
//        if (curBean != null) {
//            curBean = null;
//        }
//        if (seekBar != null) {
//            seekBar = null;
//        }
        mHandler = null;
    }

    @Override
    public int getItemCount() {
        //注意，空布局---> mViewType = 1 显示1个布局
        //            ---> mViewType = 0 所有布局都不显示的
        return (records != null && records.size() > 0) ? records.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        protected final ItemRecordHistoryBinding binding;

        public HistoryViewHolder(ItemRecordHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRoot;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.ll_root);
        }
    }
}
