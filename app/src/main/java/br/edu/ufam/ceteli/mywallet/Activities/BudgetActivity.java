package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.edu.ufam.ceteli.mywallet.Classes.Entry;
import br.edu.ufam.ceteli.mywallet.R;

public class BudgetActivity extends AppCompatActivity {

    float orcamento, bonus, total, valor, valor1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

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

                EditText orc = (EditText) v.findViewById(R.id.editText2);
                valor = Float.parseFloat(orc.getText().toString());

                EditText orc1 = (EditText) v.findViewById(R.id.editText);
                valor1 = Float.parseFloat(orc1.getText().toString());

                orcamento = orcamento + valor;
                bonus = bonus + valor1;
                total = orcamento + bonus;

                Log.i("App123", String.valueOf(orcamento));
                Log.i("App123", String.valueOf(bonus));
                Log.i("App123", String.valueOf(total));

                entry.setOrcamento(valor);
                entry.setBonus(valor1);
                entry.setTotal(total);

                //Toast.makeText(getApplicationContext(), orc.getText().toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), orc1.getText().toString(), Toast.LENGTH_SHORT).show();

                entry.save();

                //adapter.add(entry);
                //adapter.notifyDataSetChanged();


                Log.i("App123", "Inserido no bd com sucesso");

                TextView t = (TextView) findViewById(R.id.textView4);
                t.setText(String.valueOf(orcamento));

                TextView te = (TextView) findViewById(R.id.textView7);
                te.setText(String.valueOf(bonus));

                TextView a = (TextView) findViewById(R.id.textView8);
                a.setText(String.valueOf(total));

                //Toast.makeText(getApplicationContext(), "inserido no bd com sucesso", Toast.LENGTH_SHORT).show();
                //adapter.add(entry);
                //adapter.notifyDataSetChanged();


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
