package tv.fengmang.xeniadialog.crash;

import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class CrashHelper {
    private final static String TAG = "CrashHelper";
    private final static String memInfoFmt = "%21s %8s\n";
    private final static String memInfoFmt2 = "%21s %8s %21s %8s\n";

    static String getStackTrace(Date startTime, Date crashTime,
                                Thread thread, Throwable throwable,
                                String appId, String appVersion) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stacktrace = sw.toString();

        return genCrashHeader(startTime, crashTime, appId, appVersion)
                + " tid: " + Process.myTid() + ", name: " + thread.getName() + "\n"
                + "java stacktrace:\n"
                + stacktrace
                + "\n";
    }

    static String genCrashHeader(Date startTime, Date crashTime, String appId, String appVersion) {
        DateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        return "-------------------------------Start--------------------------\n"
                + "Start time: '" + timeFormatter.format(startTime) + "'\n"
                + "Crash time: '" + timeFormatter.format(crashTime) + "'\n"
                + "App ID: '" + appId + "'\n"
                + "App version: '" + appVersion + "'\n"
                + "Rooted: '" + (isRoot() ? "Yes" : "No") + "'\n"
                + "API level: '" + Build.VERSION.SDK_INT + "'\n"
                + "OS version: '" + Build.VERSION.RELEASE + "'\n"
                + "ABI list: '" + getAbiList() + "'\n"
                + "Manufacturer: '" + Build.MANUFACTURER + "'\n"
                + "Brand: '" + Build.BRAND + "'\n"
                + "Model: '" + Build.MODEL + "'\n"
                + "Build fingerprint: '" + Build.FINGERPRINT + "'\n";
    }


    private static final String[] suPathname = {
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su",
            "/system/xbin/su",
            "/system/bin/su",
            "/system/bin/.ext/su",
            "/system/bin/failsafe/su",
            "/system/sd/xbin/su",
            "/system/usr/we-need-root/su",
            "/sbin/su",
            "/su/bin/su"};

    private static boolean isRoot() {
        try {
            for (String path : suPathname) {
                File file = new File(path);
                if (file.exists()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private static String getAbiList() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            return android.text.TextUtils.join(",", Build.SUPPORTED_ABIS);
        } else {
            String abi = Build.CPU_ABI;
            String abi2 = Build.CPU_ABI2;
            if (TextUtils.isEmpty(abi2)) {
                return abi;
            } else {
                return abi + "," + abi2;
            }
        }
    }

    static String getNetworkInfo() {
        if (Build.VERSION.SDK_INT >= 29) {
            return "network info:\n"
                    + "Not supported on Android Q (API level 29) and later.\n"
                    + "\n";
        } else {
            return "network info:\n"
                    + " TCP over IPv4 (From: /proc/PID/net/tcp)\n"
                    + getFileContent("/proc/self/net/tcp", 1024)
                    + "-\n"
                    + " TCP over IPv6 (From: /proc/PID/net/tcp6)\n"
                    + getFileContent("/proc/self/net/tcp6", 1024)
                    + "-\n"
                    + " UDP over IPv4 (From: /proc/PID/net/udp)\n"
                    + getFileContent("/proc/self/net/udp", 1024)
                    + "-\n"
                    + " UDP over IPv6 (From: /proc/PID/net/udp6)\n"
                    + getFileContent("/proc/self/net/udp6", 1024)
                    + "-\n"
                    + " ICMP in IPv4 (From: /proc/PID/net/icmp)\n"
                    + getFileContent("/proc/self/net/icmp", 256)
                    + "-\n"
                    + " ICMP in IPv6 (From: /proc/PID/net/icmp6)\n"
                    + getFileContent("/proc/self/net/icmp6", 256)
                    + "-\n"
                    + " UNIX domain (From: /proc/PID/net/unix)\n"
                    + getFileContent("/proc/self/net/unix", 256)
                    + "\n";
        }
    }

    static String getMemoryInfo() {
        return "memory info:\n"
                + " System Summary (From: /proc/meminfo)\n"
                + getFileContent("/proc/meminfo", 0)
                + "-\n"
                + " Process Status (From: /proc/PID/status)\n"
                + getFileContent("/proc/self/status", 0)
                + "-\n"
                + " Process Limits (From: /proc/PID/limits)\n"
                + getFileContent("/proc/self/limits", 0)
                + "-\n"
                + getProcessMemoryInfo()
                + "\n";
    }

    static String getProcessMemoryInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Process Summary (From: android.os.Debug.MemoryInfo)\n");
        sb.append(String.format(Locale.US, memInfoFmt, "", "Pss(KB)"));
        sb.append(String.format(Locale.US, memInfoFmt, "", "------"));

        try {
            Debug.MemoryInfo mi = new Debug.MemoryInfo();
            Debug.getMemoryInfo(mi);

            if (Build.VERSION.SDK_INT >= 23) {
                sb.append(String.format(Locale.US, memInfoFmt, "Java Heap:", mi.getMemoryStat("summary.java-heap")));
                sb.append(String.format(Locale.US, memInfoFmt, "Native Heap:", mi.getMemoryStat("summary.native-heap")));
                sb.append(String.format(Locale.US, memInfoFmt, "Code:", mi.getMemoryStat("summary.code")));
                sb.append(String.format(Locale.US, memInfoFmt, "Stack:", mi.getMemoryStat("summary.stack")));
                sb.append(String.format(Locale.US, memInfoFmt, "Graphics:", mi.getMemoryStat("summary.graphics")));
                sb.append(String.format(Locale.US, memInfoFmt, "Private Other:", mi.getMemoryStat("summary.private-other")));
                sb.append(String.format(Locale.US, memInfoFmt, "System:", mi.getMemoryStat("summary.system")));
                sb.append(String.format(Locale.US, memInfoFmt2, "TOTAL:", mi.getMemoryStat("summary.total-pss"), "TOTAL SWAP:", mi.getMemoryStat("summary.total-swap")));
            } else {
                sb.append(String.format(Locale.US, memInfoFmt, "Java Heap:", "~ " + mi.dalvikPrivateDirty));
                sb.append(String.format(Locale.US, memInfoFmt, "Native Heap:", String.valueOf(mi.nativePrivateDirty)));
                sb.append(String.format(Locale.US, memInfoFmt, "Private Other:", "~ " + mi.otherPrivateDirty));
                if (Build.VERSION.SDK_INT >= 19) {
                    sb.append(String.format(Locale.US, memInfoFmt, "System:", String.valueOf(mi.getTotalPss() - mi.getTotalPrivateDirty() - mi.getTotalPrivateClean())));
                } else {
                    sb.append(String.format(Locale.US, memInfoFmt, "System:", "~ " + (mi.getTotalPss() - mi.getTotalPrivateDirty())));
                }
                sb.append(String.format(Locale.US, memInfoFmt, "TOTAL:", String.valueOf(mi.getTotalPss())));
            }
        } catch (Exception e) {
            Log.i(TAG, "Util getProcessMemoryInfo failed", e);
        }

        return sb.toString();
    }

    static String getLogcat(int logcatMainLines, int logcatSystemLines, int logcatEventsLines) {
        int pid = android.os.Process.myPid();
        StringBuilder sb = new StringBuilder();

        sb.append("logcat:\n");

        if (logcatMainLines > 0) {
            getLogcatByBufferName(pid, sb, "main", logcatMainLines, 'D');
        }
        if (logcatSystemLines > 0) {
            getLogcatByBufferName(pid, sb, "system", logcatSystemLines, 'W');
        }
        if (logcatEventsLines > 0) {
            getLogcatByBufferName(pid, sb, "events", logcatSystemLines, 'I');
        }

        sb.append("\n");

        return sb.toString();
    }

    private static void getLogcatByBufferName(int pid, StringBuilder sb, String bufferName, int lines, char priority) {
        boolean withPid = (android.os.Build.VERSION.SDK_INT >= 24);
        String pidString = Integer.toString(pid);
        String pidLabel = " " + pidString + " ";

        //command for ProcessBuilder
        List<String> command = new ArrayList<String>();
        command.add("/system/bin/logcat");
        command.add("-b");
        command.add(bufferName);
        command.add("-d");
        command.add("-v");
        command.add("threadtime");
        command.add("-t");
        command.add(Integer.toString(withPid ? lines : (int) (lines * 1.2)));
        if (withPid) {
            command.add("--pid");
            command.add(pidString);
        }
        command.add("*:" + priority);

        //append the command line
        Object[] commandArray = command.toArray();
        sb.append("--------- tail end of log ").append(bufferName);
        sb.append(" (").append(android.text.TextUtils.join(" ", commandArray)).append(")\n");

        //append logs
        BufferedReader br = null;
        String line;
        try {
            java.lang.Process process = new ProcessBuilder().command(command).start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                if (withPid || line.contains(pidLabel)) {
                    sb.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Util run logcat command failed", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
    }


    private static String getFileContent(String pathname, int limit) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        String line;
        int n = 0;
        try {
            br = new BufferedReader(new FileReader(pathname));
            while (null != (line = br.readLine())) {
                String p = line.trim();
                if (p.length() > 0) {
                    n++;
                    if (limit == 0 || n <= limit) {
                        sb.append("  ").append(p).append("\n");
                    }
                }
            }
            if (limit > 0 && n > limit) {
                sb.append("  ......\n").append("  (number of records: ").append(n).append(")\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "Util getInfo(" + pathname + ") failed", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {
                }
            }
        }
        return sb.toString();
    }
}
