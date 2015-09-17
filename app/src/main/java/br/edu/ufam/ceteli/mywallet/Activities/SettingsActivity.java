package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.edu.ufam.ceteli.mywallet.Classes.Download;
import br.edu.ufam.ceteli.mywallet.Classes.Upload;
import br.edu.ufam.ceteli.mywallet.R;

public class SettingsActivity extends AppCompatActivity {

    String email;
    //Upload
    ProgressDialog dialog;
    Upload up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        email = getIntent().getExtras().getString("email");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void criarBackup(View v){
        up = new Upload(email);

        final String pathDb = "//data//data//br.edu.ufam.ceteli.mywallet//databases////teste.db";

//				String db = "teste.db";

        dialog = ProgressDialog.show(this, " ", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("uploading started.....");
                        //dialog.dismiss();
                    }
                });


                up.uploadFile("teste.db", dialog);
                //up.uploadFile(pathDb, dialog);

            }
        }).start();

    }

    public void restaurarBackup(View v){
        //Download
        Download download = new Download(this);
        download.baixa(email);

    }
}
