package tv.fengmang.xeniadialog.crash;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;

public class FileHelper {

    public final static String TAG = "FileHelper";
    public final static String CRASH_DIR = "crash";

    public static File getExternalCrashDir(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        if (pm == null) {
            return null;
        }

        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", packageName));
        if (!permission) {
            return null;
        }
        boolean permission2 = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", packageName));
        if (!permission2) {
            return null;
        }
        return context.getExternalFilesDir(CRASH_DIR);
    }

    public static File getInnerCrashDir(Context context) {
        return context.getDir(CRASH_DIR, context.MODE_PRIVATE);
    }


    public final static String LOG_DIR = "adb_log";

    public static File getExternalLogDir(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        if (pm == null) {
            return null;
        }

        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_EXTERNAL_STORAGE", packageName));
        if (!permission) {
            return null;
        }
        boolean permission2 = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", packageName));
        if (!permission2) {
            return null;
        }
        return context.getExternalFilesDir(LOG_DIR);
    }

    public static File getInnerLogDir(Context context) {
        return context.getDir(LOG_DIR, context.MODE_PRIVATE);
    }


}
