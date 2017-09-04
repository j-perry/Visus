package com.visus.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.visus.ui.settings.fragments.AboutFragment;
import com.visus.ui.settings.fragments.GeneralFragment;

/**
 * Created by jonathanperry on 22/07/2017.
 */

public final class SettingsPagerAdapter extends FragmentPagerAdapter {

    private int userId;
    private Bundle bundle;
    private int NO_FRAGMENTS = 2;

    public SettingsPagerAdapter(FragmentManager fm, int userId) {
        super(fm);
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int item) {
        switch(item) {
            case 0:
                bundle = new Bundle();
                bundle.putInt("userId", userId);
                return new GeneralFragment();
            case 1:
                return new AboutFragment();
            default:
                Fragment f = new Fragment();
                return f;
        }
    }

    @Override
    public int getCount() {
        return NO_FRAGMENTS;
    }
}