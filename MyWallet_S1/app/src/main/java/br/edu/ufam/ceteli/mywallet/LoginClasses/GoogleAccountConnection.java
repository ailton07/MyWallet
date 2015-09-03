package br.edu.ufam.ceteli.mywallet.LoginClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.lang.ref.WeakReference;
import java.util.Observable;

import br.edu.ufam.ceteli.mywallet.Activities.LoginActivity;

/**
 * Created by rodri on 28/08/2015.
 */
public class GoogleAccountConnection extends Observable implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static GoogleAccountConnection googleAccountConnectionInstance = null;
    private GoogleApiClient.Builder googleApiClientBuilder = null;
    private WeakReference<Activity> activityWeakReference = null;
    public static final String TAGERROR = "[ERR_CONN_GOOGLE]";
    public static final String TAGINFO = "[CONN_GOOGLE]";
    private ConnectionResult connectionResult = null;
    private GoogleApiClient googleApiClient = null;
    private StatusGoogleConn currentState = null;
    public static final int REQUESTCODE = 0;

    private GoogleAccountConnection(Activity activity) {
        changeCurrentState(StatusGoogleConn.DISCONNECTED);
        Log.i(TAGINFO, "Initializing/Disconnected");
        activityWeakReference = new WeakReference<>(activity);
        googleApiClientBuilder = new GoogleApiClient.Builder(activity)
                                                    .addConnectionCallbacks(this)
                                                    .addOnConnectionFailedListener(this)
                                                    .addApi(Plus.API, Plus.PlusOptions.builder().build())
                                                    .addScope(Plus.SCOPE_PLUS_LOGIN);
        googleApiClient = googleApiClientBuilder.build();
    }

    private void changeCurrentState(StatusGoogleConn state){
        currentState = state;
        setChanged();
        notifyObservers(state);
    }

    private boolean noConnAlertDialog(){
        ConnectivityManager connectivityManager = (ConnectivityManager) activityWeakReference.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (!networkInfoMobile.isConnected() && !networkInfoWifi.isConnected()) {
            if(googleApiClient.isConnected()) {
                new AlertDialog.Builder(activityWeakReference.get()).setTitle("Sem conexão com a Internet")
                        .setMessage("Verifique se o aparelho está conectado à uma rede com acesso à Internet e tente novamente.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Plus.AccountApi.clearDefaultAccount(googleApiClient);
                                Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient);
                                googleApiClient = googleApiClientBuilder.build();
                                googleApiClient.connect();
                                changeCurrentState(StatusGoogleConn.DISCONNECTED);
                                Log.i(TAGINFO, "Disconnected");
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(activityWeakReference.get()).setTitle("Sem conexão com a Internet")
                        .setMessage("Verifique se o aparelho está conectado à uma rede com acesso à Internet e tente novamente.")
                        .setPositiveButton("OK", null)
                        .show();
            }
            return true;
        }
        return false;
    }

    protected void onSignIn(){
        if(!googleApiClient.isConnected() && !googleApiClient.isConnecting()){
            googleApiClient.connect();
        }
    }

    protected void onSignUp(){
        try {
            changeCurrentState(StatusGoogleConn.CONNECTING);
            Log.i(TAGINFO, "Connecting");
            connectionResult.startResolutionForResult(activityWeakReference.get(), REQUESTCODE);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAGERROR, e.getMessage());
            changeCurrentState(StatusGoogleConn.CREATED);
            Log.i(TAGINFO, "Created");
            googleApiClient.connect();
        }
    }

    protected void onSignOut(Activity currentActivity){
        if(googleApiClient.isConnected()){
            if(currentActivity == null){
                Log.e(TAGERROR, "Activity can not be null");
            } else {
                changeCurrentState(StatusGoogleConn.DISCONNECTED);
                Log.i(TAGINFO, "Disconnected");
                Plus.AccountApi.clearDefaultAccount(googleApiClient);
                googleApiClient.disconnect();
                currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
                activityWeakReference.get().finish();
                currentActivity.finish();
                clearInstanceReference();
            }
        }
    }

    protected void onRevoke(Activity currentActivity){
        changeCurrentState(StatusGoogleConn.DISCONNECTED);
        Log.i(TAGINFO, "Revoking/Disconnected");
        Plus.AccountApi.clearDefaultAccount(googleApiClient);
        Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient);
        googleApiClient = googleApiClientBuilder.build();
        currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
        activityWeakReference.get().finish();
        currentActivity.finish();
        clearInstanceReference();
    }

    public static GoogleAccountConnection getInstance(Activity activity) {
        if(googleAccountConnectionInstance == null){
            googleAccountConnectionInstance = new GoogleAccountConnection(activity);
        }
        return googleAccountConnectionInstance;
    }

    public void connect(){
        currentState.connect(this);
    }

    public void disconnect(Activity currentActivity){
        currentState.disconnect(this, currentActivity);
    }

    public void revoke(Activity currentActivity){
        currentState.revoke(this, currentActivity);
    }

    public void onActivityResult(int resultCode){
        if (resultCode == Activity.RESULT_OK) {
            changeCurrentState(StatusGoogleConn.CREATED);
            Log.i(TAGINFO, "Created");
        } else {
            changeCurrentState(StatusGoogleConn.DISCONNECTED);
            Log.i(TAGINFO, "Disconnected");
        }
        onSignIn();
    }

    /* Embora seja mais fácil pegar a API instanciada, é melhor deixar que esta classe cuide da API
     * pois retornando a API qualquer um poderia desconectar sem avisar outras classes que a conta
     * foi desconectada (ou conectada).
     *
     *  public GoogleApiClient getGoogleApiCLient(){
     *    return googleApiClient;
     * }
     */

    public String getAccountID(){
        return Plus.PeopleApi.getCurrentPerson(googleApiClient).getId();
    }

    public String getAccountName(){
        return Plus.PeopleApi.getCurrentPerson(googleApiClient).getDisplayName();
    }

    public String getAccountEmail(){
        return Plus.AccountApi.getAccountName(googleApiClient);
    }

    public String getAccountPicURL(){
        return Plus.PeopleApi.getCurrentPerson(googleApiClient).getImage().getUrl();
    }

    public void clearInstanceReference(){
        googleAccountConnectionInstance = null;
        Log.i(TAGINFO, "Removed");
    }

    public StatusGoogleConn getState(){
        return currentState;
    }

    public void updateWeakReference(Activity currentActivity){
        activityWeakReference = new WeakReference<>(currentActivity);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(Plus.PeopleApi.getCurrentPerson(googleApiClient) == null) {
            noConnAlertDialog();
        } else {
            changeCurrentState(StatusGoogleConn.CONNECTED);
            Log.i(TAGINFO, "Connected");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        changeCurrentState(StatusGoogleConn.DISCONNECTED);
        Log.i(TAGINFO, "Disconnected");
        connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            this.connectionResult = connectionResult;
            if(currentState.equals(StatusGoogleConn.DISCONNECTED)) {
                changeCurrentState(StatusGoogleConn.CREATED);
                Log.i(TAGINFO, "Created");
            } else {
                connect();
            }
        } else {
            if(!noConnAlertDialog()) {
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), activityWeakReference.get(), 0).show();
            }
        }
    }
}
