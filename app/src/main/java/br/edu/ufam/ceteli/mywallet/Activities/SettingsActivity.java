package br.edu.ufam.ceteli.mywallet.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.edu.ufam.ceteli.mywallet.R;

public class SettingsActivity extends AppCompatActivity {
    /*
     * Para funções de backup, ver BackupPreferencesFragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction().replace(R.id.frameSettings, new BackupPreferencesFragment()).commit();
    }
}
