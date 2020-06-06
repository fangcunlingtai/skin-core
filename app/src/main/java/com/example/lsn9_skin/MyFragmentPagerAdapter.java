package com.example.lsn9_skin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> mTitles;
    private List<Fragment> mFragments;

    public MyFragmentPagerAdapter(FragmentManager supportFragmentManager, int behavior, List<Fragment> fragments, List<String> listTitle) {
        super(supportFragmentManager,behavior);
        mTitles = listTitle;
        mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
