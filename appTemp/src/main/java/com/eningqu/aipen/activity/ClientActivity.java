package com.eningqu.aipen.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.bean.QpenDataBean;
import com.eningqu.aipen.bean.QpenZipBean;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.StringUtils;
import com.eningqu.aipen.common.utils.ZipUtil;
import com.eningqu.aipen.databinding.ActivityClientBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.fragment.FragmentClient;
import com.eningqu.aipen.fragment.FragmentServiceList;
import com.eningqu.aipen.zxing.camera.Intents;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClientActivity extends FragmentBaseActivity {

    boolean isAlive = true;
    DatagramSocket socket = null;
    private ActivityClientBinding mBinding;
    int postCount = 1024 * 25;
    ArrayList<byte[]> sendList = new ArrayList<>();
    private String serverIp;
    int curIndex = 0;
    private NoteBookData noteBookData;
    private boolean isConnection;
    FragmentClient fragmentClient;
    FragmentServiceList fragmentServiceList;
    private byte[] zipInfo;
    private byte[] zipHead;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_client);
    }

    @Override
    protected void initView() {
        mBinding.includeTopBar.tvTitle.setText(R.string.str_send_data);
        fragmentClient = new FragmentClient();
        fragmentServiceList = FragmentServiceList.newInstance();
        switchFragment(fragmentClient);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                backClick();
                break;
            default:
                fragmentClient.onViewClick(view);
                break;
        }
    }

    @Override
    protected void initData() {
        receive();
    }

    @Override
    protected void initEvent() {
        startActivityForResult(new Intent(this, CaptureActivity.class), 111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 111) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString(Intents.Scan.RESULT);
                if (!TextUtils.isEmpty(scanResult)) {
                    if (scanResult.startsWith("QPen_")) {
                        serverIp = scanResult.substring(5);

                        //启动线程 向服务器发送和接收信息
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("code", "1");
                            jsonObject.put("version", "1");
                            jsonObject.put("system", "1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        byte[] bytes = jsonObject.toString().getBytes();
                        new MyThread(bytes).start();
                    }
                }
            }
        } else {
            finish();
        }
    }

    class MyThread extends Thread {

        public byte[] txt1;

        public MyThread(byte[] str) {
            txt1 = str;
        }

        @Override
        public void run() {
            //定义消息
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                //连接服务器 并设置连接超时为1秒
                InetAddress serverAddress = InetAddress.getByName(serverIp);

                DatagramPacket packet = new DatagramPacket(txt1, txt1.length, serverAddress, 5558);
                socket.send(packet);
            } catch (Exception aa) {
                //连接超时 在UI界面显示消息
                bundle.putString("msg", "服务器连接失败！请检查网络是否打开");
            }
        }
    }

    private void receive() {
        new Thread() {
            public void run() {
                try {
                    socket = new DatagramSocket(5558);
                    while (isAlive) {
                        try {
                            byte[] data = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(data, data.length);
                            socket.receive(packet);
                            byte[] getData = new byte[packet.getLength()];
                            System.arraycopy(packet.getData(), packet.getOffset(), getData, 0, packet.getLength());
                            String result = new String(getData);
                            operateResult(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }.start();
    }

    private void operateResult(String result) {
        Log.w("test", "???????????" + result);
        QpenZipBean qpenZipBean = new Gson().fromJson(result, QpenZipBean.class);
        if (qpenZipBean.getCode().equals("1")) {
            isConnection = true;
            tryCount = 0;
//            sendZipInfo();
        } else if (qpenZipBean.getCode().equals("2")) {
            tryCount = 0;
            handler.removeMessages(11224);
            sendZipHead();
        } else if (qpenZipBean.getCode().equals("3")) {
            handler.removeMessages(11225);
            tryCount = 0;
            sendZip();
            sendEnd();
        } else if (qpenZipBean.getCode().equals("5")) {
            List<String> indexs = qpenZipBean.getIndexs();
            if (indexs != null && indexs.size() > 0) {
                QpenZipBean<String> qpenZipBean2 = new QpenZipBean<>();
                for (int i = 0; i < indexs.size(); i++) {
                    qpenZipBean2 = new QpenZipBean<>();
                    qpenZipBean2.setCode("4");
                    qpenZipBean2.setVersion("1");
                    qpenZipBean2.setNotebook_id(noteBookData.notebookId);
                    qpenZipBean2.setIndex(indexs.get(i));
                    qpenZipBean2.setData(StringUtils.bytesToHexString(sendList.get(i)));

                    new MyThread(new Gson().toJson(qpenZipBean2).getBytes()).start();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                sendEnd();
            }
        } else if (qpenZipBean.getCode().equals("7")) {
            handler.removeMessages(11223);
            tryCount = 0;
            List<Integer> selectedList = fragmentClient.getAdapter().getSelectedList();
            List<NoteBookData> dataList = fragmentClient.getAdapter().getDataList();
            NoteBookData noteBookData = dataList.get(selectedList.get(curIndex));
            File file = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + noteBookData.notebookId + ".zip");
            FileUtils.delete(file);
            fragmentServiceList.setDataStatus(noteBookData.notebookId);
            curIndex++;
            if (curIndex < selectedList.size()) {
                sendZipHead();
            } else {
                isConnection = false;
                curIndex = 0;
                ToastUtils.showShort(R.string.str_send_success);
                backClick();
            }
        } else if (qpenZipBean.getCode().equals("-1")) {
            finish();
        } else if (qpenZipBean.getCode().equals("6")) {
            handler.removeMessages(11223);

            tryCount = 0;
            sendZipHead();
        }
    }

    private void sendEnd() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        QpenZipBean qpenZipBean2 = new QpenZipBean<>();
        qpenZipBean2.setCode("7");
        qpenZipBean2.setVersion("1");
        qpenZipBean2.setNotebook_id(noteBookData.notebookId);
        byte[] bytes = new Gson().toJson(qpenZipBean2).getBytes();
        new MyThread(bytes).start();
        handler.sendEmptyMessageDelayed(11223, 900);
    }

    private void sendZip() {
        QpenZipBean<String> qpenZipBean2 = new QpenZipBean<>();
        sendList.clear();

        File file = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + noteBookData.notebookId + ".zip");

        byte[] sendData = new byte[postCount];

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int index = 0;
            for (int length; (length = fileInputStream.read(sendData)) != -1; ) {
                byte[] sendData2 = new byte[length];
                qpenZipBean2 = new QpenZipBean<>();
                qpenZipBean2.setCode("4");
                qpenZipBean2.setVersion("1");
                qpenZipBean2.setNotebook_id(noteBookData.notebookId);
                qpenZipBean2.setIndex(index + "");
                System.arraycopy(sendData, 0, sendData2, 0, length);
                qpenZipBean2.setData(StringUtils.bytesToHexString(sendData2));

                sendList.add(sendData2);
                fragmentServiceList.setCurCount(index);
                new MyThread(new Gson().toJson(qpenZipBean2).getBytes()).start();
                Thread.sleep(20);
                index++;
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendZipHead() {
        List<Integer> selectedList = fragmentClient.getAdapter().getSelectedList();
        List<NoteBookData> dataList = fragmentClient.getAdapter().getDataList();
        noteBookData = dataList.get(selectedList.get(curIndex));

        QpenZipBean<QpenDataBean> qpenZipBean2 = new QpenZipBean<>();
        qpenZipBean2.setCode("3");
        qpenZipBean2.setVersion("1");
        qpenZipBean2.setNotebook_id(noteBookData.notebookId);

        final File file = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + noteBookData.notebookId + ".zip");

        if (file.exists()) {
            qpenZipBean2.setLength(file.length() + "");
            qpenZipBean2.setCount(file.length() / postCount + 1 + "");

            zipHead = new Gson().toJson(qpenZipBean2).getBytes();
            sentZipHead();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragmentServiceList.sendDataInfo(noteBookData.notebookId, file.length(),
                        (int) (file.length() / postCount + 1));
            }
        });

    }

    private void sentZipHead() {
        new MyThread(zipHead).start();
        handler.sendEmptyMessageDelayed(11225, 1000);
    }

    private void sendZipInfo() {
        isConnection = true;
        ArrayList<QpenDataBean> arrayList = new ArrayList<>();
        QpenZipBean<ArrayList<QpenDataBean>> qpenZipBean1 = new QpenZipBean<>();
        qpenZipBean1.setCode("2");
        qpenZipBean1.setVersion("1");
        List<Integer> selectedList = fragmentClient.getAdapter().getSelectedList();
        List<NoteBookData> dataList = fragmentClient.getAdapter().getDataList();
        for (int i : selectedList) {
            NoteBookData noteBookData = dataList.get(i);
            QpenDataBean qpenDataBean = new QpenDataBean();
            qpenDataBean.setBook_no(noteBookData.noteType + "");
            qpenDataBean.setCover_id(noteBookData.noteCover);
            qpenDataBean.setNotebook_id(noteBookData.notebookId);
            qpenDataBean.setNotebook_name(noteBookData.noteName);

            List<PageData> pageData = AppCommon.loadPageDataList(noteBookData.notebookId, false);
            List<QpenDataBean.PageSBean> list = new ArrayList<>();
            SimpleDateFormat ttt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            for (PageData f : pageData) {
                QpenDataBean.PageSBean sBean = new QpenDataBean.PageSBean();
                Date date = TimeUtils.string2Date(f.lastModifyTime, ttt);
                long longTime = TimeUtils.date2Millis(date) / 1000;
                sBean.setCreate_at(longTime + "");
                sBean.setPage_name(f.name + "");
                sBean.setPage_no(f.pageNum + "");
                list.add(sBean);
            }

            qpenDataBean.setPages(list);
            arrayList.add(qpenDataBean);

            try {
                ZipUtil.zip(AppCommon.NQ_SAVE_ROOT_PATH + File.separator + AppCommon.getUserUID() + File.separator + noteBookData.notebookId,
                        AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + noteBookData.notebookId + ".zip");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fragmentServiceList.sendMigration(arrayList);
        qpenZipBean1.setData(arrayList);

        zipInfo = new Gson().toJson(qpenZipBean1).getBytes();
        sentZip();
    }

    public void sentZip() {
        new MyThread(zipInfo).start();

        handler.sendEmptyMessageDelayed(11224, 1000);
    }

    int tryCount = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11223:
                    if (tryCount < 10) {
                        tryCount++;
                        sendEnd();
                    }
                    break;
                case 11224:
                    if (tryCount < 10) {
                        tryCount++;
                        sentZip();
                    } else {
                        ToastUtils.showShort(R.string.str_link_fail);
                        sendFinish();
                    }
                    break;
                case 11225:
                    if (tryCount < 10) {
                        tryCount++;
                        sentZipHead();
                    } else {
                        ToastUtils.showShort(R.string.str_link_fail);
                        sendFinish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        isAlive = false;
        if(null!=socket){
            socket.close();
        }
        socket = null;
        super.onDestroy();
    }

    private void backClick() {
        if (isConnection) {
            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    sendFinish();
                    dismissDialog();
                }

                @Override
                public void cancel() {
                    dismissDialog();
                }
            }, R.string.str_close_tips, R.string.dialog_confirm_text);
        } else {
            sendFinish();
        }
    }

    private void sendFinish() {
        QpenZipBean<QpenDataBean> qpenZipBean2 = new QpenZipBean<>();
        qpenZipBean2.setCode("-1");
        qpenZipBean2.setVersion("1");
        byte[] bytes = new Gson().toJson(qpenZipBean2).getBytes();
        new MyThread(bytes).start();

        finish();
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return fragmentClient;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.main_container;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            backClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        switch (carrier.getEventType()) {
            case Constant.MIGRATION_SENT:
                switchFragment(fragmentServiceList);
                sendZipInfo();
                break;
        }
    }
}
