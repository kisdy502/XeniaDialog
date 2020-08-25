package tv.fengmang.xeniadialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * @desc 透明对话框
 **/
public class SettingFragment extends DialogFragment {

    private Context mContext;
    private View mRootView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_channel_manager_layout, container, false);
            initUI();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        getData();
        return mRootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = new Dialog(getActivity(), R.style.common_transparent_dialog);
        dlg.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    int action = event.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        dismiss();
                        return true;
                    } else if (action == KeyEvent.ACTION_UP) {
                        return true;
                    }
                }
                return false;
            }
        });
        return dlg;
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dlg = getDialog();
        dlg.getWindow().setWindowAnimations(R.style.dialogWindowAnim2);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        params.gravity = Gravity.RIGHT | Gravity.TOP | Gravity.BOTTOM;
        params.width = 420;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.getWindow().setAttributes(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected boolean isDeActived() {
        if (!isAdded() || isDetached()) {
            return true;
        }
        return false;
    }

    // private
    private void initUI() {

    }

    private void getData() {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}
