package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.fengmang.xeniadialog.R;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class TextContentAdapter extends RecyclerView.Adapter<TextContentAdapter.VH> {

    private static final String TAG = TextContentAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> contentList;

    public TextContentAdapter(Context context, List<String> contentList) {
        this.mContext = context;
        this.contentList = contentList;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_content, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        String text = contentList.get(position);
        holder.tvName.setText(text);
    }


    @Override
    public int getItemCount() {
        return contentList == null || contentList.isEmpty() ? 0 : contentList.size();
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;


        public VH(View view) {
            super(view);
            tvName = (TextView) itemView;
        }
    }
}
