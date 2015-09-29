package br.edu.ufam.ceteli.mywallet.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by Asus on 25/09/2015.
 */
public class AdapterListViewOrcamento extends BaseAdapter{

    private LayoutInflater mInflater;
    private List<Entry> itens1;

    public AdapterListViewOrcamento(Context context, List<Entry> itens1) {

        this.itens1 = itens1;
        mInflater = LayoutInflater.from(context);
    }

    public void add(Entry entry) {

        itens1.add(entry);
    }

    public int getCount()
    {
        return itens1.size();
    }

    public Entry getItem(int position)
    {
        return itens1.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
