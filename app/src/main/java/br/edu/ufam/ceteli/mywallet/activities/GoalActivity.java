package br.edu.ufam.ceteli.mywallet.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    float meta1, saldo1;
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

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valorSpinner = sp.getSelectedItemPosition();
                float valor = 0;

                if(valorSpinner==0){
                    filtro.setText("Sem nenhum filtro");
                    gasto.setText("0.0");
                    saldo.setText("0.0");
                }
                if(valorSpinner==1){
                    filtro.setText("Planejamento do dia");
                    gasto.setText(String.valueOf(Utils.getSaidaDia(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
                    valor = CalcularSaldoDia();
                }
                if(valorSpinner==2){
                    filtro.setText("Planejamento do Mês");
                    gasto.setText(String.valueOf(Utils.getSaidaMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
                    valor = CalcularSaldoMes();
                }

                saldo.setText(String.valueOf(valor));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goal, menu);
        return true;
    }

    public void onResume(){
        super.onResume();

        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
        meta.setText(settings.getString("Meta", ""));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

}

