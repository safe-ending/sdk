package com.eningqu.aipen.activity;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.RecogLanguageAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.bean.RecogLanguageBean;
import com.eningqu.aipen.common.RecognizeLanguageEnum;
import com.eningqu.aipen.databinding.ActivityRecogLanguagesBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.functions.Consumer;

/**
 * @Author: Qiu.Li
 * @Create Date: 2021/6/1 14:00
 * @Description: 语言选择列表
 * @Email: liqiupost@163.com
 */
public class RecogLanguageActivity extends BaseActivity {
    private ActivityRecogLanguagesBinding mBinding;
    RecogLanguageAdapter adapter;
    ArrayList<RecogLanguageBean> list = new ArrayList<>();
//    final String recoLang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);

    private RxPermissions rxPermission;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recog_languages);
    }

    @Override
    protected void initView() {
        mBinding.layoutTitle.tvTitle.setText(R.string.select_reco_lang_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvLangList.setLayoutManager(layoutManager);
        adapter = new RecogLanguageAdapter(this);
        mBinding.rvLangList.setAdapter(adapter);
    }

    /**
     * 动态权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rxPermission.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                    } else {
                    }
                }
            });
        }
    }

    @Override
    protected void initData() {
        RecogLanguageBean bean;
        // 初始化翻译语种列表
        for (RecognizeLanguageEnum languageNQEnum : RecognizeLanguageEnum.values()) {
            bean = new RecogLanguageBean();
            bean.setLangCode(languageNQEnum.getCode());
            bean.setName(languageNQEnum.getName());
            bean.setName0(languageNQEnum.getName0());
            bean.setSize(languageNQEnum.getSize());
            bean.setShortName(languageNQEnum.getShort_name());

            //sort: zh_CN->en_US->zh_TW->ja_JP
            if(bean.getShortName().equals("en_US")||
                    bean.getShortName().equals("zh_CN")||
                    bean.getShortName().equals("zh_TW")||
                    bean.getShortName().equals("ja_JP")){
                list.add(bean);
            }
        }
        rxPermission = new RxPermissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestPermissions();
    }

    @Override
    protected void initEvent() {
        adapter.setArrayList(list);
        adapter.notifyDataSetChanged();
    }

    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                setResult(0);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
