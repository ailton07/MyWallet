package br.edu.ufam.ceteli.mywallet.LoginClasses;

import android.app.Activity;

/**
 * Created by rodri on 28/08/2015.
 */
public enum StatusGoogleConn {
    CREATED{
        @Override
        void connect(GoogleAccountConnection googleAccountConnection){
            googleAccountConnection.onSignUp();
        }

        @Override
        void disconnect(GoogleAccountConnection googleAccountConnection, Activity currentActivity){
            googleAccountConnection.onSignOut(currentActivity);
        }
    },

    CONNECTING,

    CONNECTED{
        @Override
        void disconnect(GoogleAccountConnection googleAccountConnection, Activity currentActivity){
            googleAccountConnection.onSignOut(currentActivity);
        }

        @Override
        void revoke(GoogleAccountConnection googleAccountConnection, Activity currentActivity){
            googleAccountConnection.onRevoke(currentActivity);
        }

    },

    DISCONNECTED{
        @Override
        void connect(GoogleAccountConnection googleAccountConnection){
            googleAccountConnection.onSignIn();
        }
    };

    void connect(GoogleAccountConnection googleAccountConnection){};
    void disconnect(GoogleAccountConnection googleAccountConnection, Activity currentActivity){};
    void revoke(GoogleAccountConnection googleAccountConnection, Activity currentActivity){};
}
