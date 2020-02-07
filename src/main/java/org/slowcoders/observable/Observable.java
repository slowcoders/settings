package org.slowcoders.observable;

public interface Observable<Observer> {

    Observer addAsyncObserver(Observer observer);

    Observer addWeakAsyncObserver(Observer observer);

    Observer addRealtimeObserver(Observer observer);

    Observer addWeakRealtimeObserver(Observer observer);

    boolean removeObserver(Observer observer);

}
