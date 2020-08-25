package tv.fengmang.xeniadialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import tv.fengmang.xeniadialog.download.DownloadManager;
import tv.fengmang.xeniadialog.download.MyConstants;


public class UpdateDialogFragment extends DialogFragment {

    private static final String TAG = "UpdateDialogFragment";

    private static String QQ_URL = "http://js.xiazaicc.com/apk4/totalapkv0.2.1_downcc.com.apk";
    private Context mContext;
    private View mRootView;
    private ProgressBar pdDownload;

    private TextView tvUpdate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_update_layout, container, false);
            initUI();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        getData();
        return mRootView;
    }


    private void getData() {

    }

    private void initUI() {
        tvUpdate = mRootView.findViewById(R.id.update_now);
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DownloadManager.getInstance().start(QQ_URL);
//                File outFile = new File(MyApp.getInstance().getDir("Download", Context.MODE_PRIVATE), "yaoshi.apk");
//                new DownloadUtils().download(QQ_URL, outFile);
            }
        });
        pdDownload = mRootView.findViewById(R.id.download_progress);
        DownloadManager.getInstance().setProgressListener(new DownloadManager.ProgressListener() {
            @Override
            public void progressChanged(long read, long contentLength, boolean done) {
                if (done) {
                    Log.d(TAG, "下载完成了");
                    Toast.makeText(MyApp.getInstance(), "下载完成", Toast.LENGTH_LONG).show();
                } else {
                    pdDownload.setProgress((int) (read * 100 / contentLength));
                }
            }
        });

        mRootView.findViewById(R.id.cancel_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = new Dialog(getActivity(), R.style.common_transparent_dialog);
        dlg.getWindow().setWindowAnimations(R.style.dialogWindowAnim3);
        return dlg;
    }


}
