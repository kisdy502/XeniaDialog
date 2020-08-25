package tv.fengmang.xeniadialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkPlayerConfigs;

public class PlayerActivity extends AppCompatActivity implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener {
    private final static String TAG = "PlayerActivity";
    protected IjkMediaPlayer mMediaPlayer;
    private SurfaceView surfaceView;
    private VideoStatus mStatus;
    private boolean surfaceCreated;
    private String playUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        surfaceView = findViewById(R.id.surface_view);
        mMediaPlayer = new IjkMediaPlayer();
        IjkPlayerConfigs.configPlayerOption(mMediaPlayer);
        surfaceView.getHolder().addCallback(mCallback);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mStatus = VideoStatus.QUITED;
        playUrl = getIntent().getStringExtra("playUrl");
        Log.d(TAG, "playUrl:" + playUrl);
    }

    public void startPlay() {
        if (TextUtils.isEmpty(playUrl)) {
            return;
        }
        if (surfaceCreated) {
            mMediaPlayer.setDisplay(surfaceView.getHolder());
            try {
                mMediaPlayer.setDataSource(playUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.prepareAsync();
            mStatus = VideoStatus.PREPARE;
        }
    }


    private SurfaceHolder.Callback mCallback = new Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceCreated = true;
            startPlay();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceCreated = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStatus == VideoStatus.PLAY) {
            mStatus = VideoStatus.PAUSED;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        Log.d(TAG, ">>>onPrepared:" + (mp.hashCode() == mMediaPlayer.hashCode()));
        mStatus = VideoStatus.PLAY;

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        Log.d(TAG, ">>>onVideoSizeChanged:" + width + "," + height);
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        Log.d(TAG, ">>>onCompletion:" + (mp.hashCode() == mMediaPlayer.hashCode()));
        mStatus = VideoStatus.COMPLETED;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        Log.d(TAG, ">>>onInfo:" + (mp.hashCode() == mMediaPlayer.hashCode()));
        return false;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        Log.e(TAG, ">>>onError:" + (mp.hashCode() == mMediaPlayer.hashCode()));
        return false;
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        Log.e(TAG, ">>>onSeekComplete:" + (mp.hashCode() == mMediaPlayer.hashCode()));
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        Log.d(TAG, ">>>onBufferingUpdate:" + percent);
    }

    private enum VideoStatus {
        LOADING/* 加载中 */, PREPARE/* 准备中 */, PLAY/* 正在播放 */, PAUSED/* 暂停 */, QUITED/* 退出 */, COMPLETED/* 播放结束 */, SEEKING
        /* 快进快退 */
    }
}
