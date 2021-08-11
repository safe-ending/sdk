package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.bean.QpenDataBean;
import com.eningqu.aipen.bean.QpenZipBean;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.StringUtils;
import com.eningqu.aipen.common.utils.ZipUtil;
import com.eningqu.aipen.databinding.ActivityServiceBinding;
import com.eningqu.aipen.fragment.FragmentServer;
import com.eningqu.aipen.fragment.FragmentServiceList;
import com.eningqu.aipen.qpen.listener.IQPenCollectNotebookListener;
import com.eningqu.aipen.qpen.listener.IQPenDeleteNotebookListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceActivity extends FragmentBaseActivity {
    public static DatagramSocket serverSocket = null;
    private String IP = "";
    private ActivityServiceBinding mBinding;
    private String notebook_id;
    private long length;
    private Map<Integer, byte[]> dataSets = new HashMap<>();
    private int count;
    boolean isAlive = true;
    private boolean isConnection = false;
    FragmentServer fragmentServer;
    FragmentServiceList fragmentServiceList;
    private ArrayList<QpenDataBean> migrationdata;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 11124:
                    try {
                        checkZip(notebook_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };
    private InetAddress serverAddress;

    @Override
    protected void initEvent() {
        fragmentServer = new FragmentServer();
        fragmentServiceList = FragmentServiceList.newInstance();
    }

    @Override
    protected void initData() {
        new Thread() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.clear();
                try {
                    serverSocket = new DatagramSocket(5558);
                    while (isAlive) {
                        Message msg = new Message();
                        msg.what = 0x11;
                        byte[] data = new byte[1024 * 52];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        serverSocket.receive(packet);
                        byte[] getData = new byte[packet.getLength()];
                        System.arraycopy(packet.getData(), packet.getOffset(), getData, 0, packet.getLength());
                        String result = new String(getData);

                        operateResult(packet.getAddress(), result);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        }.start();
    }

    private void operateResult(InetAddress address, String result) throws IOException {
        Log.w("test", "result = " + result);

        QpenZipBean qpenZipBean = new Gson().fromJson(result, QpenZipBean.class);
        if (qpenZipBean.getCode().equals("1")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", "1");
                jsonObject.put("version", "1");
                jsonObject.put("system", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] bytes = jsonObject.toString().getBytes();
            serverAddress = InetAddress.getByName(address.getHostAddress());
            DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
            isConnection = true;
            serverSocket.send(packetS);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switchFragment(fragmentServiceList);
                }
            });
        } else if (qpenZipBean.getCode().equals("2")) {
            isConnection = true;
            Type t = new TypeToken<QpenZipBean<ArrayList<QpenDataBean>>>() {
            }.getType();
            QpenZipBean<ArrayList<QpenDataBean>> qpenDataBeanQpenZipBean = new Gson().fromJson(result, t);
            migrationdata = qpenDataBeanQpenZipBean.getData();
            Log.w("test", "qp = " + migrationdata.size());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentServiceList.sendMigration(migrationdata);
                }
            });

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", "2");
                jsonObject.put("version", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] bytes = jsonObject.toString().getBytes();
            DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
            serverSocket.send(packetS);
        } else if (qpenZipBean.getCode().equals("3")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", "3");
                jsonObject.put("version", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] bytes = jsonObject.toString().getBytes();
            DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
            serverSocket.send(packetS);

            notebook_id = qpenZipBean.getNotebook_id();
            length = Long.parseLong(qpenZipBean.getLength());
            count = Integer.parseInt(qpenZipBean.getCount());
            dataSets.clear();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentServiceList.sendDataInfo(notebook_id, length, count);
                }
            });
        } else if (qpenZipBean.getCode().equals("4")) {
            mHandler.removeMessages(11124);
            mHandler.sendEmptyMessageDelayed(11124, 10000);
            Type t = new TypeToken<QpenZipBean<String>>() {
            }.getType();
            QpenZipBean<String> qpenDataBeanQpenZipBean = new Gson().fromJson(result, t);

            dataSets.put(Integer.parseInt(qpenZipBean.getIndex()), StringUtils.hexStringToBytes(qpenDataBeanQpenZipBean.getData()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentServiceList.setCurCount(dataSets.size());
                }
            });

        } else if (qpenZipBean.getCode().equals("7")) {
            mHandler.removeMessages(11124);
            checkZip(qpenZipBean.getNotebook_id());
        } else if (qpenZipBean.getCode().equals("-1")) {
            finish();
        }
    }

    private void checkZip(String bookid) throws IOException {
        if (bookid.equals(notebook_id)) {
            File file = createZipFile();

            List<String> indexs = new ArrayList<>();
            Object[] objects = dataSets.keySet().toArray();
            Arrays.sort(objects);
            for (int i = 0; i < objects.length; i++) {
                try {
                    createFileAdd(file.getPath(), dataSets.get(objects[i]), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (file.length() != length) {
                zipError(indexs);
            } else {
                AppCommon.setNotebooksChange(true);
                //成功
                if (unzipBooks(file)) {
                    sentSuccess();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentServiceList.setDataStatus(notebook_id);
                            if (fragmentServiceList.getMapSize() == migrationdata.size()) {
                                ToastUtils.showShort(R.string.str_get_success);
                                isConnection = false;
                                backClick();
                            }
                        }
                    });
                    dbNote(file);
                } else {
                    reSentCall();
                }

            }
        }
    }

    private void sentSuccess() throws IOException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", "7");
            jsonObject.put("version", "1");
            jsonObject.put("notebook_id", notebook_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        byte[] bytes = jsonObject.toString().getBytes();
        DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
        serverSocket.send(packetS);
    }

    private void dbNote(File file) {
        for (QpenDataBean qpenDataBean : migrationdata) {
            if (qpenDataBean.getNotebook_id().equals(notebook_id)) {
                AppCommon.deleteNoteBook(AppCommon.getUserUID(), qpenDataBean.getNotebook_id(),
                        new IQPenDeleteNotebookListener() {
                            @Override
                            public void onSuccessful() {

                            }

                            @Override
                            public void onFail() {

                            }
                        });

                AppCommon.createMigrationNoteBook(Integer.parseInt(qpenDataBean.getBook_no()), qpenDataBean.getCover_id(),
                        AppCommon.getUserUID(), qpenDataBean.getNotebook_name(), qpenDataBean.getNotebook_id(),
                        new IQPenCollectNotebookListener() {
                            @Override
                            public void onSuccessful() {

                            }

                            @Override
                            public void onFail() {

                            }
                        });

//                String s = AppCommon.NQ_SAVE_ROOT_PATH + File.separator
//                        + AppCommon.getUserUID() + File.separator + file.getName().replace(".zip", "");
//                File file1 = new File(s);
//                File[] files = file1.listFiles();
//                for (File file2 : files) {
//                    AppCommon.createPageData(this, qpenDataBean.getNotebook_id(),
//                            Integer.parseInt(qpenDataBean.getBook_no()),
//                            Integer.parseInt(file2.getName()));
//                }
                List<QpenDataBean.PageSBean> pages = qpenDataBean.getPages();
                for (QpenDataBean.PageSBean pageSBean : pages) {
                    AppCommon.createPageData(this, qpenDataBean.getNotebook_id(),
                            Integer.parseInt(qpenDataBean.getBook_no()),
                            Integer.parseInt(pageSBean.getPage_no()), pageSBean.getPage_name());
                }

            }
        }
    }

    private void zipError(List<String> indexs) throws IOException {
        if (dataSets.size() == 0) {
            reSentCall();
        } else {
            for (int i = 0; i < count; i++) {
                if (!dataSets.containsKey(i)) {
                    indexs.add(i + "");
                }
            }
            QpenZipBean<QpenDataBean> qpenZipBean1 = new QpenZipBean<>();
            qpenZipBean1.setCode("5");
            qpenZipBean1.setVersion("1");
            qpenZipBean1.setNotebook_id(notebook_id);
            qpenZipBean1.setIndexs(indexs);

            byte[] bytes = new Gson().toJson(qpenZipBean1).getBytes();
            DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
            serverSocket.send(packetS);
        }
    }

    private void reSentCall() throws IOException {
        QpenZipBean<QpenDataBean> qpenZipBean1 = new QpenZipBean<>();
        qpenZipBean1.setCode("6");
        qpenZipBean1.setVersion("1");
        qpenZipBean1.setNotebook_id(notebook_id);

        byte[] bytes = new Gson().toJson(qpenZipBean1).getBytes();
        DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
        serverSocket.send(packetS);
    }

    @NonNull
    private File createZipFile() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(absolutePath + "/" + notebook_id + ".zip");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private boolean unzipBooks(File file) {
        if (file.exists()) {
            try {
                String s = AppCommon.NQ_SAVE_ROOT_PATH + File.separator
                        + AppCommon.getUserUID() + File.separator + file.getName().replace(".zip", "");
                FileUtils.delete(s);
                ZipUtil.unzip(file.getAbsolutePath(), s);
                FileUtils.delete(file);

//                File zipFiles = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + AppCommon.getUserUID());
//                File[] files = zipFiles.listFiles();
//                for (File f : files) {
//                    String name = f.getName();
//                    File dbFile = new File(AppCommon.NQ_SAVE_ROOT_PATH + File.separator + AppCommon.getUserUID() + File.separator + name);
//                    if (dbFile.exists()) {
//                        FileUtils.deleteDir(dbFile);
//                    }
////                    FileUtils.createOrExistsDir(dbFile);
//                    boolean b = FileUtils.moveDir(f, dbFile, new FileUtils.OnReplaceListener() {
//                        @Override
//                        public boolean onReplace() {
//                            return false;
//                        }
//                    });
//                }
//                FileUtils.deleteDir(zipFiles);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(R.string.str_zip_error);
            }
        }
        return false;
    }

    public void createFileAdd(String path, byte[] bytes, boolean append) throws IOException {
        // 实例化对象：文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(path, append);
        // 写入文件
        fileOutputStream.write(bytes);
        // 清空输出流缓存
        fileOutputStream.flush();
        // 关闭输出流
        fileOutputStream.close();
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return fragmentServer;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.main_container;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        serverSocket.close();
        serverSocket = null;
    }

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_service);
    }

    @Override
    protected void initView() {
        mBinding.includeTopBar.tvTitle.setText(R.string.str_get_data);

    }

    public void onViewClick(View view) {
        if (view.getId() == R.id.iv_back) {
            backClick();
        }
    }

    private void backClick() {
        if (isConnection) {
            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    QpenZipBean<QpenDataBean> qpenZipBean2 = new QpenZipBean<>();
                    qpenZipBean2.setCode("-1");
                    qpenZipBean2.setVersion("1");
                    byte[] bytes = new Gson().toJson(qpenZipBean2).getBytes();

                    DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
                    try {
                        serverSocket.send(packetS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    finish();
                    dismissDialog();
                }

                @Override
                public void cancel() {
                    dismissDialog();
                }
            }, R.string.str_close_tips, R.string.dialog_confirm_text);
        } else {
            QpenZipBean<QpenDataBean> qpenZipBean2 = new QpenZipBean<>();
            qpenZipBean2.setCode("-1");
            qpenZipBean2.setVersion("1");
            byte[] bytes = new Gson().toJson(qpenZipBean2).getBytes();
            DatagramPacket packetS = new DatagramPacket(bytes, bytes.length, serverAddress, 5558);
            try {
                serverSocket.send(packetS);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            backClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
