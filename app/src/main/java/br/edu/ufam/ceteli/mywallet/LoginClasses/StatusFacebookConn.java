package br.edu.ufam.ceteli.mywallet.LoginClasses;

import android.app.Activity;

/**
 * Created by rodri on 28/08/2015.
 */
public enum StatusFacebookConn {
    DISCONNECTED{
        @Override
        void connect(FacebookAccountConnection facebookAccountConnection){
            facebookAccountConnection.onSignUp();
        }
    },

    CONNECTING,

    CONNECTED{
        @Override
        void disconnect(FacebookAccountConnection facebookAccountConnection, Activity currentActivity){
            facebookAccountConnection.onSignOut(currentActivity);
        }

        @Override
        void revoke(FacebookAccountConnection facebookAccountConnection, Activity currentActivity){
            facebookAccountConnection.onRevoke(currentActivity);
        }

    };

    void connect(FacebookAccountConnection facebookAccountConnection){};
    void disconnect(FacebookAccountConnection facebookAccountConnection, Activity currentActivity){};
    void revoke(FacebookAccountConnection facebookAccountConnection, Activity currentActivity){};
}
