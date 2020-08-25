package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

import tv.fengmang.xeniadialog.MyApp;
import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.bean.VideoBean;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.VH> {

    private static final String TAG = ImageListAdapter.class.getSimpleName();

    private final Context mContext;
    private List<VideoBean.Video> videoList;


    public ImageListAdapter(Context context, List<VideoBean.Video> videoList) {
        this.mContext = context;
        this.videoList = videoList;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_image, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        VideoBean.Video video = videoList.get(position);
        holder.tvName.setText(video.getName());
        //使用Controller加载图片
        holder.simpleDraweeView.setController(getLisPicController(video.getSrcPic()));
    }

    @Override
    public int getItemCount() {
        return videoList == null ? 0 : videoList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private SimpleDraweeView simpleDraweeView;

        public VH(View view) {
            super(view);
            tvName = itemView.findViewById(R.id.tv_name);
            simpleDraweeView = itemView.findViewById(R.id.sdv_collected);
        }
    }


    private static DraweeController getLisPicController(String photoUrl) {
        int width = MyApp.getInstance().getResources().getDimensionPixelOffset(R.dimen.size_300);
        int height = MyApp.getInstance().getResources().getDimensionPixelOffset(R.dimen.size_408);
        //Log.d(TAG, "width:" + width + "height:" + height);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(photoUrl)).
                setResizeOptions(new ResizeOptions(width, height)).build();
        return Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true).build();
    }


}
