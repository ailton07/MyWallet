package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by rodrigo on 29/10/15.
 */
public class BudgetActivity extends Fragment{
    private static Fragment instance = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new BudgetActivity() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
