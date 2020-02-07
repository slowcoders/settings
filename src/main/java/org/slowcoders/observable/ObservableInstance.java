package org.slowcoders.observable;

public class ObservableInstance<P extends ObservableInstance> extends AbstractObservable<ObservableInstance.Observer<P>> {

    public interface Observer<P> {
        void  onChanged(P property);
    }

    public void notifyChanged() {
        super.postNotification((P)this);
    }


    @Override
    protected void doNotify(Observer<P> observer, Object data) {
        observer.onChanged((P)this);
    }
}
