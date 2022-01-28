package com.cdy.mobiledesign.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import cn.smssdk.SMSSDK;
import com.cdy.mobiledesign.Login.privacy.PrivacyHolder;
import com.cdy.mobiledesign.Login.util.DemoSpHelper;
import com.cdy.mobiledesign.MainActivity;
import com.cdy.mobiledesign.R;

/**
 * 开屏页，双击进入旧的短信demo
 */
public class SplashActivity extends Activity implements View.OnTouchListener {
    private GestureDetector gestureDetector;
    private Handler handler;
    private static final long DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView imageView = new ImageView(this);
        imageView.setId(R.id.ivSplash);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.smssdk_openpage_bg);
        setContentView(imageView);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            //双击
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                finish();
                return false;
            }
        });
        imageView.setOnTouchListener(this);
        handler = new Handler();

        //延时结束后的操作
        handler.postDelayed(() -> {
            //如果没有保存用户，就进登录界面
            SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
            String username = sp.getString("username", "");
            Long accessTime = sp.getLong("accessTime", 0);
            Long nowTime = System.currentTimeMillis();
            boolean is_inTime = nowTime - accessTime < 10*60*1000;    //10分钟内有效
            if(username.equals("") || username == null || !is_inTime) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
            else{
                //如果保存有用户，就直接进入主界面
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                Toast.makeText(SplashActivity.this, "自动登录用户:" + username, Toast.LENGTH_SHORT).show();
            }
            finish();
        }, DELAY);

        init();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void init() {
    	if (!DemoSpHelper.getInstance().isPrivacyGranted()) {
    		// 初始化MobTech隐私协议获取
			//PrivacyHolder.getInstance().init();
		}
		SMSSDK.setAskPermisionOnReadContact(true);
	}
}
