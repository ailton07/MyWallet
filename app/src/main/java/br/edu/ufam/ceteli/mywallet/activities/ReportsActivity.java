package br.edu.ufam.ceteli.mywallet.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

public class ReportsActivity extends AppCompatActivity {
    ViewPagerAdapter fragmentPagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.addTab("Grafico", new GraphicReport());
        fragmentPagerAdapter.addTab("Geral", new GeneralReport());
        fragmentPagerAdapter.addTab("Ganhos", new EarningReport());
        fragmentPagerAdapter.addTab("Gastos", new SpedingReport());
        fragmentPagerAdapter.addTab("Residencia", new HomeReport());
        fragmentPagerAdapter.addTab("Alimentícios", new FoodReport());
        fragmentPagerAdapter.addTab("Entretenimento", new LoungeReport());
        fragmentPagerAdapter.addTab("Transporte", new MoveReport());
        fragmentPagerAdapter.addTab("Outros gastos", new OthersReport());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReports);
        setSupportActionBar(toolbar);
        getDelegate().getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabReports);
        tabLayout.setupWithViewPager(viewPager);
    }
}
