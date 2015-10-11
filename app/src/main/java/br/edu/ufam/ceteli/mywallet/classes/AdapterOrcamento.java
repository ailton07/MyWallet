package br.edu.ufam.ceteli.mywallet.classes;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by Asus on 04/10/2015.
 */
public class AdapterOrcamento extends BaseAdapter {
    private LayoutInflater inf;
    private List<Entry> itens;

    public AdapterOrcamento(Context context, List<Entry> itens){

        this.itens = itens;
        inf = LayoutInflater.from(context);
    }

    public void adiciona(Entry entry){
        itens.add(entry);
    }

    public void remove(Entry entry){
        itens.remove(entry);
    }


    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public Entry getItem(int position) {
        return itens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Entry item = itens.get(position);
        view = inf.inflate(R.layout.adapter_orcamento_layout, null);

        ((TextView) view.findViewById(R.id.textView9)).setText(item.toString());

        return view;
    }
}
