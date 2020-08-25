package tv.fengmang.xeniadialog.download;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 多任务下载管理类
 */
public class DownloadHelper {

    private static ConcurrentHashMap<String, List<ProgressListener>> progressListenerListMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, AsyncDownload> downloadMap = new ConcurrentHashMap<>();

    public static void start(@NonNull String downloadUrl, @NonNull File saveDir, @NonNull String saveFileName, @Nullable ProgressListener listener) {
        List<ProgressListener> listenerList = getListenerList(downloadUrl);
        if (downloadMap.containsKey(downloadUrl)) {
            if (!listenerList.contains(listener) && listener != null) {
                listenerList.add(listener);
                AsyncDownload asyncDownload = downloadMap.get(downloadUrl);
                if (asyncDownload != null) {
                    asyncDownload.refreshProgressListener(listenerList);
                }
            }
        } else {
            AsyncDownload asyncDownload = new AsyncDownload(downloadUrl);
            downloadMap.put(downloadUrl, asyncDownload);
            if (!listenerList.contains(listener) && listener != null) {
                listenerList.add(listener);
            }
            asyncDownload.download(saveDir, saveFileName);
        }
    }


    public static void stop(String downloadUrl) {
        AsyncDownload asyncDownload = downloadMap.get(downloadUrl);
        if (asyncDownload != null) {
            asyncDownload.cancel();
        }
        removeListenerAndTask(downloadUrl);
    }

    public static void release(){

    }
    /**
     * 下载成功，或失败，都讲map清理掉
     *
     * @param downloadUrl
     */
    static void removeListenerAndTask(String downloadUrl) {
        List<ProgressListener> listenerList = progressListenerListMap.get(downloadUrl);
        if (listenerList == null || listenerList.isEmpty()) {
            return;
        }
        progressListenerListMap.remove(downloadUrl);
        downloadMap.remove(downloadUrl);
    }

    static List<ProgressListener> getListenerList(String downloadUrl) {
        List<ProgressListener> listenerList = progressListenerListMap.get(downloadUrl);
        if (listenerList == null || listenerList.isEmpty()) {
            progressListenerListMap.put(downloadUrl, new ArrayList<ProgressListener>());
            listenerList = progressListenerListMap.get(downloadUrl);
        }
        return listenerList;
    }


    public interface ProgressListener {

        void onFailed(String downloadUrl, int result, String desc);

        void onUpdate(String downloadUrl, long bytesRead, long contentLength, boolean done);
    }

}
