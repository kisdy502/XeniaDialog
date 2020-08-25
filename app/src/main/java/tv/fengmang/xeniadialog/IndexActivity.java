package tv.fengmang.xeniadialog;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.bean.ProvincesBean;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.log.LogUtils;
import tv.fengmang.xeniadialog.task.TransFormRunnable;
import tv.fengmang.xeniadialog.utils.RequestPermission;
import tv.fengmang.xeniadialog.widget.ForeverShowToast;
import tv.sdt.mvp.ui.CrashManagerActivity;
import tv.sdt.mvp.ui.DiskListActivity;
import tv.sdt.mvp.ui.LivePlayActivity;

public class IndexActivity extends AppCompatActivity {

    private ChannelManagerFragment channelManagerFragment;
    private SettingFragment settingFragment;

    private BaseLiveData.TvClass mPlayingTvClass;
    private ProvincesBean.Province mPlayingProvince;
    private BaseLiveData.Channel mPlayingChannel;

    private ForeverShowToast customToast;
    private MediaRecorder mMediaRecorder;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //
            //handler.sendEmptyMessageDelayed(1, 12000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChannelDialog();
            }
        });

        findViewById(R.id.show_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showSettingDialog();//
                startActivity(new Intent(IndexActivity.this, AsyncDownloadActivity.class));
            }
        });

        findViewById(R.id.check_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDialogFragment updateDialogFragment = (UpdateDialogFragment) UpdateDialogFragment.instantiate
                        (IndexActivity.this, UpdateDialogFragment.class.getName());
                updateDialogFragment.show(getSupportFragmentManager(), "updateDialogFragment");
            }
        });

        findViewById(R.id.jump_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, LivePlayActivity.class));
            }
        });

        findViewById(R.id.menu_opt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, RecordActivity.class));
            }
        });


        findViewById(R.id.menu_to_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, CrashManagerActivity.class);
                intent.putExtra("type", "debug");
                startActivity(intent);
            }
        });

        findViewById(R.id.menu_to_crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, CrashManagerActivity.class);
                intent.putExtra("type", "crash");
                startActivity(intent);
            }
        });

        findViewById(R.id.menu_to_disk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, DiskListActivity.class));
            }
        });

        findViewById(R.id.ip_to_mac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlCCTV8 = "http://ivi.bupt.edu.cn/hls/cctv8hd.m3u8";
                String urlCCTV1 = "http://111.13.111.242/otttv.bj.chinamobile.com/PLTV/88888888/224/3221226226/1.m3u8";
                String szTv = "http://111.13.111.242/otttv.bj.chinamobile.com/PLTV/88888888/224/3221226245/1.m3u8";
                String cctv2 = "http://live.aishang.ctlcdn.com/00000110240127_1/playlist.m3u8?CONTENTID=00000110240127_1&AUTHINFO=FABqh274XDn8fkurD5614gM2WskyVx2lTv2Q2v%2B%2FB0%2BA1dCWrX8Ya72zEnc7Vc30XaSdwYK%2BdePzag2MGUw%2FdE8ZRItRJMEe3Qlo0xingHerdFb4EhE%2ByR%2BZ9afgYmcQZ5Ti53Jkdog%2BQEIiSMiQqnSM9D%2FTuZbrVNcqM1nFFl7SK7R35nSfKdaQR5NmR9iGBZuqBYSnToEkYeB2tQYfrw%3D%3D&USERTOKEN=7HT0UxWHLD67z1i4zpgoeOB0O2BhR0KR";
                Intent intent = new Intent(IndexActivity.this, PlayerActivity.class);
                intent.putExtra("playUrl", cctv2);
                startActivity(intent);
            }
        });

        findViewById(R.id.to_image_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, ImageListActivity.class);
                startActivity(intent);
            }
        });


        handler.sendEmptyMessageDelayed(1, 3000);
        //testXtransForm();

//        customToast = new ForeverShowToast(MyApp.getInstance());
//        customToast.show("我是不会消失的吐司", -1);

        mMediaRecorder = new MediaRecorder();

        RequestPermission requestPermission = new RequestPermission();
        requestPermission.RequestPermission(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
//        if (customToast != null && customToast.isShowing()) {
//            customToast.hide();
//        }
        LogUtils.getInstance(MyApp.getInstance()).stop();
    }

    public void showChannelDialog() {
        if (channelManagerFragment == null) {
            channelManagerFragment = (ChannelManagerFragment) ChannelManagerFragment.instantiate
                    (this, ChannelManagerFragment.class.getName());
            channelManagerFragment.setPlayingTvClass(mPlayingTvClass);
            channelManagerFragment.setPlayingProvince(mPlayingProvince);
            channelManagerFragment.setPlayingChannel(mPlayingChannel);
            channelManagerFragment.show(getSupportFragmentManager(), "channel_manager_fragment");
        } else {
            channelManagerFragment.setPlayingTvClass(mPlayingTvClass);
            channelManagerFragment.setPlayingProvince(mPlayingProvince);
            channelManagerFragment.setPlayingChannel(mPlayingChannel);
            channelManagerFragment.show(getSupportFragmentManager(), "channel_manager_fragment");
        }
    }

    public void showSettingDialog() {
        if (settingFragment == null) {
            settingFragment = (SettingFragment) SettingFragment.instantiate
                    (this, SettingFragment.class.getName());
            settingFragment.show(getSupportFragmentManager(), "setting_fragment");
        } else {
            settingFragment.show(getSupportFragmentManager(), "setting_fragment");
        }
    }


    private void playByWeb() {
        Intent i = new Intent(this, WebAdActivity.class);
        i.putExtra("play_url", "http://db.sdsjyy.cn");
        startActivity(i);
    }

    private void check() {
        ELog.d("Activity", "is null:" + (channelManagerFragment == null));
        ELog.d("Activity", "is null:" + (settingFragment == null));
        if (channelManagerFragment != null) {
            Log.d("Activity", "is show:" + (channelManagerFragment.getDialog() != null));
        }
        if (settingFragment != null) {
            Log.d("Activity", "is show:" + (settingFragment.getDialog() != null));
        }
        ELog.i("activity", "---------------------------------");
    }

    private void getData() {

    }

    private void testXtransForm() {
        new Thread(new TransFormRunnable(this)).start();
    }

    public void setPlayingTvClass(BaseLiveData.TvClass mPlayingTvClass) {
        this.mPlayingTvClass = mPlayingTvClass;
    }

    public void setPlayingProvince(ProvincesBean.Province playingProvince) {
        this.mPlayingProvince = playingProvince;
    }

    public void setPlayingChannel(BaseLiveData.Channel mPlayingChannel) {
        this.mPlayingChannel = mPlayingChannel;
    }
}
