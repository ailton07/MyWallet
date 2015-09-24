package br.edu.ufam.ceteli.mywallet.Classes.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import br.edu.ufam.ceteli.mywallet.Activities.LoginActivity;

/**
 * Created by rodri on 28/08/2015.
 */
public class FacebookAccountConnection extends Observable implements FacebookCallback<LoginResult>, ILoginConnection {
    private static final List facebookPermissions = Arrays.asList("public_profile", "email");
    private static FacebookAccountConnection facebookAccountConnection = null;
    private WeakReference<Activity> activityWeakReference = null;
    public static final String TAGERROR = "[ERR_CONN_FACEBOOK]";
    public static final String TAGINFO = "[CONN_FACEBOOK]";
    private static final String APIID = "109489642738112";
    private CallbackManager callbackManager = null;
    private StatusFacebookConn currentState = null;

    private FacebookAccountConnection(Activity activity) {
        changeCurrentState(StatusFacebookConn.DISCONNECTED);
        Log.i(TAGINFO, "Initializing/Disconnected");
        activityWeakReference = new WeakReference<>(activity);
        FacebookSdk.sdkInitialize(activity);
        FacebookSdk.setApplicationId(APIID);
    }

    private void changeCurrentState(StatusFacebookConn state){
        currentState = state;
        setChanged();
        notifyObservers(state);
    }

    private void getUserInfo(){
        Bundle parameters = new Bundle();
        final AlertDialog alertDialog = new AlertDialog.Builder(activityWeakReference.get()).setMessage("Baixando informações do usuário...").setCancelable(false).show();

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                alertDialog.dismiss();
                changeCurrentState(StatusFacebookConn.CONNECTED);
                Log.i(TAGINFO, "Getting user info completed/Connected");
                try {
                    activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).edit().putString("FacebookEmail", jsonObject.getString("email")).commit();
                    activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).edit().putBoolean("FacebookLogged", true).commit();

                    //TODO: pegar foto
                    Log.i(TAGINFO, jsonObject.getString("picture"));
                } catch (JSONException e) {
                    Log.e(TAGERROR, e.getMessage() + " - " + e.getCause());
                    e.printStackTrace();
                }

            }
        });
        parameters.putString("fields", "email,picture");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
        Log.i(TAGINFO, "Requesting user info");
    }

    private boolean noConnAlertDialog(){
        ConnectivityManager connectivityManager = (ConnectivityManager) activityWeakReference.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        changeCurrentState(StatusFacebookConn.DISCONNECTED);
        Log.i(TAGINFO, "Canceled/Disconnected");

        if(!networkInfoMobile.isConnected() && !networkInfoWifi.isConnected()) {
            new AlertDialog.Builder(activityWeakReference.get()).setTitle("Sem conexão com a Internet")
                    .setMessage("Verifique se o aparelho está conectado à uma rede com acesso à Internet e tente novamente.")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return false;
    }

    protected void onSignUp(){
        changeCurrentState(StatusFacebookConn.CONNECTING);
        Log.i(TAGINFO, "Connecting");
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);
        LoginManager.getInstance().logInWithReadPermissions(activityWeakReference.get(), facebookPermissions);
    }

    protected void onSignOut(Activity currentActivity){
        if(currentActivity == null){
            Log.e(TAGERROR, "Activity can not be null");
        } else {
            changeCurrentState(StatusFacebookConn.DISCONNECTED);
            Log.i(TAGINFO, "Disconnected");
            LoginManager.getInstance().logOut();
            activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).edit().putBoolean("FacebookLogged", false).commit();
            activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).edit().putString("FacebookEmail", null).commit();
            currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
            activityWeakReference.get().finish();
            currentActivity.finish();
            clearInstanceReference();
        }
    }

    protected void onRevoke(final Activity currentActivity){
        try {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    if(graphResponse != null){
                        if(graphResponse.getError() != null) {
                            Toast.makeText(activityWeakReference.get(), "Você está desconectado. Impossível revogar autorização!", Toast.LENGTH_LONG).show();
                            Log.e(TAGERROR, graphResponse.getError().toString());
                        } else {
                            changeCurrentState(StatusFacebookConn.DISCONNECTED);
                            Log.i(TAGINFO, "Auth Revoked/Disconnected");
                            activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).edit().putString("FacebookEmail", null).commit();
                            activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).edit().putBoolean("FacebookLogged", false).commit();
                            currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
                            activityWeakReference.get().finish();
                            currentActivity.finish();
                            clearInstanceReference();
                        }
                    }
                }
            }).executeAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static FacebookAccountConnection getInstance(Activity activity) {
        if (facebookAccountConnection == null){
            facebookAccountConnection = new FacebookAccountConnection(activity);
        }
        return facebookAccountConnection;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        if(Profile.getCurrentProfile() == null) {
            new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    Profile.setCurrentProfile(currentProfile);
                    if(currentState.equals(StatusFacebookConn.CONNECTING)) {
                        getUserInfo();
                    }
                }
            };
        } else {
            getUserInfo();
        }
    }

    @Override
    public void onCancel() {
        noConnAlertDialog();
    }

    @Override
    public void onError(FacebookException e) {
        changeCurrentState(StatusFacebookConn.DISCONNECTED);
        Log.e(TAGERROR, e.getMessage());
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
    public void updateWeakReference(Activity currentActivity){
        activityWeakReference = new WeakReference<>(currentActivity);
    }

    @Override
    public void clearInstanceReference(){
        facebookAccountConnection = null;
        Log.i(TAGINFO, "Removed");
    }

    @Override
    public String getAccountID() {
        return Profile.getCurrentProfile().getId();
    }

    @Override
    public String getAccountName() {
        return Profile.getCurrentProfile().getName();
    }

    @Override
    public String getAccountEmail() {
        return activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).getString("FacebookEmail", null);
    }

    @Override
    public void getAccountPicProfile(final ImageView imageView){
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
        }; //TODO: execute();
    }

    @Override
    public void getAccountPicCover(final ImageView imageView) {
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
        }; //TODO: execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void addObserverClass(Observer observer){
        addObserver(observer);
    }

    @Override
    public void delObserverClass(Observer observer){
        deleteObserver(observer);
    }

    @Override
    public boolean verifyLogin(){
        if(activityWeakReference.get().getSharedPreferences("FacebookSession", Context.MODE_PRIVATE).getBoolean("FacebookLogged", false)){
            changeCurrentState(StatusFacebookConn.CONNECTED);
            Log.i(TAGINFO, "Saved/Connected");
            return true;
        }
        return false;
    }
}