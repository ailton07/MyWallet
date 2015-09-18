package br.edu.ufam.ceteli.mywallet.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioCasa;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioEntradas;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioGeral;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioLazer;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioOcasional;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioRestaurante;
import br.edu.ufam.ceteli.mywallet.Activities.Relatorios.RelatorioSaida;
import br.edu.ufam.ceteli.mywallet.R;

public class RelatoriosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_relatorios, menu);
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

    public void RelatorioGeral(View view){
        Intent a = new Intent(this, RelatorioGeral.class);
        startActivity(a);
    }
    public void RelatorioEntrada(View view){
        Intent d = new Intent(this, RelatorioEntradas.class);
        startActivity(d);
    }
    public void RelatorioSaida(View view){
        Intent e = new Intent(this, RelatorioSaida.class);
        startActivity(e);
    }
    public void RelatorioCasa(View view){
        Intent f = new Intent(this, RelatorioCasa.class);
        startActivity(f);
    }
    public void RelatorioRestaurante(View view){
        Intent g = new Intent(this, RelatorioRestaurante.class);
        startActivity(g);
    }
    public void RelatorioLazer(View view){
        Intent h = new Intent(this, RelatorioLazer.class);
        startActivity(h);
    }
    public void RelatorioOcasional(View view){
        Intent i = new Intent(this, RelatorioOcasional.class);
        startActivity(i);
    }
}
