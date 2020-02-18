package org.slowcoders.observable;

import org.slowcoders.util.RefList;

public abstract class AbstractObservable<Observer> extends AsyncObservable<Observer> implements Observable<Observer> {

    static RefList rtEmpty = new RefList();

    private RefList<Observer> rtObservers = rtEmpty;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Observer addRealtimeObserver(Observer observer, boolean isWeakRef) {
        if (rtObservers == rtEmpty) {
            rtObservers = new RefList<>();
        }
        if (isWeakRef) {
            rtObservers.addLast_asWeakRef(observer);
        }
        else {
            rtObservers.addLast(observer);
        }
        return observer;
    }

    public Observer addRealtimeObserver(Observer observer) {
        return this.addRealtimeObserver(observer, false);
    }

    public Observer addWeakRealtimeObserver(Observer observer) {
        return this.addRealtimeObserver(observer, true);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        if (rtObservers.remove(observer)) {
            return true;
        }
        return super.removeObserver(observer);
    }

    public void postNotification(Object data) {
        for (Observer observer : this.rtObservers) {
            try {
                doNotify(observer, data);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.postNotification(data);
    }

    static class ObserverProxy<Observer> {
        final Observer observer;

        ObserverProxy(Observer observer) {
            this.observer = observer;
        }
    }
}
