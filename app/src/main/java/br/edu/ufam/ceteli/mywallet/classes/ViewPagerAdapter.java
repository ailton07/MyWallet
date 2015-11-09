package br.edu.ufam.ceteli.mywallet.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by rodrigo on 29/09/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Integer> tabColors = null;
    private ArrayList<String> tabTitles = null;
    private ArrayList<Fragment> tabFragments = null;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        tabTitles = new ArrayList<>();
        tabFragments = new ArrayList<>();
        tabColors = new ArrayList<>();
    }

    public void addTab(String tabName, Fragment fragment, int color){
        tabTitles.add(tabName);
        tabFragments.add(fragment);
        tabColors.add(color);
    }

    public int getColor(int position){
        return tabColors.get(position);
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
