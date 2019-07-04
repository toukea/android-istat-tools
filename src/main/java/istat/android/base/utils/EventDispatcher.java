package istat.android.base.utils;

public class EventDispatcher {

    public boolean dispatchEvent(String eventName, Object... payload) {
        return dispatchEvent(eventName, new PayLoad(payload));
    }

    public boolean dispatchEvent(String eventName, PayLoad payLoad) {
        return false;
    }

    public void registerEventListener(EventListener listener, String... eventNames) {

    }

    public void unregisterAll(String... eventNames) {

    }

    public void unregisterEventListener(EventListener listener) {

    }

    public void unregisterEventListener(EventListener listener, String... eventNames) {

    }

    public interface EventListener {
        boolean onEvent(String eventName, PayLoad payLoad);
    }

    public interface RunnableDispatcher {
        void dispatch(Runnable runnable, int delay);

        void cancel(Runnable runnable);

        void release();
    }
}
