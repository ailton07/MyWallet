package br.edu.ufam.ceteli.mywallet.activities.reports;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;

public class HomeReport extends Fragment {
    private AdapterListView adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_report, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Entrada> valores = Entrada.getCategoriaCasa();

        adapter = new AdapterListView(getActivity(), valores);
        ListView lista = (ListView) getActivity().findViewById(R.id.listView4);
        lista.setAdapter(adapter);
    }
}
