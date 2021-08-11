package com.eningqu.aipen.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.activity.MainActivity;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.PAGE_OPEN_STATUS;
import com.eningqu.aipen.base.ActivityStackManager;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.db.model.NoteBookData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/8/8 16:17
 * desc   :
 * version: 1.0
 */
public abstract class DrawBaseFragment extends BaseFragment {

    protected abstract void onChangePage();

    //    protected abstract void onDrawPage(NQDot dot);
    protected abstract void onError(int error);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if(hidden){
//            eventBusUnRegister(this);
//        }else {
//            eventBusRegister(this);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        if (null == carrier) {
            return;
        }

        switch (carrier.getEventType()) {
            case Constant.OPEN_NOTEBOOK_CODE:
                L.error("try OPEN_NOTEBOOK_CODE");
                NoteBookData noteBookData = AppCommon.getCurrentNoteBookData();
                if (null != noteBookData) {
                    try {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).gotoPageDrawFragment(noteBookData);
                        } else {
                            onChangePage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    ActivityStackManager.getInstance().exitAllActivityExceptCurrent(MainActivity.class);
                } else {
                    L.error("cur notebookData is null");
                }
                break;
            case Constant.ERROR_NONE_SELECT_NOTEBOOK:
                AFPenClientCtrl.getInstance().cleanQueenDatas();
                ToastUtils.showShort(R.string.pls_select_notebook_tips);
                AppCommon.setCurrentPage(-1);
                AppCommon.setCurrentNoteBookData(null);
                AppCommon.setCurrentNotebookId("");
                break;
            case Constant.SWITCH_PAGE_CODE:
                if (AppCommon.getDrawOpenState() == PAGE_OPEN_STATUS.OPEN) {
                    onChangePage();
                }
                break;
            case Constant.ERROR_LOCKED:
                showToast(R.string.collected_canot_modif);
                break;
//            case Constant.FUNCTION_COMMAND_CODE:
//                CommandBase commandBase = (CommandBase) carrier.getObject();
//                //                onCommand(commandBase);
//                break;
            case Constant.USER_LOGOUT:
                AppCommon.setNotebooksChange(true);
                break;
        }
    }
}
