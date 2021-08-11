package com.eningqu.aipen.base.ui;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.common.dialog.BaseDialog;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/18 14:23
 */

public abstract class BaseFragment extends Fragment {

    private static String TAG = BaseFragment.class.getSimpleName();
    private static String TAG_UI = "UI-Fragment";

    private FragmentActivity activity;
    protected BaseDialog dialog;

    private View rootView;

    /**
     * 保存数据
     */
    private Bundle mSavedState;

    /**
     * 视图是否已经初始化完毕
     */
    private boolean isViewReady;

    /**
     * fragment是否处于可见状态
     */
    private boolean isFragmentVisible;

    /**
     * 是否已经加载过
     */
    protected boolean isLoaded;

    protected boolean isHidden;//是否可见

    /**
     * 设置Fragment可见或者不可见时会调用此方法。在该方法里面可以通过调用getUserVisibleHint()获得
     * Fragment的状态是可见还是不可见的，如果可见则进行懒加载操作
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        //        System.out.println("===========setUserVisibleHint============");
        isFragmentVisible = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);
        L.info(TAG_UI, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser + " " + this.getClass().getName());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        L.info(TAG_UI, "onHiddenChanged hidden=" + hidden + " " + this.getClass().getName());
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onAttach(Context context) {
        //        System.out.println("==========onAttach=============");
        super.onAttach(context);
        L.info(TAG_UI, "onAttach" + " " + this.getClass().getName());
    }

    /**
     * 初始化Fragment  可通过参数savedInstanceState获取之前保存的值
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //        System.out.println("===========onCreate============");
        super.onCreate(savedInstanceState);
        L.info(TAG_UI, "onCreate" + " " + this.getClass().getName());
        eventBusRegister(this);
    }

    /**
     * 初始化Fragment的布局。加载布局和findViewById的操作通常在此函数内完成，但是不建议执行耗时的操作，比如读取数据库数据列表
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //        System.out.println("===========onCreateView============");
        L.info(TAG_UI, "onCreateView" + " " + this.getClass().getName());
        if (rootView == null) {
            //            rootView = inflater.inflate(setLayout(), container, false);
            ViewDataBinding viewDataBinding = DataBindingUtil.inflate(inflater, setLayout(), container, false);
            dataBindingLayout(viewDataBinding);
            rootView = viewDataBinding.getRoot();
        }
        //        initView();
        return rootView;
    }

    /**
     * 执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回，
     * 在该方法内可以进行与Activity交互的UI操作，所以在该方法之前Activity的onCreate方法并未执行完成，如果提前进行交互操作，会引发空指针异常
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //        System.out.println("==========onActivityCreated=============");
        //视图准备完毕
        isViewReady = true;
        //如果视图准备完毕且Fragment处于可见状态，则开始初始化操作
        if (isViewReady && isFragmentVisible) {
            onFragmentVisiable();
        } else {
            initData();
            initView();
        }

        //如果之前有保存数据，则恢复数据
        //restoreStateFromArguments();
        super.onActivityCreated(savedInstanceState);
        L.info(TAG_UI, "onActivityCreated" + " " + this.getClass().getName());
    }

    @Override
    public void onResume() {
        //        System.out.println("===========onResume============");
        super.onResume();
        L.info(TAG_UI, "onResume" + " " + this.getClass().getName());
    }

    @Override
    public void onPause() {
        //        System.out.println("===========onPause============");
        super.onPause();
        L.info(TAG_UI, "onPause" + " " + this.getClass().getName());
    }

    /**
     * 执行该方法时，Fragment完全不可见
     */
    @Override
    public void onStop() {
        //        System.out.println("===========onStop============");
        super.onStop();
        L.info(TAG_UI, "onStop" + " " + this.getClass().getName());
    }

    /**
     * 销毁与Fragment有关的视图，但未与Activity解除绑定，依然可以通过onCreateView方法重新创建视图。
     * 通常在ViewPager+Fragment的方式下会调用此方法
     */
    @Override
    public void onDestroyView() {
        //        System.out.println("===========onDestroyView============");
        super.onDestroyView();
        rootView = null;
        L.info(TAG_UI, "onDestroyView" + " " + this.getClass().getName());
    }

    /**
     * 销毁Fragment 通常按Back键退出或者Fragment被回收时调用此方法
     */
    @Override
    public void onDestroy() {
        //        System.out.println("============onDestroy===========");
        super.onDestroy();
        L.info(TAG_UI, "onDestroy" + " " + this.getClass().getName());
        eventBusUnRegister(this);
    }

    /**
     * 解除与Activity的绑定。在onDestroy方法之后调用
     */
    @Override
    public void onDetach() {
        //        System.out.println("============onDetach===========");
        super.onDetach();
        L.info(TAG_UI, "onDetach" + " " + this.getClass().getName());
    }

    private void onFragmentVisiable() {
        if (!isLoaded) {
            isLoaded = true;
            initData();
            initView();
            isLoaded = false;
        }
    }

    /**
     * 设置布局
     */
    protected abstract int setLayout();

    protected abstract void dataBindingLayout(ViewDataBinding viewDataBinding);

    /**
     * 视图相关的初始化
     */
    protected abstract void initView();

    /**
     * 数据的初始化
     */
    protected abstract void initData();

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected void showToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(message);
            }
        });
    }

    protected void showToast(final int message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(message);
            }
        });
    }

    protected void eventBusRegister(Object subscriber){
        L.info("register :"+subscriber.getClass().getName());
        EventBusUtil.register(subscriber);
    }

    protected void eventBusUnRegister(Object subscriber){
        L.info("unregister :"+subscriber.getClass().getName());
        EventBusUtil.unregister(subscriber);
    }
}
