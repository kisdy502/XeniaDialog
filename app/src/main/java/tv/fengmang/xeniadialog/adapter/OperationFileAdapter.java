package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.bean.Operation;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class OperationFileAdapter extends RecyclerView.Adapter<OperationFileAdapter.VH> {

    private static final String TAG = OperationFileAdapter.class.getSimpleName();
    private Context mContext;
    private List<Operation> operationList;

    public OperationFileAdapter(Context context, List<Operation> operationList) {
        this.mContext = context;
        this.operationList = operationList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_opration, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Operation operation = operationList.get(position);
        holder.tvOprationName.setText(operation.getName());
    }

    @Override
    public int getItemCount() {
        return operationList == null ? 0 : operationList.size();
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvOprationName;


        public VH(View view) {
            super(view);
            tvOprationName = itemView.findViewById(R.id.tv_opration_name);
        }
    }
}
