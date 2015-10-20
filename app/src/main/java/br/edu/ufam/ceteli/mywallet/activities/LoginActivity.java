package br.edu.ufam.ceteli.mywallet.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.activeandroid.ActiveAndroid;
import com.facebook.appevents.AppEventsLogger;

import java.util.Observable;
import java.util.Observer;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.ILoginConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.StatusFacebookConn;
import br.edu.ufam.ceteli.mywallet.classes.login.StatusGoogleConn;

public class LoginActivity extends AppCompatActivity implements Observer {
    private ILoginConnection iFacebookConn = null;
    private ILoginConnection iGoogleConn = null;
    private static final String TYPE_FACEBOOK = "FACEBOOK";
    private static final String TYPE_GOOGLE = "GOOGLE";
    private ProgressDialog conncecting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDB();
        iGoogleConn = GoogleAccountConnection.getInstance(this);
        iGoogleConn.addObserverClass(this);
        iFacebookConn = FacebookAccountConnection.getInstance(this);
        iFacebookConn.addObserverClass(this);
        setContentView(R.layout.activity_login_wait);
    }

    protected void initializeDB() {
        com.activeandroid.Configuration.Builder configurationBuilder = new com.activeandroid.Configuration.Builder(this).setDatabaseName("teste.db");
        configurationBuilder.addModelClasses(Entrada.class);
        configurationBuilder.addModelClasses(br.edu.ufam.ceteli.mywallet.classes.Entry.class);
        ActiveAndroid.initialize(configurationBuilder.create());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!iFacebookConn.verifyLogin()) {
            if (iGoogleConn.getState().equals(StatusGoogleConn.DISCONNECTED)) {
                iGoogleConn.connect();
            } else {
                if(iGoogleConn.getState().equals(StatusGoogleConn.CONNECTED)){
                    update(GoogleAccountConnection.getInstance(null), StatusGoogleConn.CONNECTED);
                } else {
                    if(iGoogleConn.getState().equals(StatusGoogleConn.CREATED)){
                        update(GoogleAccountConnection.getInstance(null), StatusGoogleConn.CREATED);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        iGoogleConn.updateWeakReference(this);
        iFacebookConn.updateWeakReference(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iGoogleConn.delObserverClass(this);
        iFacebookConn.delObserverClass(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iFacebookConn.onActivityResult(requestCode, resultCode, data);
        if(GoogleAccountConnection.REQUESTCODE == requestCode){
            iGoogleConn.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public synchronized void update(Observable observable, Object data) {
        if((observable != GoogleAccountConnection.getInstance(null)) && (observable != FacebookAccountConnection.getInstance(null))) {
            return;
        }

        if(data instanceof StatusGoogleConn) {
            switch ((StatusGoogleConn) data) {
                case CREATED:
                    if(conncecting != null)
                        conncecting.dismiss();
                    setContentView(R.layout.activity_login_root);
                    break;

                case CONNECTING:
                    conncecting = ProgressDialog.show(this, "", "Concetando. Por favor espere...", true, false);
                    break;

                case CONNECTED:
                    if(conncecting != null)
                        conncecting.dismiss();
                    iFacebookConn.clearInstanceReference();
                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("TYPE", TYPE_GOOGLE);
                    startActivity(intent);
                    this.finish();
                    break;

                case DISCONNECTED:
                    if(conncecting != null)
                        conncecting.dismiss();
                    break;
            }
        }

        if(data instanceof StatusFacebookConn){
            switch ((StatusFacebookConn) data) {
                case DISCONNECTED:
                    if(conncecting != null)
                        conncecting.dismiss();
                    break;

                case CONNECTING:
                    conncecting = ProgressDialog.show(this, "", "Concetando. Por favor espere...", true, false);
                    break;

                case CONNECTED:
                    if(conncecting != null)
                        conncecting.dismiss();
                    iGoogleConn.clearInstanceReference();
                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("TYPE", TYPE_FACEBOOK);
                    startActivity(intent);
                    this.finish();
                    break;
            }
        }
    }

    public void googleLogin(View view){
        iGoogleConn.connect();
    }

    public void facebookLogin(View view){
        iFacebookConn.connect();
    }
}