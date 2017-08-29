package istat.android.base.system;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Looper;

public class ProcessLaunchWatcher extends Thread {
	private ActivityManager manager;
	private Context context;
	private WatcherCallBack mCallBack;
	private boolean run = false;
	private String lastProcessName, firstProcessName;
	private String currentProcessName;

	public ProcessLaunchWatcher(Context con) {
		context = con;
		manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		firstProcessName = manager.getRunningAppProcesses().get(1).processName;
		lastProcessName = manager.getRunningAppProcesses().get(0).processName;
	}

	public void run() {
		Looper.prepare();

		while (run) {
			checkNewProcess();
		}
	}

	public synchronized void startWatching(WatcherCallBack callBack) {
		setOnCatchListener(callBack);
		start();
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		run = true;
		super.start();
	}

	public void setOnCatchListener(WatcherCallBack mCallBack) {
		this.mCallBack = mCallBack;
	}

	public void stopWatching() {
		run = false;
	}

	public String getFirstProcessName() {
		return firstProcessName;
	}

	public boolean isWatching() {
		return run;
	}

	private void checkNewProcess() {
		List<ActivityManager.RunningAppProcessInfo> processInfo = manager
				.getRunningAppProcesses();
		currentProcessName = processInfo.get(0).processName;
		if (!currentProcessName.equals(lastProcessName))
			mCallBack.onCatchNewProcess(currentProcessName);
		lastProcessName = currentProcessName;
	}

	public static interface WatcherCallBack {
		public abstract void onCatchNewProcess(String packageName);
	}
}