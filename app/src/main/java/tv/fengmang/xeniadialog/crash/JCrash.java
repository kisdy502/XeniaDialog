package tv.fengmang.xeniadialog.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

public class JCrash {

    private static boolean initialized = false;
    private static String appId = null;
    private static String appVersion = null;
    private static File crashLogDir = null;

    public static void init(Context context, File logDir) {
        if (initialized) {
            return;
        }
        initialized = true;

        if (context == null) {
            return;
        }
        appId = getAppId(context);
        appVersion = getAppVersion(context);

        if (logDir != null) {
            crashLogDir = logDir;
        } else {
            crashLogDir = FileHelper.getInnerCrashDir(context);
        }
        JavaCrashHandler handler = new JavaCrashHandler(appId, appVersion, crashLogDir);
    }

    private static String getAppId(Context context) {
        return context.getPackageName();
    }

    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        String code = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            code = "Unknown";
        }
        return code;
    }

}
