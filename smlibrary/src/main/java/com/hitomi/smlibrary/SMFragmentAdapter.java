package com.hitomi.smlibrary;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;


import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/8 10:30
 * desc   :
 * version: 1.0
 */
public class SMFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;
    private List<CharSequence> mTitles = new ArrayList<>();
    public SMFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        mFragments = list;
    }

    public void setFragments(List<Fragment> list){
        if(null!=list && list.size()>0){
            mFragments.clear();
            mFragments.addAll(list);
        }
    }

    public void addFrament(Fragment fragment){
        mFragments.add(fragment);
    }
    public void addFrament(int position, Fragment fragment){
        mFragments.add(position, fragment);
    }
    public void removeFragment(int position){
        if(position<mFragments.size()){
            mFragments.remove(position);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
