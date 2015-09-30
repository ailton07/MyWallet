package br.edu.ufam.ceteli.mywallet.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.EntryReport;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.FoodReport;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.GeneralReport;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.HomeReport;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.LoungeReport;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.OthersReport;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.OutReport;
import br.edu.ufam.ceteli.mywallet.Classes.ViewPagerAdapter;
import br.edu.ufam.ceteli.mywallet.R;

public class ReportsActivity extends AppCompatActivity {
    ViewPagerAdapter fragmentPagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);

        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentPagerAdapter.addTab("Geral", new GeneralReport());
        fragmentPagerAdapter.addTab("Entrada", new EntryReport());
        fragmentPagerAdapter.addTab("Saída", new OutReport());
        fragmentPagerAdapter.addTab("Casa", new HomeReport());
        fragmentPagerAdapter.addTab("Alimentícios", new FoodReport());
        fragmentPagerAdapter.addTab("Entreterimento", new LoungeReport());
        fragmentPagerAdapter.addTab("Outros gastos", new OthersReport());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReports);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabReports);
        tabLayout.setupWithViewPager(viewPager);
    }
}
