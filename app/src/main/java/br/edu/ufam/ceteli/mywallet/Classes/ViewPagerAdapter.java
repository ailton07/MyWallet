package br.edu.ufam.ceteli.mywallet.Classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by rodrigo on 29/09/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<String> tabTitles = null;
    private ArrayList<Fragment> tabFragments = null;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        tabTitles = new ArrayList<>();
        tabFragments = new ArrayList<>();
    }

    public void addTab(String tabName, Fragment fragment){
        tabTitles.add(tabName);
        tabFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
