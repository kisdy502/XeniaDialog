package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tv.fengmang.xeniadialog.R;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class CrashAdapter extends RecyclerView.Adapter<CrashAdapter.VH> {

    private static final String TAG = CrashAdapter.class.getSimpleName();

    private Context mContext;
    private String[] fileList;

    public CrashAdapter(Context context, String[] fileList) {
        this.mContext = context;
        this.fileList = fileList;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_crash_file, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        String fileName = fileList[position];
        holder.tvName.setText(fileName);
    }


    @Override
    public int getItemCount() {
        return fileList == null ? 0 : fileList.length;
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;


        public VH(View view) {
            super(view);
            tvName = itemView.findViewById(R.id.tv_crash_file);
        }
    }
}
