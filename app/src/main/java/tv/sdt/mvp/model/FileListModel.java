package tv.sdt.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import tv.sdt.mvp.contract.FileListContract;
import tv.sdt.mvp.contract.RxjavaContract;
import tv.sdt.mvp.ui.FileListActivity;
import tv.sdt.mvp.ui.TextReadActivity;

public class FileListModel implements FileListContract.Model {

    private final static String TAG = "FileListModel";
    private Context mContext;

    public FileListModel(Context context) {
        this.mContext = context;
    }



}
