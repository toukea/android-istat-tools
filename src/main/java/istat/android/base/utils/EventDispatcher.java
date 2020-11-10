package istat.android.base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventDispatcher {
    final static String EVENT_ALL = "event_dispatcher_all";
    ConcurrentHashMap<String, List<EventListener>> eventNameListenerMap = new ConcurrentHashMap<>();
    static EventDispatcher instance;

    public static EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
        }
        return instance;
    }

    private EventDispatcher() {

    }

    public boolean dispatchEvent(String eventName, Object... payload) {
        return dispatchEvent(eventName, new PayLoad(payload));
    }

    public boolean dispatchEvent(String eventName, PayLoad payLoad) {
        List<EventListener> listeners = getEventListeners(eventName);
        for (EventListener listener : listeners) {
            if (listener.onEvent(eventName, payLoad)) {
                return false;
            }
        }
        return true;
    }

    public void dispatchEvent(RunnableDispatcher dispatcher, final String eventName, Object... payLoad) {
        dispatchEvent(dispatcher, 0, eventName, new PayLoad(payLoad), null);
    }

    public void dispatchEvent(RunnableDispatcher dispatcher, final String eventName, final PayLoad payLoad) {
        dispatchEvent(dispatcher, 0, eventName, payLoad, null);
    }

    public void dispatchEvent(RunnableDispatcher dispatcher, final String eventName, final PayLoad payLoad, final CompletionCallback callback) {
        dispatchEvent(dispatcher, 0, eventName, payLoad, callback);
    }

    public void dispatchEvent(final String eventName, final PayLoad payLoad, final CompletionCallback callback) {
        dispatchEvent(null, 0, eventName, payLoad, callback);
    }

    public void dispatchEvent(int delay, final String eventName, final PayLoad payLoad, final CompletionCallback callback) {
        dispatchEvent(null, delay, eventName, payLoad, callback);
    }

    public void dispatchEvent(RunnableDispatcher dispatcher, int delay, final String eventName, final PayLoad payLoad, final CompletionCallback callback) {
        if (dispatcher == null) {
            dispatcher = RunnableDispatcher.DEFAULT;
        }
        final List<EventListener> listeners = getEventListeners(eventName);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (EventListener listener : listeners) {
                    if (!listener.onEvent(eventName, payLoad)) {
                        if (callback != null) {
                            callback.onCompleted(false);
                        }
                        return;
                    }
                }
                if (callback != null) {
                    callback.onCompleted(true);
                }
            }
        };
        dispatcher.dispatch(runnable, delay);
    }


    private List<EventListener> getEventListeners(String eventName) {
        List<EventListener> listeners = new ArrayList();
        List<EventListener> dispatchAll = getEventDispatcherListByEventName(EVENT_ALL);
        List<EventListener> dispatchEvents = getEventDispatcherListByEventName(eventName);
        if (dispatchAll != null && !dispatchAll.isEmpty()) {
            listeners.addAll(dispatchAll);
        }
        if (dispatchEvents != null && !dispatchEvents.isEmpty()) {
            listeners.addAll(dispatchEvents);
        }
        return listeners;
    }

    public boolean registerEventListener(EventListener listener, String... eventNames) {
        return registerEventListener(-1, listener,  eventNames);
    }

    public boolean registerEventListener(int priority, EventListener listener, String... eventNames) {
        if (eventNames == null || eventNames.length == 0) {
            getEventDispatcherListByEventName(EVENT_ALL).add(listener);
            return true;
        }
        List<EventListener> listeners;
        for (String eventName : eventNames) {
            listeners = getEventDispatcherListByEventName(eventName);
            if (!listeners.contains(listener)) {
                if (priority < 0 || priority >= listeners.size()) {
                    listeners.add(listener);
                } else {
                    listeners.add(priority, listener);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private List<EventListener> getEventDispatcherListByEventName(String eventName) {
        if (!eventNameListenerMap.containsKey(eventName)) {
            eventNameListenerMap.put(eventName, new ArrayList());
        }
        return eventNameListenerMap.get(eventName);
    }

    public void unregisterAll(String... eventNames) {
        if (eventNames == null || eventNames.length == 0) {
            eventNameListenerMap.clear();
            return;
        }
        for (String eventName : eventNames) {
            getEventDispatcherListByEventName(eventName).clear();
        }
    }

    public int unregisterEventListener(EventListener listener) {
        int count = 0;
        if (eventNameListenerMap.isEmpty()) {
            return count;
        }
        List<EventListener> listeners;
        for (Map.Entry<String, List<EventListener>> entry : eventNameListenerMap.entrySet()) {
            listeners = entry.getValue();
            if (listeners != null && !listeners.isEmpty()) {
                if (listeners.remove(listener)) {
                    count++;
                }
            }
        }
        return 0;
    }

    public int unregisterEventListener(EventListener listener, String... eventNames) {
        if (eventNames == null || eventNames.length == 0) {
            return unregisterEventListener(listener);
        }
        int count = 0;
        List<EventListener> listeners;
        for (String eventName : eventNames) {
            listeners = getEventDispatcherListByEventName(eventName);
            if (listeners.remove(listener)) {
                count++;
            }
        }
        return count;
    }

    public interface EventListener {
        /**
         * Called when an event is dispatched.
         * @param eventName the name of the dispatched event
         * @param payLoad the payload of the event
         * @return should cancel dispatching ?
         */
        boolean onEvent(String eventName, PayLoad payLoad);
    }

    public interface RunnableDispatcher {
        void dispatch(Runnable runnable, int delay);

        boolean cancel(Runnable runnable);

        int release();

        RunnableDispatcher DEFAULT = new RunnableDispatcher() {
            @Override
            public void dispatch(Runnable runnable, int delay) {
                runnable.run();
            }

            @Override
            public boolean cancel(Runnable runnable) {
                return false;
            }

            @Override
            public int release() {
                return 0;
            }
        };
    }

    public interface CompletionCallback {
        void onCompleted(boolean completed);
    }

}
