package tv.fengmang.xeniadialog;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tv.fengmang.xeniadialog.adapter.ImageListAdapter;
import tv.fengmang.xeniadialog.bean.VideoBean;
import tv.fengmang.xeniadialog.widget.DividerGridItemDecoration;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;
import tv.fengmang.xeniadialog.widget.V7GridLayoutManager;

public class ImageListActivity extends AppCompatActivity {

    private static final String TAG = "ImageListActivity";

    private MenuRecyclerView recyclerImageList;
    private ImageListAdapter imageListAdapter;
    private List<VideoBean.Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        recyclerImageList = findViewById(R.id.recycler_image_list);
        int spanCount = 3;
        recyclerImageList.setLayoutManager(new V7GridLayoutManager(this, spanCount));
        int spacePixels = getResources().getDimensionPixelOffset(R.dimen.size_24);
        //recyclerImageList.addItemDecoration(new SpacesItemDecoration(spacePixels));
        recyclerImageList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacePixels, true));
        //recyclerImageList.addItemDecoration(new DividerGridItemDecoration(getApplication(),2,R.color.colorAccent));
        initData();
    }

    private void initData() {
        //1,创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //2,创建一个Request
        final Request request = new Request.Builder()
                .url("http://meta.beevideo.bestv.com.cn/videoApi/api2.0/videolist.action?chnId=3&vip=0&pn=1&ps=48&sourceId=19&tagId=1")
                .addHeader("channel", "mifeng")
                .build();
        //3,新建一个call对象
        Call call = mOkHttpClient.newCall(request);
        //4，请求加入调度，这里是异步Get请求回调
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "request list error:", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                VideoBean videoBean = new Gson().fromJson(response.body().string(), VideoBean.class);
                videoList = videoBean.getData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageListAdapter = new ImageListAdapter(getApplicationContext(), videoList);
                        recyclerImageList.setAdapter(imageListAdapter);
                    }
                });
            }

        });

    }


    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0)
                outRect.top = space;
        }
    }


    private static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount; //列数
        private int spacing; //间隔
        private boolean includeEdge; //是否包含边缘

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            //这里是关键，需要根据你有几列来判断
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing;
                outRect.right = spacing;
                //outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                //outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
                outRect.top = spacing;
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
