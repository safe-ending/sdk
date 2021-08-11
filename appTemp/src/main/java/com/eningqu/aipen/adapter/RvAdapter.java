package com.eningqu.aipen.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.eningqu.aipen.R;
import com.eningqu.aipen.db.model.PageData;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：8:56
 */
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvViewHolder> {
    private Context context;
    private List<PageData> pageDataList = Collections.emptyList();
    protected int changePosition = -1;

    private int scaleWidth = 0;
    private int scaleHeight = 0;
    private int mLastPosition = -1;

    public RvAdapter(Context mContext, List<PageData> pageDataList) {
        this.context = mContext;
        this.pageDataList = pageDataList;
        this.scaleWidth = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
        this.scaleHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 5.0f / 8.0f);
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_page_item, parent, false);
        RvViewHolder rvViewHolder = new RvViewHolder(view);
        rvViewHolder.setIsRecyclable(true);
        return rvViewHolder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
//        if (context == null) return;
//                Glide.with(context).load(pageDataList.get(position).picPath)
//                        .override(holder.iv.getMeasuredWidth(), holder.iv.getMeasuredHeight())
//                        .centerCrop()
//                        .into(holder.iv);
        if(!StringUtils.isEmpty(pageDataList.get(position).picUrl)){
            Picasso.with(context)
                    .load(new File(pageDataList.get(position).picUrl))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.iv);
        }
        boolean b = Integer.compare(position,mLastPosition) < 0 ? true : false;
        addInAnimation(holder.itemView, b);
        mLastPosition = position;
    }

    /**
     *  将动画对象加入集合中  根据左右滑动加入不同
     */
    private void addInAnimation(View view, boolean left) {
        List<Animator> list = new ArrayList<>();
        if (left) {
            list.add(ObjectAnimator.ofFloat(view, "translationX", -view.getMeasuredWidth() * 2, 0));
        } else {
            list.add(ObjectAnimator.ofFloat(view, "translationX", view.getMeasuredWidth() * 2, 0));
        }
        list.add(ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight() / 2, 0));
        list.add(ObjectAnimator.ofFloat(view, "alpha", 0, 1));
        list.add(ObjectAnimator.ofFloat(view, "scaleX", 0.25f, 1));
        list.add(ObjectAnimator.ofFloat(view, "scaleY", 0.25f, 1));
        startAnimation(list);
    }
    /**
     *  开启动画
     */
    private void startAnimation(List<Animator> list) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(list);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    @Override
    public int getItemCount() {
        return pageDataList.size();
    }

    class RvViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv;

        public RvViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.im_view);
        }
    }
}

