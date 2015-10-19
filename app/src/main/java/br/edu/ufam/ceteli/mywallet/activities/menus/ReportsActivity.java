package br.edu.ufam.ceteli.mywallet.activities.menus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.reports.EarningReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.FoodReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.GeneralReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.GraphicReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.HomeReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.LoungeReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.MoveReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.OthersReport;
import br.edu.ufam.ceteli.mywallet.activities.reports.SpedingReport;
import br.edu.ufam.ceteli.mywallet.classes.ViewPagerAdapter;

public class ReportsActivity extends Fragment {
    ViewPagerAdapter fragmentPagerAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        fragmentPagerAdapter.addTab("Grafico", new GraphicReport());
        fragmentPagerAdapter.addTab("Geral", new GeneralReport());
        fragmentPagerAdapter.addTab("Ganhos", new EarningReport());
        fragmentPagerAdapter.addTab("Gastos", new SpedingReport());
        fragmentPagerAdapter.addTab("Residencia", new HomeReport());
        fragmentPagerAdapter.addTab("Alimentícios", new FoodReport());
        fragmentPagerAdapter.addTab("Entretenimento", new LoungeReport());
        fragmentPagerAdapter.addTab("Transporte", new MoveReport());
        fragmentPagerAdapter.addTab("Outros gastos", new OthersReport());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabReports);
        tabLayout.setupWithViewPager(viewPager);
    }
}