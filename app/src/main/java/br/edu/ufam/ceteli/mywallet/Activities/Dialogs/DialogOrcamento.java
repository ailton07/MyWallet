package br.edu.ufam.ceteli.mywallet.Activities.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.AdapterListViewOrcamento;
import br.edu.ufam.ceteli.mywallet.Classes.Entry;
import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by Asus on 25/09/2015.
 */
public class DialogOrcamento {

    float orcamento = 0;
    float bonus = 0;
    Context context;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    AdapterListViewOrcamento adapter;

    public DialogOrcamento(Context context, AlertDialog.Builder builder, LayoutInflater inflater, AdapterListViewOrcamento adapter) {
        this.context = context;
        this.builder = builder;
        this.inflater = inflater;
        this.adapter = adapter;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AlertDialog geraOrcamento() {

        final View v = inflater.inflate(R.layout.orcamento_dialog_layout, null);

        builder.setView(v).setPositiveButton("Adicionar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Entry entry = new Entry();

                EditText orc = (EditText) v.findViewById(R.id.editText);
                float valor = Float.parseFloat(orc.getText().toString());

                EditText orc1 = (EditText) v.findViewById(R.id.editText2);
                float valor1 = Float.parseFloat(orc1.getText().toString());

                orcamento = orcamento + valor;
                bonus = bonus + valor1;

                entry.setOrcamento(valor);
                entry.setBonus(valor1);

                //Toast.makeText(getApplicationContext(), orc.getText().toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), orc1.getText().toString(), Toast.LENGTH_SHORT).show();

                Log.i("App123", String.valueOf(orcamento));
                Log.i("App123", String.valueOf(bonus));

                adapter.add(entry);
                adapter.notifyDataSetChanged();

                //entry.save();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder.create();
    }


}
