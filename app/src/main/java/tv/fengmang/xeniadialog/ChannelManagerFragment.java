package tv.fengmang.xeniadialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import tv.fengmang.xeniadialog.adapter.ChannelAdapter;
import tv.fengmang.xeniadialog.adapter.ProvinceAdapter;
import tv.fengmang.xeniadialog.adapter.TvClassAdapter;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.bean.ProvincesBean;
import tv.fengmang.xeniadialog.db.BeanAdapter;
import tv.fengmang.xeniadialog.db.TbFavoriteChannel;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.net.LiveRetrofit;
import tv.fengmang.xeniadialog.widget.FocusFixedLinearLayoutManager;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;

/**
 * @desc 透明对话框
 **/
public class ChannelManagerFragment extends DialogFragment {

    private static final String TAG = "CMFragment";
    private final static String AVATRA = "http://thirdwx.qlogo.cn/mmopen/icAlKfoyTJSJc0ibEicia8icUiaaSn7rR1ItTtMQm5OrBOvTNqUGepQvtHxVYsLw87NwfNsPMHQ4sZSiaWdjLW7JAHPtjGFc4lHcib7m/132";
    private Context mContext;
    private View mRootView;
    private MenuRecyclerView recyclerTvClass;
    private MenuRecyclerView recyclerProvinces;
    private MenuRecyclerView recyclerChannel;
    private ProgressBar pbLoading;
    private SimpleDraweeView sdvUserIcon;
    private View rvSearch;
    private View rvEmptyChannel;
    private View mQrLayout;

    private ChannelAdapter channelAdapter;
    private ProvinceAdapter proviceAdapter;
    private TvClassAdapter tvClassAdapter;

    List<BaseLiveData.TvClass> tvClassList;
    Map<BaseLiveData.TvClass, List<BaseLiveData.Channel>> channelMap;
    Map<ProvincesBean.Province, List<BaseLiveData.Channel>> locationChannelMap;
    private ProvincesBean mProviceBean;

    private IndexActivity mAttachActivity;
    private BaseLiveData.TvClass mPlayingTvClass;
    private ProvincesBean.Province mPlayingProvince;
    private BaseLiveData.Channel mPlayingChannel;

    List<BaseLiveData.Channel> mChannelList = null;  //播放的channel list


    List<TbFavoriteChannel> tbFavoriteChannelList = null; //收藏数据库数据

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        ELog.d(TAG, "onAttach");
        mAttachActivity = (IndexActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ELog.d(TAG, "onCreateView");
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
        return dlg;
    }

    @Override
    public void onResume() {
        super.onResume();
        ELog.d(TAG, "onResume");
        Dialog dlg = getDialog();
        dlg.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        params.gravity = Gravity.LEFT | Gravity.TOP | Gravity.BOTTOM;
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

    private int mFocusedTvClassPosition;
    private int mFocusedProvincePosition;
    private int mFocusedChannelPosition;

    private int mPlayingTvClassPosition;
    private int mPlayingProvincePosition;
    private int mPlayingChannelPosition;


    //private
    private void initUI() {

        pbLoading = mRootView.findViewById(R.id.pb_loading);
        sdvUserIcon = mRootView.findViewById(R.id.sdv_login_icon);
        rvSearch = mRootView.findViewById(R.id.rv_search);


        sdvUserIcon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setHeadSimpleDraweeView(sdvUserIcon, Uri.parse(AVATRA), hasFocus);
                mQrLayout.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                if (hasFocus) {

                } else {

                }
            }
        });

        mQrLayout = mRootView.findViewById(R.id.qr_code_layout);
        rvEmptyChannel = mRootView.findViewById(R.id.rv_empty_channel);

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerTvClass = mRootView.findViewById(R.id.recycler_tv_class);
        recyclerProvinces = mRootView.findViewById(R.id.recycler_provinces);
        recyclerChannel = mRootView.findViewById(R.id.recycler_channel);
        recyclerTvClass.setViewName("recyclerTvClass");
        recyclerProvinces.setViewName("recyclerProvinces");
        recyclerChannel.setViewName("recyclerChannel");

        recyclerTvClass.setLayoutManager(new FocusFixedLinearLayoutManager(mContext));
        recyclerProvinces.setLayoutManager(new FocusFixedLinearLayoutManager(mContext));
        recyclerChannel.setLayoutManager(new FocusFixedLinearLayoutManager(mContext));
        recyclerProvinces.setCanFocusOutVertical(false);
        recyclerProvinces.setCanFocusOutVertical(false);
        recyclerChannel.setCanFocusOutVertical(false);


        recyclerTvClass.setOnItemFocusListener(new MenuRecyclerView.ItemFocusListener() {
            @Override
            public void onItemFocused(View itemView, int position) {
                Log.d(TAG, "recyclerTvClass.onItemFocused()" + position);
                if (mFocusedTvClassPosition != position) {
                    mFocusedTvClassPosition = position;
                    if (tvClassList.get(mFocusedTvClassPosition).getId() == 5) {
                        recyclerProvinces.setVisibility(View.VISIBLE);
                        proviceAdapter = new ProvinceAdapter(mContext, mProviceBean.getProvinces(), mPlayingProvince);
                        recyclerProvinces.setAdapter(proviceAdapter);
                        refreshLocaltionChannelList();
                    } else {
                        recyclerProvinces.setVisibility(View.GONE);
                        refreshChannelList();
                    }
                }
            }

            @Override
            public void onItemLostFocus(View itemView, int position) {

            }

        });

        recyclerProvinces.setOnItemFocusListener(new MenuRecyclerView.ItemFocusListener() {
            @Override
            public void onItemFocused(View itemView, int position) {
                Log.d(TAG, "recyclerProvinces.onItemFocused()" + position);
                if (mFocusedProvincePosition != position) {
                    mFocusedProvincePosition = position;
                    refreshLocaltionChannelList();
                }
            }

            @Override
            public void onItemLostFocus(View itemView, int position) {

            }
        });

        recyclerChannel.setOnItemFocusListener(new MenuRecyclerView.ItemFocusListener() {
            @Override
            public void onItemFocused(View itemView, int position) {
                Log.d(TAG, "recyclerChannel.onItemFocused()" + position);
                if (mFocusedChannelPosition != position) {
                    mFocusedChannelPosition = position;
                }
            }

            @Override
            public void onItemLostFocus(View itemView, int position) {

            }
        });


        recyclerTvClass.setOnItemSelectedListener(new MenuRecyclerView.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, int position) {
                TextView textView = itemView.findViewById(R.id.tv_class_name);
                textView.setTextColor(Color.parseColor("#D62708"));
            }

            @Override
            public void onItemUnSelected(View itemView, int position) {
                TextView textView = itemView.findViewById(R.id.tv_class_name);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });


        recyclerProvinces.setOnItemSelectedListener(new MenuRecyclerView.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, int position) {
                TextView textView = itemView.findViewById(R.id.tv_provice_name);
                textView.setTextColor(Color.parseColor("#D62708"));
            }

            @Override
            public void onItemUnSelected(View itemView, int position) {
                TextView textView = itemView.findViewById(R.id.tv_provice_name);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });


        recyclerChannel.setOnItemSelectedListener(new MenuRecyclerView.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, int position) {
                TextView textView = itemView.findViewById(R.id.tv_channel_name);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
            }

            @Override
            public void onItemUnSelected(View itemView, int position) {
                TextView textView = itemView.findViewById(R.id.tv_channel_name);
                textView.setTextColor(Color.parseColor("#888888"));
            }
        });

        recyclerChannel.setOnItemClickListener(new MenuRecyclerView.ItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                mPlayingTvClassPosition = mFocusedTvClassPosition;
                mPlayingTvClass = tvClassList.get(mPlayingTvClassPosition);
                tvClassAdapter.setPlayingTvClass(mPlayingTvClass);
                if (mPlayingTvClass.getId() == 5) {
                    mPlayingProvincePosition = mFocusedProvincePosition;
                    mPlayingProvince = mProviceBean.getProvinces().get(mPlayingProvincePosition);
                    Log.d("manager_fragment", "mPlayingProvicePosition:" + mPlayingProvincePosition);
                    Log.d("manager_fragment", "mPlayingProvince:" + mPlayingProvince.getId() + "," + mPlayingProvince.getName());
                    mAttachActivity.setPlayingProvince(mPlayingProvince);
                    proviceAdapter.setPlayingProvince(mPlayingProvince);
                    mPlayingChannelPosition = mFocusedChannelPosition;
                    mChannelList = locationChannelMap.get(mPlayingProvince);
                    mPlayingChannel = mChannelList.get(mPlayingChannelPosition);
                    channelAdapter.setPlayingChannel(mPlayingChannel);
                } else {
                    mPlayingProvince = null;
                    mAttachActivity.setPlayingProvince(mPlayingProvince);
                    mPlayingChannelPosition = mFocusedChannelPosition;
                    mChannelList = channelMap.get(mPlayingTvClass);
                    mPlayingChannel = mChannelList.get(mPlayingChannelPosition);
                    channelAdapter.setPlayingChannel(mPlayingChannel);
                }
                Log.d("manager_fragment", "mPlayingTvClassPosition:" + mPlayingTvClassPosition);
                Log.d("manager_fragment", "mPlayingTvClass:" + mPlayingTvClass.getId() + "," + mPlayingTvClass.getName());
                Log.d("manager_fragment", "mPlayingChannelPosition:" + mPlayingChannelPosition);
                Log.d("manager_fragment", "mPlayingChannel:" + mPlayingChannel.getChannelId() + "," + mPlayingChannel.getName());
                mAttachActivity.setPlayingTvClass(mPlayingTvClass);
                mAttachActivity.setPlayingChannel(mPlayingChannel);
            }
        });


        recyclerChannel.setOnItemLongClickListener(new MenuRecyclerView.ItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View itemView, int position) {
                final BaseLiveData.Channel channel = mChannelList.get(position);
                if (tbFavoriteChannelList == null || tbFavoriteChannelList.isEmpty()) {
                    addChannelToFavorite(channel);
                } else {
                    if (isChannelInTbFavorite(tbFavoriteChannelList, channel)) {
                        removeFavChannelFromDb(channel, position);
                    } else {
                        addChannelToFavorite(channel);
                    }
                }

                return true;
            }
        });
    }

    private void removeFavChannelFromDb(BaseLiveData.Channel channel, final int deletePosition) {
        TbFavoriteChannel deleteChannel = null;
        for (TbFavoriteChannel tbFavoriteChannel : tbFavoriteChannelList) {
            if (tbFavoriteChannel.getChannelId() == channel.getChannelId()) {
                deleteChannel = tbFavoriteChannel;
                break;
            }
        }
        Log.d(TAG, "tbFavoriteChannelList:" + tbFavoriteChannelList.size());
        boolean isLastPos = false;
        if (deletePosition == tbFavoriteChannelList.size() - 1) {
            isLastPos = true;
        }
        if (deleteChannel != null) {
            boolean result = deleteChannel.delete();        //数据库的删除了
            Log.d(TAG, "tbFavoriteChannelList:" + tbFavoriteChannelList.size());
            Log.d(TAG, "删除频道:" + channel.getChannelId() + "," + channel.getName() + ",结果:" + result);
            //焦点落在收藏分类上面
            if (mFocusedTvClassPosition == 0) {
                //禁用其他控件抢焦点能力
                sdvUserIcon.setFocusable(false);
                rvSearch.setFocusable(false);

                channelAdapter.removeTbFavoriteChannel(deleteChannel, true);

                final boolean finalIsLastPos = isLastPos;

                recyclerChannel.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mChannelList.isEmpty()) {
                            rvEmptyChannel.setVisibility(View.VISIBLE);
                            recyclerChannel.setVisibility(View.GONE);
                            recyclerTvClass.requestFocus();
                        } else {
                            if (finalIsLastPos) {
                                //最后一个位置被删除，焦点会移动到第一个item，这里强制让删除位置的上一个位置View获得焦点
                                //会闪烁，体验并不好，先这样写，以后再研究，看看有没有好方式
                                View child = recyclerChannel.getLayoutManager().findViewByPosition(deletePosition - 1);
                                if (child != null)
                                    child.requestFocus();
                            } else {
                                recyclerChannel.requestFocus();
                            }
                        }
                        resetOtherFocusable(); //恢复其他控件抢焦点能力
                    }
                });
            } else {
                channelAdapter.removeTbFavoriteChannel(deleteChannel, false);
                removeFavChannelFromList(channel);  //收藏分类的对应Channel要删掉
            }
        } else {
            Log.d(TAG, "没有找到要删除的频道:" + channel.getChannelId() + "," + channel.getName());
        }
        Log.d(TAG, "tbFavoriteChannelList:" + tbFavoriteChannelList.size());
    }

    //recyclerview删除item时候，其他地方会抢焦点，需要先禁用，删除完成后，再恢复焦点能力
    private void resetOtherFocusable() {
        recyclerChannel.post(new Runnable() {
            @Override
            public void run() {
                sdvUserIcon.setFocusable(true);
                rvSearch.setFocusable(true);
            }
        });
    }


    private boolean isChannelInTbFavorite(List<TbFavoriteChannel> tbFavoriteChannelList, BaseLiveData.Channel channel) {
        for (TbFavoriteChannel tbFavoriteChannel : tbFavoriteChannelList) {
            if (tbFavoriteChannel.getChannelId() == channel.getChannelId()) {
                return true;
            }
        }
        return false;
    }

    private void addChannelToFavorite(BaseLiveData.Channel channel) {
        TbFavoriteChannel tbFavoriteChannel = BeanAdapter.fromChannelBean(channel);
        long id = tbFavoriteChannel.insert();
        Log.d(TAG, "添加收藏频道:" + id + "," + channel.getName());
        addFavChannelToList(channel);
        channelAdapter.addTbFavoriteChannel(tbFavoriteChannel);
    }

    private void addFavChannelToList(BaseLiveData.Channel addChannel) {
        List<BaseLiveData.Channel> favoriteChannelList = channelMap.get(tvClassList.get(0));
        int pos = -1;
        for (int i = 0, size = favoriteChannelList.size(); i < size; i++) {
            if (favoriteChannelList.get(i).getChannelId() == addChannel.getChannelId()) {
                pos = i;
            }
        }

        if (pos == -1) {
            favoriteChannelList.add(addChannel);
        }
    }

    private void removeFavChannelFromList(BaseLiveData.Channel deletechannel) {
        List<BaseLiveData.Channel> favoriteChannelList = channelMap.get(tvClassList.get(0));
        if (favoriteChannelList == null || favoriteChannelList.isEmpty()) {
            return;
        }
        Log.d(TAG, "favoriteChannelList:" + favoriteChannelList.size());
        int pos = 0;
        for (int i = 0, size = favoriteChannelList.size(); i < size; i++) {
            if (favoriteChannelList.get(i).getChannelId() == deletechannel.getChannelId()) {
                pos = i;
            }
        }
        favoriteChannelList.remove(pos);
        Log.d(TAG, "favoriteChannelList:" + favoriteChannelList.size());
    }

    private void refreshChannelList() {
        mChannelList = channelMap.get(tvClassList.get(mFocusedTvClassPosition));
        if (mChannelList == null || mChannelList.isEmpty()) {
            rvEmptyChannel.setVisibility(View.VISIBLE);
            recyclerChannel.setVisibility(View.GONE);
        } else {
            rvEmptyChannel.setVisibility(View.GONE);
            recyclerChannel.setVisibility(View.VISIBLE);
            channelAdapter = new ChannelAdapter(mContext, mChannelList, tbFavoriteChannelList, mPlayingChannel);
            recyclerChannel.setAdapter(channelAdapter);
        }
    }

    private void refreshLocaltionChannelList() {
        mChannelList = locationChannelMap.get(mProviceBean.getProvinces().get(mFocusedProvincePosition));
        channelAdapter = new ChannelAdapter(mContext, mChannelList, tbFavoriteChannelList, mPlayingChannel);
        recyclerChannel.setAdapter(channelAdapter);
    }

    private void getData() {
        setHeadSimpleDraweeView(sdvUserIcon, Uri.parse(AVATRA), false);

        AsyncTask<Void, Void, Void> task = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                pbLoading.setVisibility(View.VISIBLE);
                rvSearch.setFocusable(false);
                sdvUserIcon.setFocusable(false);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                asset2ProvicesBean();
                Call<BaseLiveData> xCall = LiveRetrofit.getInstance().getApi().getBaseData("1", "1");
                try {
                    Response<BaseLiveData> response = xCall.execute();
                    if (response.isSuccessful()) {
                        BaseLiveData baseLiveData = response.body();
                        paseBaseData(baseLiveData);
                    } else {
                        Log.d("test", "code:" + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                fillData();
            }
        };

        task.execute();
    }


    private void fillData() {
        if (mPlayingTvClass != null) {
            Log.d("manager_fragment", "mPlayingTvClass:" + mPlayingTvClass.getId() + "," + mPlayingTvClass.getName());
        }

        if (mPlayingProvince != null) {
            Log.d("manager_fragment", "mPlayingProvince:" + mPlayingProvince.getId() + "," + mPlayingProvince.getName());
        }

        if (mPlayingChannel != null) {
            Log.d("manager_fragment", "mPlayingChannel:" + mPlayingChannel.getChannelId() + "," + mPlayingChannel.getName());
        }
        pbLoading.setVisibility(View.GONE);

        fillTvClass();

        if (mPlayingTvClass.getId() == 5) {
            fillProvince();
            fillChannel();
        } else {
            recyclerProvinces.setVisibility(View.GONE);
            mPlayingProvincePosition = 0;
            mPlayingProvince = null;
            fillChannel();
        }
    }

    private void fillTvClass() {
        if (mPlayingTvClass != null) {
            mPlayingTvClassPosition = getTvClassIndex(tvClassList, mPlayingTvClass);
        } else {
            mPlayingTvClassPosition = 3;
            mPlayingTvClass = tvClassList.get(mPlayingTvClassPosition); //默认央视
        }
        tvClassAdapter = new TvClassAdapter(mContext, tvClassList, mPlayingTvClass);
        recyclerTvClass.setAdapter(tvClassAdapter);
        mFocusedTvClassPosition = mPlayingTvClassPosition;
        recyclerTvClass.setDefaultSelect(mPlayingTvClassPosition);
        tvClassAdapter.setPlayingTvClass(mPlayingTvClass);
    }

    private void fillProvince() {
        recyclerProvinces.setVisibility(View.VISIBLE);
        proviceAdapter = new ProvinceAdapter(mContext, mProviceBean.getProvinces(), mPlayingProvince);
        recyclerProvinces.setAdapter(proviceAdapter);
        mPlayingProvincePosition = getProvinceIndex(mProviceBean.getProvinces(), mPlayingProvince);
        mFocusedProvincePosition = mPlayingProvincePosition;
        recyclerProvinces.setDefaultSelect(mPlayingProvincePosition);
        proviceAdapter.setPlayingProvince(mPlayingProvince);
    }

    private void fillChannel() {
        if (mPlayingTvClass.getId() == 5) {
            mChannelList = locationChannelMap.get(mPlayingProvince);
        } else {
            mChannelList = channelMap.get(mPlayingTvClass);
        }
        printChannelList(mChannelList);
        if (mChannelList == null || mChannelList.isEmpty()) {
            recyclerChannel.setVisibility(View.GONE);
            rvEmptyChannel.setVisibility(View.VISIBLE);
        } else {
            rvEmptyChannel.setVisibility(View.GONE);
            recyclerChannel.setVisibility(View.VISIBLE);

            if (mPlayingChannel != null) {
                mPlayingChannelPosition = getTChannelIndex(mChannelList, mPlayingChannel);
            } else {
                mPlayingChannelPosition = 0;
                mPlayingChannel = mChannelList.get(mPlayingChannelPosition);
            }

            channelAdapter = new ChannelAdapter(mContext, mChannelList, tbFavoriteChannelList, mPlayingChannel);
            recyclerChannel.setAdapter(channelAdapter);
            mFocusedChannelPosition = mPlayingChannelPosition;
            recyclerChannel.setDefaultSelect(mPlayingChannelPosition);
            rvSearch.setFocusable(true);
            sdvUserIcon.setFocusable(true);
            recyclerChannel.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            View child = recyclerChannel.getLayoutManager().findViewByPosition(mPlayingChannelPosition);
                            if (child != null) {
                                child.requestFocus();
                            }
                        }
                    }, 200);


            //channelAdapter.setPlayingChannel(mPlayingChannel);
        }


    }

    private void printChannelList(List<BaseLiveData.Channel> channelList) {
        for (BaseLiveData.Channel channel : channelList) {
            Log.d(TAG, channel.getChannelId() + "," + channel.getName());
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private int getTvClassIndex(List<BaseLiveData.TvClass> list, BaseLiveData.TvClass target) {
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).getId() == target.getId()) {
                return i;
            }
        }
        return -1;
    }

    private int getProvinceIndex(List<ProvincesBean.Province> provinceList, ProvincesBean.Province target) {
        for (int i = 0, len = provinceList.size(); i < len; i++) {
            if (provinceList.get(i).getId().equalsIgnoreCase(target.getId())) {
                return i;
            }
        }
        return -1;
    }

    private int getTChannelIndex(List<BaseLiveData.Channel> channelList, BaseLiveData.Channel target) {
        for (int i = 0, len = channelList.size(); i < len; i++) {
            if (channelList.get(i).getChannelId() == target.getChannelId()) {
                return i;
            }
        }
        return -1;
    }

    private void paseBaseData(BaseLiveData baseLiveData) {
        tvClassList = baseLiveData.getTvclassList();
        channelMap = new HashMap<>();
        locationChannelMap = new HashMap<>();
        final List<BaseLiveData.Channel> channelList = baseLiveData.getChannelList();
        for (BaseLiveData.TvClass tvClass : tvClassList) {
            if (tvClass.getId() == 5) {
                //地方台单独处理逻辑
                handleLocationChannel(baseLiveData, tvClass);
            } else {
                List<BaseLiveData.Channel> cateChannelList = new ArrayList<>();
                String tvClassId = String.valueOf(tvClass.getId());
                for (BaseLiveData.Channel channel : channelList) {
                    String[] channleCateArray = channel.getChannelCate().split(",");
                    if (isSub(channleCateArray, tvClassId)) {
                        cateChannelList.add(channel);
                    }
                }
                channelMap.put(tvClass, cateChannelList);
            }

        }
        tbFavoriteChannelList = SQLite.select().from(TbFavoriteChannel.class).queryList();
        addFavAndCommonChannel();
        //printMap();
    }


    //常用和收藏
    private void addFavAndCommonChannel() {
        BaseLiveData.TvClass tvClassFav = new BaseLiveData.TvClass();
        tvClassFav.setId(999999);
        tvClassFav.setType(1);
        tvClassFav.setAdtype(0);
        tvClassFav.setLiveType(1);
        tvClassFav.setName("收藏");
        List<BaseLiveData.Channel> favoriteChannelList = loadFavoriteChannelList();

        BaseLiveData.TvClass tvClassCommon = new BaseLiveData.TvClass();
        tvClassCommon.setId(1000000);
        tvClassCommon.setType(1);
        tvClassCommon.setAdtype(0);
        tvClassCommon.setLiveType(1);
        tvClassCommon.setName("常用");

        tvClassList.add(0, tvClassCommon);
        tvClassList.add(0, tvClassFav);

        channelMap.put(tvClassFav, favoriteChannelList);
        channelMap.put(tvClassCommon, new ArrayList<BaseLiveData.Channel>());
    }


    //地方台逻辑
    private void handleLocationChannel(BaseLiveData baseLiveData, BaseLiveData.TvClass tvClass) {
        final List<BaseLiveData.Channel> channelList = baseLiveData.getChannelList();
        for (ProvincesBean.Province province : mProviceBean.getProvinces()) {
            List<BaseLiveData.Channel> locationChannelList = new ArrayList<>();
            for (BaseLiveData.Channel channel : channelList) {
                if (province.getId().equalsIgnoreCase(channel.getProvince())) {
                    locationChannelList.add(channel);
                }
            }
            locationChannelMap.put(province, locationChannelList);
        }
    }

    private void printLocationMap() {
        Iterator<Map.Entry<ProvincesBean.Province, List<BaseLiveData.Channel>>> entries = locationChannelMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<ProvincesBean.Province, List<BaseLiveData.Channel>> entry = entries.next();
            ProvincesBean.Province key = entry.getKey();
            List<BaseLiveData.Channel> channelList = entry.getValue();
            Log.d("manager", "key:" + key.getName() + "," + key.getId());
            for (BaseLiveData.Channel channel : channelList) {
                Log.d("manager", "channel:" + channel.getName() + "," + channel.getChannelId());
            }
            Log.d("manager", "----------------------------------------------");
        }
    }

    private void printMap() {
        Iterator<Map.Entry<BaseLiveData.TvClass, List<BaseLiveData.Channel>>> entries = channelMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<BaseLiveData.TvClass, List<BaseLiveData.Channel>> entry = entries.next();
            BaseLiveData.TvClass key = entry.getKey();
            List channelList = entry.getValue();
            ELog.d("manager", "key:" + key.getId() + "," + key.getName());
            ELog.i("manager", "channelList:" + channelList.size());
        }
    }

    public void asset2ProvicesBean() {
        InputStream inputStream = null;

        try {
            inputStream = mContext.getResources().getAssets().open("provinces.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream == null) {
            return;
        }
        mProviceBean = new Gson().fromJson(new InputStreamReader(inputStream), ProvincesBean.class);
    }

    public void setPlayingTvClass(BaseLiveData.TvClass mPlayingTvClass) {
        this.mPlayingTvClass = mPlayingTvClass;
    }

    public void setPlayingProvince(ProvincesBean.Province mPlayingProvince) {
        this.mPlayingProvince = mPlayingProvince;
    }

    public void setPlayingChannel(BaseLiveData.Channel mPlayingChannel) {
        this.mPlayingChannel = mPlayingChannel;
    }

    private boolean isSub(String[] channleCateArray, String tcClsId) {
        if (TextUtils.isEmpty(tcClsId) || channleCateArray == null || channleCateArray.length == 0) {
            return false;
        }

        for (String channelCate : channleCateArray) {
            if (tcClsId.equalsIgnoreCase(channelCate)) {
                return true;
            }
        }
        return false;
    }


    private void setHeadSimpleDraweeView(SimpleDraweeView draweeView, Uri uri, boolean hasFocus) {
        //圆角图片
        RoundingParams rp = new RoundingParams();
        //设置边框颜色 宽度
        if (hasFocus) {
            rp.setBorder(Color.parseColor("#D62708"), getResources().getDimensionPixelOffset(R.dimen.size_6));
        } else {
            rp.setBorder(Color.parseColor("#CCCCCC"), getResources().getDimensionPixelOffset(R.dimen.size_6));
        }
        //设置圆角
        rp.setRoundAsCircle(true);
        GenericDraweeHierarchy build = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setRoundingParams(rp)
                .build();
        //图片
        draweeView.setHierarchy(build);

        ControllerListener<ImageInfo> listener = new ControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {

            }

            //图片加载成功的时候
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                //Log.d("menu", "加载微信用户头像成功");
            }

            @Override
            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {

            }

            //图片加载失败
            @Override
            public void onFailure(String id, Throwable throwable) {
                Log.d("menu", "加载微信用户头像失败");
            }

            @Override
            public void onRelease(String id) {
                Log.d("menu", "清理不用的图片资源");
            }
        };

        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)        //图片地址
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true)//播放gif 图片
                .setTapToRetryEnabled(true)  //点击重新加载时 可以重新加载4 次
                .setControllerListener(listener)
                .build();
        draweeView.setController(controller);

    }


    private List<BaseLiveData.Channel> loadFavoriteChannelList() {
        if (tbFavoriteChannelList == null || tbFavoriteChannelList.isEmpty()) {
            return new ArrayList<>();
        }
        List<BaseLiveData.Channel> favoriteChannelList = BeanAdapter.fromTbFavChannelList(tbFavoriteChannelList);
        return favoriteChannelList;
    }

}
