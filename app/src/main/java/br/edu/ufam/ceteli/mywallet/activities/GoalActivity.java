package br.edu.ufam.ceteli.mywallet.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

public class GoalActivity extends ActionBarActivity {

    public static final String PREF_NAME = "Preferences";
    TextView meta, gasto, saldo, filtro;
    EditText metaEdit;
    float meta1, saldo1, valor;
    int valorSpinner;
    Spinner sp;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        c = Calendar.getInstance();

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this, R.array.categoria_array1, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp = ((Spinner) findViewById(R.id.spinner));
        sp.setAdapter(adapterSpinner);

        filtro = (TextView) findViewById(R.id.textView10);
        meta = (TextView) findViewById(R.id.textView12);
        gasto = (TextView) findViewById(R.id.textView14);
        saldo = (TextView) findViewById(R.id.textView16);

        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
        meta.setText(settings.getString("Meta", ""));
        sp.setSelection(settings.getInt("Filtro", valorSpinner));


        final Button button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog dialog = (AlertDialog) Editar();
                dialog.show();
            }
        });
    }

    public Dialog Editar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("Meta");

        final View v = inflater.inflate(R.layout.dialog_goal_layout, null);

        builder.setView(v).setPositiveButton("Adicionar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                metaEdit = (EditText) v.findViewById(R.id.editText3);

                meta.setText(metaEdit.getText().toString());

                meta1 = Float.valueOf(meta.getText().toString());

                float a = CalcularSaldo(valorSpinner);

                saldo.setText(String.valueOf(a));

                savePreferences("Meta", metaEdit.getText().toString());


            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public void Filtrar(View v){
        savePreferences1("Filtro", valorSpinner);
        Toast.makeText(getApplicationContext(), "Filtrado com sucesso", Toast.LENGTH_SHORT).show();

    }

    private void savePreferences(String key, String value) {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private void savePreferences1(String key, int value) {
        SharedPreferences sp = getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public float CalcularSaldoDia(){
        meta1 = Float.valueOf(meta.getText().toString());
        saldo1 = meta1 - Utils.getSaidaDia(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        return saldo1;
    }

    public float CalcularSaldoMes(){
        meta1 = Float.valueOf(meta.getText().toString());
        saldo1 = meta1 - Utils.getSaidaMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        return saldo1;
    }

    public float CalcularSaldo(int valor){
        meta1 = Float.valueOf(meta.getText().toString());
        if(valor==1) {
            saldo1 = meta1 - Utils.getSaidaDia(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        }
        if(valor==2){
            saldo1 = meta1 - Utils.getSaidaMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        }
        return saldo1;
    }

    public String DicaSaldo(float meta) {

        if (meta < 0) {
            return "Seu saldo esta negativo" + "\n" + "Saldo: " + meta ;
        } else {
            float v = (float) (0.7 * meta);
            float z = (float) (0.8 * meta);
            float y = (float) (0.9 * meta);
            float x = meta;


            if (meta >= v && meta < z) {
                return "Restam cerca de 30% para atingir sua meta." + "\n" + "Saldo: " + meta;
            } else if (meta >= z && meta < y) {
                return "Restam cerca de 20% para atingir sua meta." + "\n" + "Saldo: " + meta;
            } else if (meta >= y && meta<x) {
                return "Restam cerca de 10% para atingir sua meta." + "\n" + "Saldo: " + meta;
            } else if (meta == x) {
                return "Meta alcancada." + "\n" + "Economize seu dinheiro";
            }else
                return "Seguro";

        }
    }

}

