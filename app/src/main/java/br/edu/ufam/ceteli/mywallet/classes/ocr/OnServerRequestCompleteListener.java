package br.edu.ufam.ceteli.mywallet.classes.ocr;

/**
 * Created by Paco on 6/25/2015.
 */

public interface OnServerRequestCompleteListener {
    void onServerRequestComplete(String response);
    void onErrorOccurred(String errorMessage);
}
