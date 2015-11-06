package br.edu.ufam.ceteli.mywallet.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rodrigo on 03/11/15.
 */
public class BudgetGraphic extends Fragment {
    private static Fragment instance = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new BudgetGraphic() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
