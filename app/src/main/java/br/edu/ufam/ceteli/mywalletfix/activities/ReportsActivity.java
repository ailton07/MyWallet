package br.edu.ufam.ceteli.mywalletfix.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.edu.ufam.ceteli.mywalletfix.R;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.EarningReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.FoodReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.GeneralReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.GraphicReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.HomeReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.LoungeReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.OthersReport;
import br.edu.ufam.ceteli.mywalletfix.activities.reports.SpedingReport;
import br.edu.ufam.ceteli.mywalletfix.classes.ViewPagerAdapter;

public class ReportsActivity extends AppCompatActivity {
    ViewPagerAdapter fragmentPagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.addTab("Grafico", new GraphicReport());
        fragmentPagerAdapter.addTab("Geral", new GeneralReport());
        fragmentPagerAdapter.addTab("Entrada", new EarningReport());
        fragmentPagerAdapter.addTab("Saída", new SpedingReport());
        fragmentPagerAdapter.addTab("Casa", new HomeReport());
        fragmentPagerAdapter.addTab("Alimentícios", new FoodReport());
        fragmentPagerAdapter.addTab("Entreterimento", new LoungeReport());
        fragmentPagerAdapter.addTab("Outros gastos", new OthersReport());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReports);
        setSupportActionBar(toolbar);
        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabReports);
        tabLayout.setupWithViewPager(viewPager);
    }
}
