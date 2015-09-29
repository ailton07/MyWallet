package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufam.ceteli.mywallet.Activities.Dialogs.DialogOrcamento;
import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.AdapterListViewOrcamento;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.Classes.Entry;
import br.edu.ufam.ceteli.mywallet.R;

public class OrcamentoActivity extends ActionBarActivity {

    Entry entrada;
    AdapterListViewOrcamento adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);

        //TextView t = (TextView) findViewById(R.id.textView9);
        //t.setText(String.valueOf(entrada.getOrcamento()));

        //TextView te = (TextView) findViewById(R.id.textView7);
        //te.setText(String.valueOf(entrada.getBonus()));

        List<Entry> valores = Entry.getGeralOrcamento();
        adapter = new AdapterListViewOrcamento(this, valores);
        ListView l = (ListView) findViewById(R.id.listView8);
        l.setAdapter(adapter);

        final Button button = (Button) findViewById(R.id.button10);
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
        getMenuInflater().inflate(R.menu.menu_orcamento, menu);
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

        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle("orcamento");

        DialogOrcamento d = new DialogOrcamento(this, builder, inflater, adapter);

        return d.geraOrcamento();
    }

}
