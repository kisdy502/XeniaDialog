package tv.fengmang.xeniadialog.utils;

import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DiskHelper {

    public final static String TAG = "FileHelper";

    public final static String KEY_USB = "USB";
    public final static String KEY_SD = "SD";
    public final static String KEY_EXT = "EXT";

    public String[] SDCARD_ARRAY = {
            "/storage/emulated/0",
            "/storage/sdcard0",
            "/mnt/sdcard/usbStorage",
            "/mnt/sdcard/usb_storage"
    };


    //兼容各种电视, 需要判断反射的方法是否存在,例如小米电视没有getUserLabel这个方法
    public static List<StorageInfo> getStoragePath(Context mContext) {
        List<StorageInfo> storageInfoList = new ArrayList<>();
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;

        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            if (length == 0) {
                return storageInfoList;
            }
            for (int i = 0; i < length; i++) {
                StorageInfo storageInfo = new StorageInfo();
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                //Log.d(TAG, "path:" + path);
                storageInfo.path = path;
                storageInfoList.add(storageInfo);
            }

            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                //Log.d(TAG, "userLabel:" + userLabel);
                storageInfoList.get(i).userLabel = userLabel;
            }

            for (int i = 0; i < length; i++) {
                getExtInfo(storageInfoList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storageInfoList;
    }


    private static void getExtInfo(StorageInfo storageInfo) {
        final StatFs statFs = new StatFs(storageInfo.path);
        long totalCount = statFs.getBlockCount();                //总共的block数
        long availableCount = statFs.getAvailableBlocks();       //获取可用的block数
        long size = statFs.getBlockSize();                       //每格所占的大小，一般是4KB==
        long availROMSize = availableCount * size;               //可用存储大小
        long totalROMSize = totalCount * size;                   //总大小
        storageInfo.availableVolume = availROMSize;
        storageInfo.totalVolume = totalROMSize;
    }

    public static class StorageInfo {
        private String path;
        private String userLabel;
        private long totalVolume;       // 总大小
        private long availableVolume;   // 可用大小

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getUserLabel() {
            return userLabel;
        }

        public void setUserLabel(String userLabel) {
            this.userLabel = userLabel;
        }

        public long getTotalVolume() {
            return totalVolume;
        }

        public void setTotalVolume(long totalVolume) {
            this.totalVolume = totalVolume;
        }

        public long getAvailableVolume() {
            return availableVolume;
        }

        public void setAvailableVolume(long availableVolume) {
            this.availableVolume = availableVolume;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
