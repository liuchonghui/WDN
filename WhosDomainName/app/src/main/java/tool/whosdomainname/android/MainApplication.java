package tool.whosdomainname.android;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.android.imageloadercompact.ImageLoaderCompact;
import com.android.overlay.RunningEnvironment;

/**
 * @author liu_chonghui
 */
public class MainApplication extends Application {
    protected RunningEnvironment subSystem;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        if (getCurrentProcessName(this).contains("remoteprocess")) {
            return;
        }

        super.onCreate();

        ImageLoaderCompact.getInstance().init(this);

        if (subSystem == null) {
            subSystem = new RunningEnvironment("R.array.managers",
                    "R.array.tables");
        } else {
            subSystem = RunningEnvironment.getInstance();
        }
        subSystem.onCreate(this);
        subSystem.run(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        if (subSystem != null) {
            subSystem.onTerminate();
        }
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        Log.d("ENV", "onLowMemory");
        if (subSystem != null) {
            subSystem.onLowMemory();
        }
        super.onLowMemory();
    }

    @SuppressLint("NewApi")
    @Override
    public void onTrimMemory(int level) {
        Log.d("ENV", "onTrimMemory[" + level + "]");
        if (subSystem != null) {
            subSystem.onTrimMemory(level);
        }
        super.onTrimMemory(level);
    }

    private String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
