package br.edu.ufam.ceteli.mywallet.Activities.Relatorios;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.R;

public class LoungeReport extends Fragment {
    private AdapterListView adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_relatorio_lazer, container, false);
        List<Entrada> valores = Entrada.getCategoriaLazer();

        adapter = new AdapterListView(getActivity(), valores);
        ListView lista = (ListView) view.findViewById(R.id.listView6);
        lista.setAdapter(adapter);

        return view;
    }
}
