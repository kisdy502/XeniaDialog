package tv.danmaku.ijk.media.player;

/**
 * IjkPlayerConfigs
 * <p>
 * 注: 高分辨率开启硬解码，不支持的话会自动切换到软解。
 * (就算开启mediacodec，如果设备不支持，显示的解码器也是avcodec软解。)
 *
 * @author vio_wang
 * @date 2019-03-13
 */
public class IjkPlayerConfigs {

    public static void configPlayerOption(IjkMediaPlayer mediaPlayer) {
        //开启硬解
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);

        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);

		/*
		  framedrop 跳帧开关
		  CPU在处理视频帧的时候处理得太慢,会出现严重的视频音频不同步现象
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);

        //开启这个就黑屏，先关闭，等搞懂了原理
        //mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

		/*
		  skip_loop_filter
		  0 : 开启 画面质量高,解码开销大
		  48 : 关闭 画面质量差,解码开销小
		  设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);

        /**
         * 最大fps
         */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"max-fps",30);

        /*
         * ijkplyer 在播放部分视频时，调用seekTo的时候，会跳回到拖动前的位置.
         * 这是因为视频的关键帧的问题（GOP导致的），视频压缩比较高，而seek只支持关键帧，
         * 出现这个情况就是原始的视频文件中i帧比较少，播放器会在拖动的位置找最近的关键帧。
         * 所以，目前针对此问题ijkPlayer无解。
         */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        /**
         * 设置seekTo能够快速seek到指定位置并播放
         */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flags", "fastseek");

		/*
		  analyzemaxduration 播放前最大探测时间
		  设置为1, 可首屏秒开
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);

		/*
		  probesize 播放前的探测Size，默认是1M, 改小一点会出画面更快
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);

		/*
		  flush_packets 每处理一个packet之后刷新io上下文
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"flush_packets",1L);

        /**最大缓存3000毫秒 点播可以设置为0*/
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3000);

        /**无限读 直播开启，点播关闭*/
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);
		/*
		  packet-buffering 是否开启预缓冲
		  是否开启预缓冲，开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验，考虑到直播体验，不开
		  实测关闭后，画面不会暂停了，但会卡顿
		  0/1 关闭/开启
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);

		/*
		  reconnect 播放重连次数
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);

		/*
		  m3u8本地播放问题
		 */
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "crypto,file,http,https,tcp,tls,udp");
    }

    /**
     * 设置锋彩直播 cswtv 高清直播源 软解
     *
     * @param ijkMediaPlayer
     */
    public static void configPlayOption_cswtv(IjkMediaPlayer ijkMediaPlayer) {
        IjkMediaPlayer.native_setLogLevel(6);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-avc", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "protocol_whitelist",
                "async,cache,crypto,concat,ffconcat,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,tcp,tls,udp,ijkurlhook,data,rtsp,mmsh,mmst,mms,rtmp");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "protocol_whitelist",
                "ffconcat,file,http,https");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
    }


}
