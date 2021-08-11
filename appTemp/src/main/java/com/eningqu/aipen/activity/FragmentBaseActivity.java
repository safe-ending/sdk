package com.eningqu.aipen.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;

import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.fragment.MainFragment;
import com.eningqu.aipen.fragment.PageDrawFragment;

import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/8/6 11:17
 * desc   : 装载Fragment的base activity
 * version: 1.0
 */
public abstract class FragmentBaseActivity extends DrawBaseActivity {
    //由于有些跳转无需参数,所以这里无需抽象方法
    protected void handleIntent(Intent intent) {
    }

    protected abstract BaseFragment getFirstFragment();

    protected abstract int getFragmentContainerId();

    protected PageDrawFragment pageDrawFragment;
    protected MainFragment mainFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //写死竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //处理Intent(主要用来获取其中携带的参数)
        if (getIntent() != null) {
            handleIntent(getIntent());
        }

        //添加栈底的第一个fragment
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (getFirstFragment() != null) {
                //                pushFragment(getFirstFragment());
                switchFragment(getFirstFragment());
            } else {
                throw new NullPointerException("getFirstFragment() cannot be null");
            }
        }
    }

    public Fragment getFragment(@NonNull Class<?> clz) {

        String clzName = clz.getName();
        if (clzName.equals(MainFragment.class.getName())) {
            if (null == mainFragment) {
                mainFragment = new MainFragment();
            }
            return mainFragment;
        }
        if (clzName.equals(PageDrawFragment.class.getName())) {
            if (null == pageDrawFragment) {
                pageDrawFragment = new PageDrawFragment();
            }
            return pageDrawFragment;
        }
        return null;
    }

    protected Fragment currentFragment;

    public FragmentTransaction switchFragment(@NonNull Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            try {
                transaction.add(getFragmentContainerId(), targetFragment, targetFragment.getClass().getName());
                //            transaction.addToBackStack(targetFragment.getClass().getName());
            } catch (IllegalStateException e) {
                transaction.show(targetFragment);
            }
            transaction.commitAllowingStateLoss();
        } else {
            if (null != currentFragment) {
                if (currentFragment != targetFragment) {
                    transaction.hide(currentFragment).show(targetFragment);
                } else {
                    transaction.show(targetFragment);
                }
            }
            transaction.commitAllowingStateLoss();
        }
        currentFragment = targetFragment;
        getSupportFragmentManager().executePendingTransactions();//针对可能add两次相同对象造成崩溃的修复
        return transaction;
    }

    public void hideFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (null != fragment && fragment.isAdded()) {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    public void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (null != fragment && fragment.isAdded()) {
            transaction.show(fragment);
        }
        transaction.commit();
    }

    public void pushFragment(BaseFragment fragment) {
        if (fragment != null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            /*for(Fragment fragment1:fragments){
                if(fragment.getClass().getSimpleName().equals(fragment1.getClass().getSimpleName())){
                    getSupportFragmentManager().beginTransaction().remove(fragment1);
                }
            }*/
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContainerId(), fragment)
                    .addToBackStack(((Object) fragment).getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            //            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            //            for(Fragment fragment1:fragments){
            //                if(fragment.getClass().getSimpleName().equals(fragment1.getClass().getSimpleName())){
            //                    return;
            //                }
            //            }
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContainerId(), fragment)
                    .addToBackStack(((Object) fragment).getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    public void popFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public Fragment getTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getFirstFragment() != null) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                return fragments.get(fragments.size() - 1);
            } else {
                throw new NullPointerException("getFirstFragment() cannot be null");
            }
        }
        return null;
    }

    public void toGotoActivity(Class<?> clz, Bundle bundle) {
        gotoActivity(clz, bundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = super.getSupportParentActivityIntent();
        if (intent == null) {
            finish();
        }
        return intent;
    }

    //回退键处理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                popFragment();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainFragment = null;
        pageDrawFragment = null;
        currentFragment = null;
    }
}
