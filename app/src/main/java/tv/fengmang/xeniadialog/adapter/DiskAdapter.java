package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.utils.DiskHelper;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class DiskAdapter extends RecyclerView.Adapter<DiskAdapter.VH> {

    private static final String TAG = DiskAdapter.class.getSimpleName();
    private final static float GB_SIZE = 1024 * 1024 * 1024F;
    private Context mContext;
    private List<DiskHelper.StorageInfo> storageInfoList;

    public DiskAdapter(Context context, List<DiskHelper.StorageInfo> storageInfoList) {
        this.mContext = context;
        this.storageInfoList = storageInfoList;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_disk, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        DiskHelper.StorageInfo storageInfo = storageInfoList.get(position);
        holder.tvPath.setText(storageInfo.getPath());
        if (TextUtils.isEmpty(storageInfo.getUserLabel())) {
            holder.tvLabel.setText("无");
        } else {
            holder.tvLabel.setText(storageInfo.getUserLabel());
        }

        if (storageInfo.getTotalVolume() > 0) {
            holder.tvSizeInfo.setText(formatSizeInfo(storageInfo));
        } else {
            holder.tvSizeInfo.setText("未获取到");
        }
    }

    private String formatSizeInfo(DiskHelper.StorageInfo storageInfo) {
        float available = (storageInfo.getAvailableVolume() / GB_SIZE);
        float total = (storageInfo.getTotalVolume() / GB_SIZE);
        return String.format("可用:%.1fGB/总共:%.1fGB", available, total);
    }


    @Override
    public int getItemCount() {
        return storageInfoList == null ? 0 : storageInfoList.size();
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvLabel;
        private TextView tvPath;
        private TextView tvSizeInfo;


        public VH(View view) {
            super(view);
            tvLabel = itemView.findViewById(R.id.tv_disk_label);
            tvPath = itemView.findViewById(R.id.tv_disk_path);
            tvSizeInfo = itemView.findViewById(R.id.tv_size_info);
        }
    }
}
