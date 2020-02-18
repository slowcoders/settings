package org.slowcoders.observable;

import org.slowcoders.io.util.NPAsyncScheduler;
import org.slowcoders.io.util.UITask;
import org.slowcoders.util.RefList;

import java.util.ArrayDeque;

public abstract class AsyncObservable<Observer> {

    static AsyncObservers uiEmpty = new AsyncObservers(null);

    private AsyncObservers<Observer> uiObservers = uiEmpty;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected synchronized Observer addAsyncObserver(Observer observer, boolean isWeakRef) {
        RefList<Observer> observers = this.getAsyncObservers();
        if (isWeakRef) {
            observers.addLast_asWeakRef(observer);
        }
        else {
            observers.addLast(observer);
        }
        return observer;
    }

    public final Observer addAsyncObserver(Observer observer) {
        return this.addAsyncObserver(observer, false);
    }

    public final Observer addWeakAsyncObserver(Observer observer) {
        return this.addAsyncObserver(observer, true);
    }

    public boolean removeObserver(Observer observer) {
        if (uiObservers.remove(observer)) {
            return true;
        }
        return false;
    }

    public void postNotification(Object data) {
        uiObservers.addNotificationData(data);
    }

    protected abstract void doNotify(Observer observer, Object data);

    protected synchronized final RefList<Observer> getAsyncObservers() {
        if (uiObservers == uiEmpty) {
            uiObservers = new AsyncObservers(this);
        }
        return this.uiObservers;
    }

    private static final class AsyncObservers<Observer> extends RefList<Observer> implements UITask {
        AsyncObservable<Observer> observable;
        private ArrayDeque dataQueue;

        public AsyncObservers(AsyncObservable<Observer> observable) {
            this.observable = observable;
        }

        synchronized void addNotificationData(Object data) {
            if (this.isEmpty()) {
                return;
            }
            if (this.dataQueue == null) {
                this.dataQueue = new ArrayDeque();
            }
            else if (dataQueue.peekLast() == data) {
                return;
            }

            dataQueue.addLast(data);
            if (dataQueue.size() == 1) {
                NPAsyncScheduler.executeLater(this);
            }
        }

        @Override
        public void executeTask() throws Exception {
            ArrayDeque dataQueue;
            synchronized (this) {
                dataQueue = this.dataQueue;
                this.dataQueue = null;
            }

            while (true) {
                Object data = dataQueue.pollFirst();
                if (data == null) break;
                for (Observer observer : this) {
                    observable.doNotify(observer, data);
                }
            }

            synchronized (this) {
                if (this.dataQueue == null) {
                    this.dataQueue = dataQueue;
                }
            }
        }
    }

}
