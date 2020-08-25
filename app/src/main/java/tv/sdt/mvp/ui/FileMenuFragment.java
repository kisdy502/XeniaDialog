package tv.sdt.mvp.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.log.ELog;

public class FileMenuFragment extends DialogFragment {

    private final static String TAG = "FileMenuFragment";
    private Context mContext;
    private View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        ELog.d(TAG, "onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ELog.d(TAG, "onCreateView");
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_file_menu_layout, container, false);
            initUI();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        getData();
        return mRootView;
    }

    private void initUI() {

    }

    private void getData() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = new Dialog(getActivity(), R.style.common_transparent_dialog);
        return dlg;
    }


    @Override
    public void onResume() {
        super.onResume();
        ELog.d(TAG, "onResume");
        Dialog dlg = getDialog();
        dlg.getWindow().setWindowAnimations(R.style.dialogWindowAnim2);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        params.gravity = Gravity.RIGHT | Gravity.TOP | Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.getWindow().setAttributes(params);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ELog.d(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ELog.d(TAG, "onDestroyView");
    }

    protected boolean isDeActived() {
        if (!isAdded() || isDetached()) {
            return true;
        }
        return false;
    }

}
