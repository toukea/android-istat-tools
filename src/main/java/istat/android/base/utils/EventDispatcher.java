package istat.android.base.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDispatcher {
    final static String EVENT_ALL = "event_dispatcher_all";
    RunnableDispatcher runnableDispatcher = RunnableDispatcher.DEFAULT;
    ConcurrentHashMap<String, List<EventListener>> eventNameListenerMap = new ConcurrentHashMap<>();
    static HashMap<String, EventDispatcher> nameDispatcherMap = new HashMap();
    String nameSpace;

    public static EventDispatcher from(String nameSpace) {
        EventDispatcher dispatcher = nameDispatcherMap.get(nameSpace);
        if (dispatcher == null) {
            dispatcher = new EventDispatcher(nameSpace);
            nameDispatcherMap.put(nameSpace, dispatcher);
        }
        return dispatcher;
    }

    public void setRunnableDispatcher(RunnableDispatcher runnableDispatcher) {
        this.runnableDispatcher = runnableDispatcher;
    }

    private EventDispatcher(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public boolean dispatchEvent(String eventName, Object... payload) {
        return dispatchEvent(eventName, new PayLoad(payload));
    }

    public boolean dispatchEvent(String eventName, PayLoad payLoad) {
        List<EventListener> listeners = new ArrayList();
        List<EventListener> dispatchAll = getEventDispatcherListByEventName(EVENT_ALL);
        List<EventListener> dispatchEvents = getEventDispatcherListByEventName(eventName);
        if (dispatchAll != null && !dispatchAll.isEmpty()) {
            listeners.addAll(dispatchAll);
        }
        if (dispatchEvents != null && !dispatchEvents.isEmpty()) {
            listeners.addAll(dispatchEvents);
        }
        for (EventListener listener : listeners) {
            if (!listener.onEvent(eventName, payLoad)) {
                return false;
            }
        }
        return true;
    }

    public boolean registerEventListener(EventListener listener, String... eventNames) {
        return registerEventListener(-1, listener, (String[]) eventNames);
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
        return false;
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

}
