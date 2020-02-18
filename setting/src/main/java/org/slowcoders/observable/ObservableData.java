package org.slowcoders.observable;

public class ObservableData<P extends Observable, T> extends AbstractObservable<ObservableData.Observer<P, T>> {

    public interface Observer<P, T> {
        void onChanged(P property, T data);
    }

    @Override
    protected void doNotify(Observer<P, T> observer, Object data) {
        observer.onChanged((P)this, (T)data);
    }

}
