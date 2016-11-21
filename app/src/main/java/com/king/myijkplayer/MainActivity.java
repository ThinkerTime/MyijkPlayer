package com.king.myijkplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private FrameLayout videoLayout;
    private VideoPlayView videoItemView;
    private FrameLayout fullScreen;//全屏
    private RelativeLayout mShowView_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = this;
        setContentView(R.layout.activity_main);

        videoLayout = (FrameLayout) findViewById(R.id.layout_video);
        fullScreen = (FrameLayout) findViewById(R.id.full_screen);
        videoItemView = new VideoPlayView(context);
//        videoLayout.addView(videoItemView);
        mShowView_rl = (RelativeLayout) findViewById(R.id.showview);
        mShowView_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartPlay();
            }
        });
        initActions();
    }

    private void initActions() {
        videoItemView.setCompletionListener(new VideoPlayView.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
//                Log.d("data","播放完毕");
                //播放完还原播放界面
                FrameLayout frameLayout = (FrameLayout) videoItemView.getParent();
                videoItemView.stop();
                videoItemView.release();

                if (frameLayout != null && frameLayout.getChildCount() > 0) {
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();

                    if (itemView != null) {
                        itemView.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void onStartPlay(){
//        videoItemView.start("http://movie.ks.js.cn/flv/other/1_0.mp4");
//        videoItemView.start("/storage/emulated/0/lanjiejie/rec/1470991655992.mp4");
        videoItemView.start("http://obl2o62uy.bkt.clouddn.com/2016-08-15-15:04:52");
        FrameLayout frameLayout = (FrameLayout) videoItemView.getParent();
        if(frameLayout == null){
            videoLayout.addView(videoItemView);
        }
        mShowView_rl.setVisibility(View.INVISIBLE);

        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource("/storage/emulated/0/lanjiejie/rec/1470991655992.mp4");
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        Log.d("data","rotation："+rotation);
    }

    @Override
    protected void onResume() {
        Log.d("data","onResume");
        super.onResume();
        if (videoItemView == null) {
            videoItemView = new VideoPlayView(context);
        }
//        videoItemView.start();
        onStartPlay();
    }

    @Override
    protected void onDestroy() {
        Log.d("data","onDestroy");
        super.onDestroy();

        if (videoLayout == null) return;

        ViewGroup view = (ViewGroup) videoItemView.getParent();
        if (view != null) {
            view.removeAllViews();
        }

        videoItemView.stop();
        videoItemView.release();
        videoItemView.onDestroy();
        videoItemView = null;
    }

    @Override
    protected void onPause() {
        Log.d("data","onPause");
        super.onPause();
        if (videoItemView != null) {
            videoItemView.stop();

            //自己改造
            videoItemView.release();
            FrameLayout frameLayout = (FrameLayout) videoItemView.getParent();
            if (frameLayout != null && frameLayout.getChildCount() > 0) {
                frameLayout.removeAllViews();
                View itemView = (View) frameLayout.getParent();

                if (itemView != null) {
                    itemView.findViewById(R.id.showview).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        Log.d("data", "屏幕转换");
        super.onConfigurationChanged(newConfig);

        if (videoItemView != null) {
            videoItemView.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                fullScreen.setVisibility(View.GONE);
                fullScreen.removeAllViews();
                videoLayout.removeAllViews();
                videoLayout.addView(videoItemView);
                videoItemView.setShowContoller(true);
                videoItemView.setContorllerVisiable();
            } else {
                //横屏
                ViewGroup viewGroup = (ViewGroup) videoItemView.getParent();
                if (viewGroup == null)
                    return;
                viewGroup.removeAllViews();
                fullScreen.addView(videoItemView);
                fullScreen.setVisibility(View.VISIBLE);
            }
        } else {
            fullScreen.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

}
