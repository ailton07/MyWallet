package br.edu.ufam.ceteli.mywalletfix.activities.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import br.edu.ufam.ceteli.mywalletfix.R;

/**
 * Created by rodrigo on 30/09/15.
 */
public class BackupPreferences extends PreferenceFragment {
    private Preference restoreBackup = null;
    private Preference createBackup = null;
    private String dbKey = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_backup);

        createBackup = findPreference("createBackup");
        restoreBackup = findPreference("restoreBackup");

        createBackup.setOnPreferenceClickListener(createBackupListener());
        restoreBackup.setOnPreferenceClickListener(restoreBackupListener());

        dbKey = getActivity().getIntent().getExtras().getString("DBKey");
    }

    private Preference.OnPreferenceClickListener createBackupListener(){
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //criarBackup();
                return true;
            }
        };
    }

    private Preference.OnPreferenceClickListener restoreBackupListener(){
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //restaurarBackup();
                return true;
            }
        };
    }
}
