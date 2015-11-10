package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.Calendar;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.budget.fragments.BudgetGraphic;
import br.edu.ufam.ceteli.mywallet.activities.budget.fragments.BudgetReport;
import br.edu.ufam.ceteli.mywallet.activities.dialog.fragments.DialogBudget;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerScrollListener;
import br.edu.ufam.ceteli.mywallet.classes.ViewPagerAdapter;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

/**
 * Created by rodrigo on 29/10/15.
 */
public class BudgetActivity extends Fragment{
    private ViewPagerAdapter fragmentPagerAdapter = null;
    private TabLayout tabLayout = null;
    private static Fragment instance = null;
    private TextView budget, bonus, total;
    Calendar c = Calendar.getInstance();

    public static Fragment getInstance() {
        return (instance == null)? instance = new BudgetActivity() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Orçamento");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().findViewById(R.id.appBarLayout).setElevation(0);
        }

        budget = (TextView) getActivity().findViewById(R.id.tvValueBudget);
        bonus = (TextView) getActivity().findViewById(R.id.tvValueBonus);
        total = (TextView) getActivity().findViewById(R.id.tvValueTotal);

        //budget.setText(String.valueOf(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        //bonus.setText(String.valueOf(Utils.getSaldoBonus(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        //total.setText(String.valueOf(Utils.getOrcamentoTotalMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));

        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        fragmentPagerAdapter.addTab("Gráfico", BudgetGraphic.getInstance(), getResources().getColor(R.color.toolbar_main));
        fragmentPagerAdapter.addTab("Detalhamento", BudgetReport.getInstance(), getResources().getColor(R.color.toolbar_main));

        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener());

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabBudget);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fabNewBudget = (FloatingActionButton) view.findViewById(R.id.fabNewBudget);
        fabNewBudget.setOnClickListener(fabNew());
    }

    private View.OnClickListener fabNew(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBudget dialogBudget = new DialogBudget();
                dialogBudget.show(getFragmentManager(), null);
            }
        };
    }

    private ViewPager.OnPageChangeListener pageChangeListener(){
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    RecyclerScrollListener.resetScrollingView();
                } else {
                    RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
                    if(recyclerView != null) {
                        recyclerView.scrollToPosition(0);
                    } else {
                        RecyclerScrollListener.resetScrollingView();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }
}
