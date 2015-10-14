package br.edu.ufam.ceteli.mywallet.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.Download;
import br.edu.ufam.ceteli.mywallet.classes.Upload;

/**
 * Created by rodrigo on 30/09/15.
 */
public class BackupPreferences extends PreferenceFragment {
    private ProgressDialog dialog = null;
    private Upload upload = null;
    private String dbKey = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_options);

        Preference createBackup = findPreference("createBackup");
        Preference restoreBackup = findPreference("restoreBackup");

        createBackup.setOnPreferenceClickListener(createBackupListener());
        restoreBackup.setOnPreferenceClickListener(restoreBackupListener());

        dbKey = getActivity().getIntent().getExtras().getString("DBKey");
    }

    private Preference.OnPreferenceClickListener createBackupListener(){
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                criarBackup();
                return true;
            }
        };
    }

    private Preference.OnPreferenceClickListener restoreBackupListener(){
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                restaurarBackup();
                return true;
            }
        };
    }

    private void criarBackup(){
        upload = new Upload(dbKey);
        final String pathDb = "//data//data//br.edu.ufam.ceteli.mywallet//databases////teste.db";

        //String db = "teste.db";
        dialog = ProgressDialog.show(getActivity(), " ", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("uploading started.....");
                        //dialog.dismiss();
                    }
                });
                upload.uploadFile("teste.db", dialog);
                //up.uploadFile(pathDb, dialog);
            }
        }).start();
    }

    private void restaurarBackup(){
        //Download
        Download download = new Download(getActivity());
        download.baixa(dbKey);
    }
}