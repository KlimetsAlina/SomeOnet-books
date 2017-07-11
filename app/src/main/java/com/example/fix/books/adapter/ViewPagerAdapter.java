package com.example.fix.books.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.fix.books.R;
import com.example.fix.books.fragment.BooksFragment_;
import com.example.fix.books.fragment.MapFragment_;
import com.example.fix.books.fragment.PracticeFragment_;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public static final int items = 3;

    private Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BooksFragment_();
            case 1:
                return new PracticeFragment_();
            default:
                return new BooksFragment_();
        }
    }

    @Override
    public int getCount() {
        return items;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.books);
            case 1:
                return mContext.getString(R.string.magazine);
            default:
                return "";
        }
    }
}
