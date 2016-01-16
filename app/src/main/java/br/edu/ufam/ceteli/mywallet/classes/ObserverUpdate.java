package br.edu.ufam.ceteli.mywallet.classes;

import java.util.Observable;

/**
 * Created by rodrigo on 15/01/16.
 */
public class ObserverUpdate extends Observable {
    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}
