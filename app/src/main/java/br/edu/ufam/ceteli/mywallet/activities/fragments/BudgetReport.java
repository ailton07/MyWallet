package br.edu.ufam.ceteli.mywallet.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.DividerItemDecoration;
import br.edu.ufam.ceteli.mywallet.classes.Entry;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerScrollListener;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerViewBudgetAdapter;

/**
 * Created by rodrigo on 03/11/15.
 */
public class BudgetReport extends Fragment{
    private static Fragment instance = null;
    private AppBarLayout appBarLayout = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new BudgetReport() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPadding(recyclerView.getPaddingLeft(), (int) ((170) * getResources().getDisplayMetrics().density + 0.5f), recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(RecyclerViewBudgetAdapter.getInstance(Entry.getGeralOrcamento()));
        recyclerView.addOnScrollListener(RecyclerScrollListener.getInstance(appBarLayout, getActivity().findViewById(R.id.appBarBudget)));
        return recyclerView;
    }
}
