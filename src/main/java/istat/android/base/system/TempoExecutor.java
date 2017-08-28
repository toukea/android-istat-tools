package istat.android.base.system;

import java.util.Date;
import istat.android.base.memories.SESSION;

public final class TempoExecutor {
	public static final String DEFAULT_TAG_LAST_MARK_TEMPO = "istat.android.base.system.TempoExecutor.MARK_TEMPO";
	private static boolean PERCISTANCE = false;
	private static String NAME_SPACE = "istat.android.base.system.TempoExecutor.NAME_SPACE";

	private TempoExecutor() {
	}

	public static void registerForNameSpace(String nameSpace) {
		NAME_SPACE = nameSpace;
	}

	public static boolean execute(long time, CallBack callBack) {
		return execute(DEFAULT_TAG_LAST_MARK_TEMPO, time, callBack);
	}

	public static boolean execute(String sessionTag, long time,
			CallBack callBack) {
		String currentNameSpace = SESSION.CURRENT_NAME_SPACE;
		boolean sessionOpen = SESSION.isOpen();
		if (sessionOpen) {
			//SESSION.open(NAME_SPACE);
			SESSION.put(DEFAULT_TAG_LAST_MARK_TEMPO,
					SESSION.retrieve(DEFAULT_TAG_LAST_MARK_TEMPO));
		}
		if (SESSION.retrieve(sessionTag) != null) {
			Date markDate = (Date) SESSION.retrieve(sessionTag);
			if (dateNow().after(new Date(markDate.getTime() + time))) {
				callBack.onTimeToExecute(dateNow().getTime()
						- markDate.getTime());
				SESSION.put(sessionTag, new Date(), PERCISTANCE);
				return true;
			}
		} else {
			SESSION.put(sessionTag, new Date(), PERCISTANCE);
		}
		if (sessionOpen) {
			//SESSION.registerForNameSpace(currentNameSpace);
		}

		return false;
	}

	public static long getLastMarkTime() {
		return ((Date) SESSION.get(DEFAULT_TAG_LAST_MARK_TEMPO)).getTime();
	}

	public static long getLastMarkTime(String sessionTag) {
		return ((Date) SESSION.get(sessionTag)).getTime();
	}

	public static void reInit() {
		SESSION.destroyTag(DEFAULT_TAG_LAST_MARK_TEMPO);
	}

	public static void reInitTAG(String sessionTag) {
		SESSION.destroyTag(sessionTag);
	}

	public static void enablePercistance(boolean value) {
		PERCISTANCE = value;
	}

	public static interface CallBack {
		public void onTimeToExecute(long time);
	}

	private static Date dateNow() {
		return new Date();
	}

}
