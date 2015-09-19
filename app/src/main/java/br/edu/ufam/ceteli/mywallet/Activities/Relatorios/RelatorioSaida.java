package br.edu.ufam.ceteli.mywallet.Activities.Relatorios;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.R;

public class RelatorioSaida extends ActionBarActivity {

    AdapterListView adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_saida);

        List<Entrada> valores = Entrada.getSaida();
        adapter3 = new AdapterListView(this, valores);
        ListView lista = (ListView) findViewById(R.id.listView3);
        lista.setAdapter(adapter3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_relatorio_saida, menu);
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
}