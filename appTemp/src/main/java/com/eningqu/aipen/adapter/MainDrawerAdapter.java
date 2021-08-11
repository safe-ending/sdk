package com.eningqu.aipen.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.view.CircleImageView;
import com.eningqu.aipen.db.model.UserInfoData;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/8 16:14
 */

public class MainDrawerAdapter extends RecyclerView.Adapter<MainDrawerAdapter.DrawerViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private UserInfoData userInfoData;

    public MainDrawerAdapter(Context mContext, UserInfoData userInfo) {
        this.mContext = mContext;
        if (null != userInfo && userInfo.userIcon != null) {
            this.userInfoData = userInfo;
        } else {
            try {
                String str = SpUtils.getString(mContext, SpUtils.LOGIN_INFO);
                if(!TextUtils.isEmpty(str)){
                    Gson gson = new Gson();
                    this.userInfoData = gson.fromJson(SpUtils.getString(mContext, SpUtils.LOGIN_INFO), UserInfoData.class);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void setUserInfoData(UserInfoData userInfoData) {
        this.userInfoData = userInfoData;
        notifyItemChanged(0);
    }

    private List<DrawerItem> dataList = Arrays.asList(
            new DrawerItemHeader(),
            new DrawerItemNormal(R.drawable.icon_item_book, R.string.drawer_home),
            new DrawerItemNormal(R.drawable.icon_item_collect, R.string.drawer_note_collect),
            new DrawerItemNormal(R.drawable.icon_item_calendar, R.string.calendar_text),
            //            new DrawerItemDivider(),
            new DrawerItemNormal(R.drawable.icon_item_label, R.string.label_text),
            //            new DrawerItemDivider(),
//            new DrawerItemNormal(R.drawable.nav_icon_bluetooth, R.string.drawer_ble_scan),
            //            new DrawerItemDivider(),
            new DrawerItemNormal(R.drawable.icon_item_operating, R.string.drawer_operating),
//            new DrawerItemNormal(R.drawable.icon_item_operating, R.string.user_agreement_title2),
            new DrawerItemNormal(R.drawable.icon_item_setting, R.string.drawer_setting),
            new DrawerItemNormal(R.drawable.icon_item_migration, R.string.drawer_data_migration)
            //            new DrawerItemDivider(),
            //            new DrawerItemDivider(),
//            new DrawerItemFooter(R.string.drawer_logout)
    );


    @Override
    public int getItemViewType(int position) {
        DrawerItem drawerItem = dataList.get(position);
        if (drawerItem instanceof DrawerItemNormal) {
            return TYPE_NORMAL;
        } else if (drawerItem instanceof DrawerItemHeader) {
            return TYPE_HEADER;
        } else if (drawerItem instanceof DrawerItemFooter) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return (dataList == null || dataList.size() == 0) ? 0 : dataList.size();
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DrawerViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.item_drawer_header, parent, false));
                break;
            case TYPE_NORMAL:
                viewHolder = new NormalViewHolder(inflater.inflate(R.layout.item_drawer_normal, parent, false));
                break;
            case TYPE_FOOTER:
                viewHolder = new FooterViewHolder(inflater.inflate(R.layout.item_drawer_footer, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        final DrawerItem item = dataList.get(position);
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            final DrawerItemNormal itemNormal = (DrawerItemNormal) item;
            normalViewHolder.icon.setBackgroundResource(itemNormal.iconRes);
            normalViewHolder.name.setText(itemNormal.titleRes);
            normalViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.itemClick(itemNormal.titleRes);
                    }
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            final DrawerItemHeader drawerItemHeader = (DrawerItemHeader) item;
            if (userInfoData != null) {
                if(TextUtils.isEmpty(userInfoData.userIcon)){
                    Picasso.with(mContext)
                            .load(R.drawable.default_head)
                            .placeholder(R.drawable.default_head)
                            .error(R.drawable.default_head)
                            .into(headerViewHolder.loginIcon);
                }else {
                    Picasso.with(mContext)
                            .load(userInfoData.userIcon)
                            .placeholder(R.drawable.default_head)
                            .error(R.drawable.default_head)
                            .into(headerViewHolder.loginIcon);
                }
                headerViewHolder.loginName.setText(userInfoData.userName);
            }
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            final DrawerItemFooter drawerItemFooter = (DrawerItemFooter) item;
            footerViewHolder.logoutName.setText(drawerItemFooter.titleRes);
            footerViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.itemClick(drawerItemFooter.titleRes);
                    }
                }
            });
        }

    }

    public OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void itemClick(int ResId);
    }


    //-------------------------item数据模型------------------------------
    // item统一的数据模型
    public interface DrawerItem {
    }

    //有图片和文字的item
    public class DrawerItemNormal implements DrawerItem {
        public int iconRes;
        public int titleRes;

        public DrawerItemNormal(int iconRes, int titleRes) {
            this.iconRes = iconRes;
            this.titleRes = titleRes;
        }

    }

    //    //分割线item
    //    public class DrawerItemDivider implements DrawerItem {
    //        public DrawerItemDivider() {
    //        }
    //    }

    //头部item
    public class DrawerItemHeader implements DrawerItem {
        public DrawerItemHeader() {
        }
    }

    //底部item
    public class DrawerItemFooter implements DrawerItem {
        public int titleRes;

        public DrawerItemFooter(int titleRes) {
            this.titleRes = titleRes;
        }
    }


    //----------------------------------ViewHolder数据模型---------------------------
    //抽屉ViewHolder模型
    public class DrawerViewHolder extends RecyclerView.ViewHolder {
        public DrawerViewHolder(View itemView) {
            super(itemView);
        }
    }

    //有图标有文字ViewHolder
    public class NormalViewHolder extends DrawerViewHolder {
        View view;
        @BindView(R.id.normal_icon)
        ImageView icon;
        @BindView(R.id.normal_name)
        TextView name;

        public NormalViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    //    //分割线ViewHolder
    //    public class DividerViewHolder extends DrawerViewHolder {
    //        public DividerViewHolder(View itemView) {
    //            super(itemView);
    //        }
    //    }

    //头部ViewHolder
    public class HeaderViewHolder extends DrawerViewHolder {

        @BindView(R.id.login_icon)
        CircleImageView loginIcon;
        @BindView(R.id.login_name)
        TextView loginName;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //底部ViewHolder
    public class FooterViewHolder extends DrawerViewHolder {
        View view;
        @BindView(R.id.logout_name)
        TextView logoutName;

        public FooterViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
