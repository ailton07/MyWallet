package br.edu.ufam.ceteli.mywallet.classes.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import java.util.Observer;

/**
 * Created by rodrigo on 10/09/15.
 * A maioria (exceto verifyLogin) dessas funções estão implementadas nas duas classes, talvez seja
 * melhor unificar em uma interface.
 */
public interface ILoginConnection {
    /*
     * Conexão
     */
    void connect();
    void disconnect(Activity currentActivity);
    void revoke(Activity currentActivity);
    Object getState();

    /*
     * Dados
     */
    String getAccountID();
    String getAccountName();
    String getAccountEmail();
    void getAccountPicProfile(ImageView imageView);
    void getAccountPicCover(ImageView imageView);

    /*
     * Talvez seja necessário verificar algum login feito (caso do facebook)
     */
    boolean verifyLogin();

    /*
     * Atualiza atividade referenciada
     */
    void updateWeakReference(Activity currentActivity);

    /*
     * Remove referencia ao objeto
     */
    void clearInstanceReference();

    /*
     * Chamado após as atividades de login serem encerradas
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /*
     * Adiciona e remove observador
     */
    void addObserverClass(Observer observer);
    void delObserverClass(Observer observer);
}