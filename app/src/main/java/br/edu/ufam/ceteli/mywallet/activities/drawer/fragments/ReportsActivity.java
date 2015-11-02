package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.reports.AllReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.EarningReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.FoodReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.GraphicReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.HealthReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.HomeReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.LoungeReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.MoveReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.OthersReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.SpedingReport;
import br.edu.ufam.ceteli.mywallet.classes.ViewPagerAdapter;

public class ReportsActivity extends Fragment {
    private ViewPagerAdapter fragmentPagerAdapter = null;
    private int previoustPage = 0;
    private TabLayout tabLayout = null;
    private static Fragment instance = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new ReportsActivity() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        fragmentPagerAdapter.addTab("Grafico", GraphicReport.getInstance(), getResources().getColor(R.color.toolbar_graphics));
        fragmentPagerAdapter.addTab("Todos", AllReport.getInstance(), getResources().getColor(R.color.toolbar_all));
        fragmentPagerAdapter.addTab("Ganhos", EarningReport.getInstance(), getResources().getColor(R.color.toolbar_earning));
        fragmentPagerAdapter.addTab("Gastos", SpedingReport.getInstance(), getResources().getColor(R.color.toolbar_spending));
        fragmentPagerAdapter.addTab("Residencia", HomeReport.getInstance(), getResources().getColor(R.color.toolbar_home));
        fragmentPagerAdapter.addTab("Alimentícios", FoodReport.getInstance(), getResources().getColor(R.color.toolbar_food));
        fragmentPagerAdapter.addTab("Entretenimento", LoungeReport.getInstance(), getResources().getColor(R.color.toolbar_lounge));
        fragmentPagerAdapter.addTab("Transporte", MoveReport.getInstance(), getResources().getColor(R.color.toolbar_transport));
        fragmentPagerAdapter.addTab("Saúde", HealthReport.getInstance(), getResources().getColor(R.color.toolbar_health));
        fragmentPagerAdapter.addTab("Outros gastos", OthersReport.getInstance(), getResources().getColor(R.color.toolbar_others));

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener());

        tabLayout = (TabLayout) view.findViewById(R.id.tabReports);
        tabLayout.setupWithViewPager(viewPager);

        // Ao criar, seta cores
        tabLayout.setBackgroundColor(getResources().getColor(R.color.toolbar_graphics));

        // Pega Toolbar
        getActivity().findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.toolbar_graphics));
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Relatórios");

    }

    private ViewPager.OnPageChangeListener pageChangeListener(){
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    resetToolbarScrool();
                }
                int currentColor = fragmentPagerAdapter.getColor(position);
                int previousColor = fragmentPagerAdapter.getColor(previoustPage);
                ValueAnimator colorToolbar= ObjectAnimator.ofInt(getActivity().findViewById(R.id.toolbar), "backgroundColor", previousColor, currentColor);
                ValueAnimator colorPager = ObjectAnimator.ofInt(tabLayout, "backgroundColor", previousColor, currentColor);
                colorToolbar.setDuration(350);
                colorPager.setDuration(350);
                colorToolbar.setEvaluator(new ArgbEvaluator());
                colorPager.setEvaluator(new ArgbEvaluator());
                colorToolbar.start();
                colorPager.start();
                previoustPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    private void resetToolbarScrool(){
        // Reseta Scroll
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
        behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, -2000, true);
    }
}
