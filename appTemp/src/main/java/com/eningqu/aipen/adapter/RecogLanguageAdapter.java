package com.eningqu.aipen.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.blankj.utilcode.util.LogUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.bean.RecogLanguageBean;
import com.eningqu.aipen.bean.RecognizeResultBean;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.NumberUtil;
import com.eningqu.aipen.common.utils.SDCardHelper;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.SystemUtil;
import com.eningqu.aipen.common.utils.ZipUtil;
import com.eningqu.aipen.databinding.ItemLanguageRecogBinding;
import com.google.gson.Gson;
import com.eningqu.aipen.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @Author: Qiu.Li
 * @Create Date: 2021/6/1 13:58
 * @Description: 语言选择列表适配器
 * @Email: liqiupost@163.com
 */
public class RecogLanguageAdapter extends RecyclerView.Adapter<RecogLanguageAdapter.RecoLangViewHolder> {

    ArrayList<RecogLanguageBean> arrayList = new ArrayList<>();
    Activity mContext;
    private String curShortName;
    private OnItemClickListener itemClickListener;

    public RecogLanguageAdapter(Activity context) {
        mContext = context;
        curShortName = SpUtils.getString(mContext, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
    }

    public void setArrayList(ArrayList<RecogLanguageBean> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public RecoLangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemLanguageRecogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_language_recog, parent, false);
        return new RecoLangViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecoLangViewHolder holder, int position) {
        RecogLanguageBean languageBean = arrayList.get(position);
        holder.binding.itemTvLanguageName.setText(languageBean.getName());
        holder.binding.itemTvLanguageSubName.setText(SystemUtil.getResId(mContext, languageBean.getName0()));
        holder.binding.itemTvInfo.setText(languageBean.getSize());

        if (languageBean.isDown()) {
            holder.binding.itemTvInfo.setText(NumberUtil.mathLength(languageBean.getDownLoadSize()) + "/" + languageBean.getSize());
        } else {
            holder.binding.itemTvInfo.setText(languageBean.getSize());
        }

        File file = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf", languageBean.getShortName() + ".conf");
        if (file.exists()) {
//                holder.binding.itemRecordLanguageName.setTextColor(ContextCompat.getColor(mContext, R.color.colors_menu_black));
            holder.binding.itemIvEdit.setBackgroundResource(R.drawable.selector_delete_btn);
        } else {
//                holder.binding.itemRecordLanguageName.setTextColor(ContextCompat.getColor(mContext, R.color.color_747474));
            holder.binding.itemIvEdit.setBackgroundResource(R.drawable.selector_cloud_download_btn);
        }

        if (languageBean.getShortName().equals(curShortName)) {
            holder.binding.itemTvLanguageName.setTextColor(ContextCompat.getColor(mContext, R.color.app_click_btn_color));
            holder.binding.itemTvLanguageSubName.setTextColor(ContextCompat.getColor(mContext, R.color.app_click_btn_color));
        } else {
            holder.binding.itemTvLanguageName.setTextColor(ContextCompat.getColor(mContext, R.color.color_000000));
            holder.binding.itemTvLanguageSubName.setTextColor(ContextCompat.getColor(mContext, R.color.color_000000));
        }

        holder.binding.clRoot.setTag(position);
        holder.binding.itemIvEdit.setTag(position);
        holder.binding.itemTvInfo.setTag(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //点击和长按接口
    public interface OnItemClickListener {
        void onItemClick(View view, int data, Object tag);
    }

    public void setItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public class RecoLangViewHolder extends RecyclerView.ViewHolder {
        protected final ItemLanguageRecogBinding binding;

        public RecoLangViewHolder(ItemLanguageRecogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.clRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    RecogLanguageBean languageBean = arrayList.get(position);
//                    if (languageBean.getShortName().equals("en_US")) {
//                        curShortName = languageBean.getShortName();
//                        SpUtils.putString(mContext, Constant.SP_KEY_RECO_LANGUAGE, curShortName);
//                        Intent intent = new Intent();
//                        intent.putExtra("tranLanguage", languageBean.getLangCode());
//                        mContext.setResult(1, intent);
//                        mContext.finish();
//                    } else {
//                    }
                    File file = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS +  "/conf", languageBean.getShortName() + ".conf");
                    if (file.exists()) {
                        int resultCode = 0;
                        if(curShortName.equals(languageBean.getShortName())){
                            resultCode = 0;
                        } else {
                            resultCode = 1;
                            curShortName = languageBean.getShortName();
                        }
                        SpUtils.putString(mContext, Constant.SP_KEY_RECO_LANGUAGE, curShortName);
                        Intent intent = new Intent();
                        intent.putExtra("tranLanguage", languageBean.getLangCode());
                        mContext.setResult(resultCode, intent);
                        mContext.finish();
                    } else {
                        ToastUtils.showShort(R.string.recognize_undownload);
                    }

//                    if(null!=itemClickListener){
//                        itemClickListener.onItemClick(v, position, languageBean);
//                    }

                }
            });

            this.binding.itemIvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    final RecogLanguageBean languageBean = arrayList.get(position);

                    File file = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf", languageBean.getShortName() + ".conf");
                    if (file.exists()) {
                        //删除
                        ToastUtils.showShort(R.string.delete_success);
                        delete(v);
                    } else {
                        //下载
                        if(SDCardHelper.isSDCardEnable()){
                            if(!languageBean.isDown()){
                                download(languageBean);
                            }
                        } else {
                            ToastUtils.showShort("SDCard is disable");
                        }
                    }
                }
            });
        }
    }


    private void delete(View v){
        int position = (int) v.getTag();
        RecogLanguageBean languageBean = arrayList.get(position);
        File file = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf", languageBean.getShortName() + ".conf");
        FileUtils.deleteFile(file);
        File file1 = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/resources/" + languageBean.getShortName());
        FileUtils.deleteFile(file1);
        if (languageBean.getShortName().equals(curShortName)) {
            curShortName = "en_US";
            SpUtils.putString(mContext, Constant.SP_KEY_RECO_LANGUAGE, curShortName);
            mContext.setResult(2);
        }
        notifyDataSetChanged();
    }

    private void download(RecogLanguageBean languageBean) {
        final Map<String, String> maps = new HashMap<>();
        maps.put("lanuageCode", languageBean.getShortName());
        maps.put("type", "1");
        maps.put("pkgName", "com.eningqu.aipen");

        HttpUtils.doPost(AppCommon.MYSCRIPT_DOWNLOAD_URL, maps, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(R.string.recognize_download_failed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                final RecognizeResultBean recognizeResultBean = new Gson().fromJson(result, RecognizeResultBean.class);
                if (recognizeResultBean != null && recognizeResultBean.isSuccess()) {
                    if (recognizeResultBean.getCode() == 1) {
                        languageBean.setDown(true);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                        String url = recognizeResultBean.getData().get(0).getMyScriptResource();
                        int startIndex = url.lastIndexOf("/") + 1;
                        int endIndex = url.lastIndexOf("?");
                        final String fileName = url.substring(startIndex, endIndex);

                        File file = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ROOT );
                        if(!file.exists()){
                            FileUtils.createDirs(file);
                        }
                        if(!file.exists()){
                            ToastUtils.showShort(R.string.recognize_download_failed);
                            languageBean.setDown(false);
                            return;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpUtils.downFile(url, file.getAbsolutePath(), fileName, new HttpUtils.DownloadListener() {
                                    @Override
                                    public void onProgress(final long count, int progress) {

//                                        LogUtils.d("download size:"+NumberUtil.mathLength(count)+" MB, progress:"+progress);

                                        languageBean.setDownLoadSize(count);

                                        if(progress==100){
                                            try {
                                                ZipUtil.unzip(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ROOT + "/" + fileName, com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
                                                FileUtils.deleteFile(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ROOT + "/" + fileName);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                ToastUtils.showShort(R.string.recognize_download_failed);
                                            }
                                            languageBean.setDown(false);
                                            languageBean.setDownLoadSize(0);
                                        }

                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        languageBean.setDown(false);
                                        languageBean.setDownLoadSize(0);
                                        ToastUtils.showShort(message);
                                    }
                                });
                            }
                        }).start();
                    } else {
                        languageBean.setDown(false);
                        ToastUtils.showShort(recognizeResultBean.getMsg());
                    }
                }
            }
        });
    }
}
