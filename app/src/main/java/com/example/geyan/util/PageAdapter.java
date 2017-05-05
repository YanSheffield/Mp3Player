package com.example.geyan.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.geyan.mp3player.RemoteTab;
import com.example.geyan.mp3player.LocalTab;

/**
 * Created by geyan on 05/05/2017.
 */

public class PageAdapter extends FragmentStatePagerAdapter {
    int nNumberTabs;
    public PageAdapter(FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.nNumberTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RemoteTab tab1 = new RemoteTab();
                return tab1;
            case 1:
                LocalTab tab2 = new LocalTab();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return nNumberTabs;
    }
}
