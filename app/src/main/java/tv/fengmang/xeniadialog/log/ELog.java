package tv.fengmang.xeniadialog.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;


/**
 * Created by SDT13292 on 2017/4/27.
 */
public class ELog {

    private static final String prefix = "BeeLive_";

    public final static int LEVEL_V = 1;
    public final static int LEVEL_D = 2;
    public final static int LEVEL_I = 3;
    public final static int LEVEL_W = 4;
    public final static int LEVEL_E = 5;

    public final static int NO_LOG_MODE = 1;            //不输出任何日志
    public final static int ONLY_LOG_MODE = 1 << 2;     //输入日志信息
    public final static int ONLY_FILE_MODE = 1 << 3;    //日志存入文件
    public final static int LOG_FILE_MODE = 1 << 4;     //输入日志，并存入文件


    private static int tagLevel = LEVEL_D;
    private static int logMode;
    private static ECS ecs;

    private static void initECS(File logDir) {
        if (null != ecs) {
            return;         //避免重复创建 ECS对象
        }
        if (!logDir.isDirectory()) {
            throw new IllegalArgumentException("ELog dir must be a Directory!");
        }
        if (!logDir.exists()) {
            boolean dirOk = logDir.mkdirs();
            if (!dirOk) {
                throw new IllegalArgumentException("ELog dir can not use!");
            }
        }
        ecs = new ECS(logDir, 1048576L);
    }

    public static void init(int logMode, int tagLevel) {
        ELog.logMode = logMode;
        ELog.tagLevel = tagLevel;
    }

    public static void init(int logMode, int tagLevel, String logDir) {
        ELog.logMode = logMode;
        ELog.tagLevel = tagLevel;
        if (logMode == ONLY_FILE_MODE || logMode == LOG_FILE_MODE) {
            initECS(new File(logDir));
        }
    }

    public static void init(int logMode, int tagLevel, File logDir) {
        ELog.logMode = logMode;
        ELog.tagLevel = tagLevel;
        if (logMode == ONLY_FILE_MODE || logMode == LOG_FILE_MODE) {
            initECS(logDir);
        }
    }

    public static void v(String msg) {
        log(LEVEL_V, msg);
    }

    public static void v(String tag, String msg) {
        log(LEVEL_V, tag, msg);
    }

    public static void d(String msg) {
        log(LEVEL_D, msg);
    }

    public static void d(String tag, String msg) {
        log(LEVEL_D, tag, msg);
    }

    public static void i(String msg) {
        log(LEVEL_I, msg);
    }

    public static void i(String tag, String msg) {
        log(LEVEL_I, tag, msg);
    }

    public static void w(String msg) {
        log(LEVEL_W, msg);
    }


    public static void w(String tag, String msg) {
        log(LEVEL_W, tag, msg);
    }

    public static void e(String msg) {
        log(LEVEL_E, msg);
    }

    public static void e(String tag, String msg) {
        log(LEVEL_E, tag, msg);
    }

    private static void log(int currentLevel, String msg) {
        if (logMode == NO_LOG_MODE) {
            return;
        }
        if (tagLevel > currentLevel) {
            return;
        }
        final String tag = generateTag();
        realLog(currentLevel, tag, msg);
    }

    private static void log(int currentLevel, String tag, String msg) {
        if (logMode == NO_LOG_MODE) {
            return;
        }
        if (tagLevel > currentLevel) {
            return;
        }
        final String newTag = generateTag(tag);
        realLog(currentLevel, newTag, msg);
    }

    private static void realLog(int currentLevel, String tag, String msg) {
        if (logMode == ONLY_LOG_MODE) {
            realAndroidLog(currentLevel, tag, msg);
        } else if (logMode == ONLY_FILE_MODE) {
            realECS(currentLevel, tag, msg);
        } else if (logMode == LOG_FILE_MODE) {
            realAndroidLog(currentLevel, tag, msg);
            realECS(currentLevel, tag, msg);
        }
    }

    private static void realAndroidLog(int currentLevel, String tag, String msg) {
        if (currentLevel == LEVEL_V) {
            Log.v(tag, msg);
        } else if (currentLevel == LEVEL_D) {
            Log.d(tag, msg);
        } else if (currentLevel == LEVEL_I) {
            Log.i(tag, msg);
        } else if (currentLevel == LEVEL_W) {
            Log.w(tag, msg);
        } else if (currentLevel == LEVEL_E) {
            Log.e(tag, msg);
        }
    }

    private static void realECS(int currentLevel, String tag, String msg) {
        if (currentLevel == LEVEL_V) {
            ecs.v(tag, msg);
        } else if (currentLevel == LEVEL_D) {
            ecs.d(tag, msg);
        } else if (currentLevel == LEVEL_I) {
            ecs.i(tag, msg);
        } else if (currentLevel == LEVEL_W) {
            ecs.w(tag, msg);
        } else if (currentLevel == LEVEL_E) {
            ecs.e(tag, msg);
        }
    }


    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(prefix) ? tag : prefix.concat(tag);
        return tag;
    }

    private static String generateTag(String mTag) {
        if (!TextUtils.isEmpty(mTag)) {
            return prefix.concat(mTag);
        } else {
            return generateTag();
        }
    }

}
