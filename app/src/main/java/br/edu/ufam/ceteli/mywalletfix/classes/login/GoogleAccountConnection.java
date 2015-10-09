package br.edu.ufam.ceteli.mywalletfix.classes.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import br.edu.ufam.ceteli.mywalletfix.activities.LoginActivity;

/**
 * Created by rodri on 28/08/2015.
 */
public class GoogleAccountConnection extends Observable implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ILoginConnection {
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

    /*
     * IConnection
     */
    @Override
    public void connect() {
        currentState.connect(this);
    }

    @Override
    public void disconnect(Activity currentActivity) {
        currentState.disconnect(this, currentActivity);
    }

    @Override
    public void revoke(Activity currentActivity) {
        currentState.revoke(this, currentActivity);
    }

    @Override
    public Object getState() {
        return currentState;
    }

    @Override
    public String getAccountID() {
        return Plus.PeopleApi.getCurrentPerson(googleApiClient).getId();
    }

    @Override
    public String getAccountName() {
        return Plus.PeopleApi.getCurrentPerson(googleApiClient).getDisplayName();
    }

    @Override
    public String getAccountEmail() {
        return Plus.AccountApi.getAccountName(googleApiClient);
    }

    @Override
    public void getAccountPicProfile(final ImageView imageView) {
        if(Plus.PeopleApi.getCurrentPerson(googleApiClient).hasImage()) {
            Person.Image image = Plus.PeopleApi.getCurrentPerson(googleApiClient).getImage();
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    try {
                        URL url = new URL(params[0]);
                        InputStream inputStream = url.openStream();
                        return BitmapFactory.decodeStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if(bitmap != null)
                        imageView.setImageBitmap(bitmap);
                }
            }.execute(image.getUrl());
        }
    }

    @Override
    public void getAccountPicCover(final ImageView imageView) {
        if(Plus.PeopleApi.getCurrentPerson(googleApiClient).hasCover()) {
            Person.Cover.CoverPhoto image = Plus.PeopleApi.getCurrentPerson(googleApiClient).getCover().getCoverPhoto();
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    try {
                        URL url = new URL(params[0]);
                        InputStream inputStream = url.openStream();
                        return BitmapFactory.decodeStream(inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if(bitmap != null)
                        imageView.setImageBitmap(bitmap);
                }
            }.execute(image.getUrl());
        }
    }

    @Override
    public boolean verifyLogin() {
        return googleApiClient.isConnected();
    }

    @Override
    public void updateWeakReference(Activity currentActivity) {
        activityWeakReference = new WeakReference<>(currentActivity);
    }

    @Override
    public void clearInstanceReference() {
        googleAccountConnectionInstance = null;
        Log.i(TAGINFO, "Removed");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            changeCurrentState(StatusGoogleConn.CREATED);
            Log.i(TAGINFO, "Created");
        } else {
            changeCurrentState(StatusGoogleConn.DISCONNECTED);
            Log.i(TAGINFO, "Disconnected");
        }
        onSignIn();
    }

    @Override
    public void addObserverClass(Observer observer) {
        addObserver(observer);
    }

    @Override
    public void delObserverClass(Observer observer) {
        deleteObserver(observer);

    }
}