package tv.fengmang.xeniadialog.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class RecorderHelper {

    public static void record(MediaRecorder mMediaRecorder) {
        String fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".m4a";
        Log.d("RecorderHelper", "fileName:" + fileName);
        File destDir = new File(Environment.getExternalStorageDirectory() + "/test/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        String filePath = Environment.getExternalStorageDirectory() + "/test/" + fileName;
        Log.d("RecorderHelper", "filePath:" + filePath);



        try {
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.REMOTE_SUBMIX);  //会报错
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();

    }


    public static void stopRecord(MediaRecorder mMediaRecorder) {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
}
