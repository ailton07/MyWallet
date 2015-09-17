package br.edu.ufam.ceteli.mywallet.Activities.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by AiltonFH on 17/09/2015.
 */
public class DialogIn {
    Context context;
    int radioClicado;
    String categoriaSpinnerSelecionado="";
    AlertDialog.Builder builder;
    // Get the layout inflater
    LayoutInflater inflater;
    AdapterListView adapter;


    public DialogIn(Context context, int radio, String categoriaSpinnerSelecionado, AlertDialog.Builder builder, LayoutInflater inflater, AdapterListView adapter){
        this.context = context;
        this.radioClicado = radio;
        this.categoriaSpinnerSelecionado = categoriaSpinnerSelecionado;
        this.builder = builder;
        this.inflater = inflater;
        this.adapter = adapter;


    }



    public AlertDialog gera(){


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Referencias:  http://stackoverflow.com/questions/30032005/access-buttons-in-custom-alert-dialog
        final View v = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Adicionar ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Entrada entrada = new Entrada();

                        //Tipo
                        entrada.setTipo(radioClicado);

                        //Descrição
                        //TextView descricao = (TextView) findViewById(R.id.descricao);
                        TextView descricao = (TextView) v.findViewById(R.id.descricao);
                        entrada.setDescricao(descricao.getText().toString());

                        //Valor
                        TextView valor = (TextView) v.findViewById(R.id.valor);
                        entrada.setValor(Float.parseFloat(valor.getText().toString()));

                        //Estabelecimento
                        TextView estabelecimento = (TextView) v.findViewById(R.id.estabelecimento);
                        entrada.setEstabelecimento(estabelecimento.getText().toString());

                        //Categoria
                        //TextView categoria = (TextView) v.findViewById(R.id.categoria);
                        //entrada.setCategoria(categoria.getText().toString());
                        Spinner categoria = (Spinner) v.findViewById(R.id.categoria);
                        entrada.setCategoria(categoriaSpinnerSelecionado);

                        //Data de Inserção
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                        Date date = new Date();

                        //entrada.setDataInsercao(dateFormat.format(date).toString());

                        //Data de Compra

                        DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
                        dataCompra.setCalendarViewShown(false);

                        Date dateDataCompra = new Date();
                        dateDataCompra.setDate(dataCompra.getDayOfMonth());
                        dateDataCompra.setMonth(dataCompra.getMonth());
                        dateDataCompra.setYear(dataCompra.getYear()-1900); // http://stackoverflow.com/questions/9751050/simpledateformat-subclass-adds-1900-to-the-year


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        entrada.setDataCompra(dateFormat.format(dateDataCompra).toString());

                        entrada.save();

                        adapter.add(entrada);
                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("Cancelar ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  LoginDialogFragment.this.getDialog().cancel();
                        dialog.cancel();
                    }
                });


        // Spinner configuration
        Spinner spinner = (Spinner) v.findViewById(R.id.categoria);
        ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(context,
                R.array.categoria_array, android.R.layout.simple_spinner_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterS);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);

        DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
        dataCompra.setCalendarViewShown(false);

        return builder.create();

    }
}
