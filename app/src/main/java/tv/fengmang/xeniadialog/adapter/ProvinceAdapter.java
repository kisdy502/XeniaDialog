package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.bean.ProvincesBean;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.widget.JumpingView;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.VH> {

    private static final String TAG = ProvinceAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<ProvincesBean.Province> provincesList;
    private ProvincesBean.Province mPlayingProvince;

    public ProvinceAdapter(Context context, List<ProvincesBean.Province> provincesList, ProvincesBean.Province playingProvince) {
        this.mContext = context;
        this.provincesList = provincesList;
        mPlayingProvince = playingProvince;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_provices, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ProvincesBean.Province province = provincesList.get(position);
        holder.tvName.setText(province.getName() + "(" + position + ")");
        if (mPlayingProvince != null && mPlayingProvince.getId().equalsIgnoreCase(province.getId())) {
            holder.tvName.setTextColor(Color.parseColor("#D62708"));
            holder.jumpingView.setVisibility(View.VISIBLE);
            holder.jumpingView.startJump();
        } else {
            holder.tvName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.jumpingView.setVisibility(View.GONE);
            holder.jumpingView.stopJump();
        }
    }


    @Override
    public int getItemCount() {
        return provincesList == null ? 0 : provincesList.size();
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private JumpingView jumpingView;

        public VH(View view) {
            super(view);
            tvName = itemView.findViewById(R.id.tv_provice_name);
            jumpingView = itemView.findViewById(R.id.jumping_view);
        }
    }


    public void setPlayingProvince(ProvincesBean.Province playingProvince) {
        if (mPlayingProvince == null && playingProvince == null) {
            return;
        } else if (mPlayingProvince == null && playingProvince != null) {
            mPlayingProvince = playingProvince;
            int position = getPlayingPosition();
            if (position != -1) {
                notifyItemChanged(position);
            }
        } else if (mPlayingProvince != null && playingProvince == null) {
            int position = getPlayingPosition();
            mPlayingProvince = playingProvince;
            if (position != -1) {
                notifyItemChanged(position);
            }
        } else {
            if (isSameProvince(mPlayingProvince, playingProvince)) {
                return;
            }
            int oldPosition = getPlayingPosition();
            mPlayingProvince = playingProvince;
            int newPosition = getPlayingPosition();
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            if (newPosition != -1) {
                notifyItemChanged(newPosition);
            }
        }
    }

    private boolean isSameProvince(ProvincesBean.Province playingProvince, ProvincesBean.Province targetProvince) {
        return playingProvince.getId().equalsIgnoreCase(targetProvince.getId());
    }


    private int getPlayingPosition() {
        if (mPlayingProvince == null || provincesList == null || provincesList.isEmpty()) {
            return -1;
        } else {
            for (int i = 0, len = provincesList.size(); i < len; i++) {
                if (provincesList.get(i).getId().equalsIgnoreCase(mPlayingProvince.getId())) {
                    return i;
                }
            }
        }
        return -1;
    }


}
