package br.edu.ufam.ceteli.mywallet.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;


/**
 * Created by AiltonFH on 26/08/2015.
 */
public class AdapterListView extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<Entrada> itens;

    public AdapterListView(Context context, List<Entrada> itens)
    {
        //Itens que preencheram o listview
        this.itens = itens;
        //responsavel por pegar o Layout do item.
        mInflater = LayoutInflater.from(context);
    }

    public void add(Entrada entrada){

        itens.add(entrada);

    }

    public void remove(Entrada entrada){

        itens.remove(entrada);

    }
    /**
     * Retorna a quantidade de itens
     *
     * @return
     */
    public int getCount()
    {
        return itens.size();
    }

    /**
     * Retorna o item de acordo com a posicao dele na tela.
     *
     * @param position
     * @return
     */
    public Entrada getItem(int position)
    {
        return itens.get(position);
    }

    /**
     * Sem implementação
     *
     * @param position
     * @return
     */
    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        //Pega o item de acordo com a posção.
        Entrada item = itens.get(position);
        //infla o layout para podermos preencher os dados
        view = mInflater.inflate(R.layout.item_listview, null);

        //atravez do layout pego pelo LayoutInflater, pegamos cada id relacionado
        //ao item e definimos as informações.

        if(item.getTipo() == 0) {
            ((TextView) view.findViewById(R.id.text)).setText(item.toString1());
        }else{
            ((TextView) view.findViewById(R.id.text)).setText(item.toString());
        }
        if(item.getTipo()==0) {
            ((ImageView) view.findViewById(R.id.imagemview)).setImageResource(R.drawable.mais_small);
        }
        else{
            ((ImageView) view.findViewById(R.id.imagemview)).setImageResource(R.drawable.menos_small);
        }

        return view;
    }
}
