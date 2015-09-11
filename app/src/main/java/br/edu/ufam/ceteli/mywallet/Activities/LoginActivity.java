package br.edu.ufam.ceteli.mywallet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;

import java.util.Observable;
import java.util.Observer;

import br.edu.ufam.ceteli.mywallet.Classes.Login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.ILoginConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.StatusFacebookConn;
import br.edu.ufam.ceteli.mywallet.Classes.Login.StatusGoogleConn;
import br.edu.ufam.ceteli.mywallet.R;

public class LoginActivity extends AppCompatActivity implements Observer {
    private ILoginConnection iFacebookConn = null;
    private ILoginConnection iGoogleConn = null;
    private static final String TYPE_FACEBOOK = "FACEBOOK";
    private static final String TYPE_GOOGLE = "GOOGLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iGoogleConn = GoogleAccountConnection.getInstance(this);
        iGoogleConn.addObserverClass(this);
        iFacebookConn = FacebookAccountConnection.getInstance(this);
        iFacebookConn.addObserverClass(this);
        setContentView(R.layout.activity_login_wait);
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
                    setContentView(R.layout.activity_login_root);
                    break;

                case CONNECTING:
                    break;

                case CONNECTED:
                    iFacebookConn.clearInstanceReference();
                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("TYPE", TYPE_GOOGLE);
                    startActivity(intent);
                    this.finish();
                    break;

                case DISCONNECTED:
                    break;
            }
        }

        if(data instanceof StatusFacebookConn){
            switch ((StatusFacebookConn) data) {
                case DISCONNECTED:
                    break;

                case CONNECTING:
                    break;

                case CONNECTED:
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