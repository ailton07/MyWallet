package br.edu.ufam.ceteli.mywallet.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.appevents.AppEventsLogger;

import java.util.Observable;
import java.util.Observer;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.LoginClasses.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.LoginClasses.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.LoginClasses.StatusFacebookConn;
import br.edu.ufam.ceteli.mywallet.LoginClasses.StatusGoogleConn;

public class LoginActivity extends AppCompatActivity implements Observer {
    private FacebookAccountConnection facebookAccountConnection = null;
    private GoogleAccountConnection googleAccountConnection = null;
    private static final String TYPE_FACEBOOK = "FACEBOOK";
    private static final String TYPE_GOOGLE = "GOOGLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleAccountConnection = GoogleAccountConnection.getInstance(this);
        googleAccountConnection.addObserver(this);
        facebookAccountConnection = FacebookAccountConnection.getInstance(this);
        facebookAccountConnection.addObserver(this);
        setContentView(R.layout.activity_login_wait);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!facebookAccountConnection.verifyLogin()) {
            /*
             * Se fizer login no facebook com estado CRIADO, é exibida a tela para login no GMAIL, não
             * queremos isso!
             * Se o estado está em CREATED, não conecte novamente ao entrar na atividade.
             */
            if (googleAccountConnection.getState().equals(StatusGoogleConn.DISCONNECTED)) {
                googleAccountConnection.connect();
            } else {
                if(googleAccountConnection.getState().equals(StatusGoogleConn.CONNECTED)){
                    update(googleAccountConnection, StatusGoogleConn.CONNECTED);
                } else {
                    if(googleAccountConnection.getState().equals(StatusGoogleConn.CREATED)){
                        update(googleAccountConnection, StatusGoogleConn.CREATED);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        // Quando voltar (ou iniciar), atualiza a referenciada
        googleAccountConnection.updateWeakReference(this);
        facebookAccountConnection.updateWeakReference(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleAccountConnection.deleteObserver(this);
        facebookAccountConnection.deleteObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookAccountConnection.onActivityResult(requestCode, resultCode, data);
        if(GoogleAccountConnection.REQUESTCODE == requestCode){
            googleAccountConnection.onActivityResult(resultCode);
        }
    }

    /*
     * Observer
     */
    @Override
    public void update(Observable observable, Object data) {
        if((observable != googleAccountConnection) && (observable != facebookAccountConnection)) {
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
                    facebookAccountConnection.clearInstanceReference();
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
                    googleAccountConnection.clearInstanceReference();
                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("TYPE", TYPE_FACEBOOK);
                    startActivity(intent);
                    this.finish();
                    break;
            }
        }
    }

    public void googleLogin(View view){
        googleAccountConnection.connect();
    }

    public void facebookLogin(View view){
        facebookAccountConnection.connect();
    }
}
