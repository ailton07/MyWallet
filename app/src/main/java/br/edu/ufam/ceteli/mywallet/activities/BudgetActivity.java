package br.edu.ufam.ceteli.mywallet.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.AdapterOrcamento;
import br.edu.ufam.ceteli.mywallet.classes.Entry;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

public class BudgetActivity extends AppCompatActivity {

    float orcamento, bonus, total;
    private AdapterOrcamento adapter1;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        c = Calendar.getInstance();
        Log.i("App123", String.valueOf(c.get(Calendar.MONTH) + 1));
        Log.i("App123", String.valueOf(c.get(Calendar.YEAR)));

        completar();

        List<Entry> valores = Entry.getGeralOrcamento();
        adapter1 = new AdapterOrcamento(this, valores);
        ListView l = (ListView) findViewById(R.id.listView8);
        l.setAdapter(adapter1);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog dialog = (AlertDialog) OnCreateOrcamento();
                dialog.show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
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

    public Dialog OnCreateOrcamento() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        builder.setTitle("orcamento");

        final View v = inflater.inflate(R.layout.dialog_orcamento_layout, null);

        builder.setView(v).setPositiveButton("Adicionar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Entry entry = new Entry();

                EditText orc = (EditText) v.findViewById(R.id.editText);
                EditText bon = (EditText) v.findViewById(R.id.editText2);

                if(orc.getText().toString().equals("")){
                    orcamento = 0;
                }else{
                    orcamento = Float.parseFloat(orc.getText().toString());
                }

                if(bon.getText().toString().equals("")){
                    bonus = 0;
                }else{
                    bonus = Float.parseFloat(bon.getText().toString());
                }

                total = orcamento + bonus;

                entry.setOrcamento(orcamento);
                entry.setBonus(bonus);
                entry.setTotal(total);

                Date data = new Date();

                SimpleDateFormat dateFormat0 = new SimpleDateFormat("yyyyMMdd");
                entry.setDataOrcamento(dateFormat0.format(data).toString());

                entry.save();

                completar();

                adapter1.adiciona(entry);
                adapter1.notifyDataSetChanged();




            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder.create();

    }

    public void completar(){

        TextView a = (TextView) findViewById(R.id.textView8);
        a.setText(String.valueOf(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));

        TextView t = (TextView) findViewById(R.id.textView4);
        t.setText(String.valueOf(Utils.getSaldoOrcamento(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));

        TextView te = (TextView) findViewById(R.id.textView7);
        te.setText(String.valueOf(Utils.getSaldoBonus(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
    }


}