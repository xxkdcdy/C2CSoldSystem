package com.cdy.mobiledesign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.filepicker.PickerManager;
import com.cdy.mobiledesign.util.AuthAdapter;
import com.qmuiteam.qmui.widget.QMUIWrapContentListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.PixelFormat.OPAQUE;

public class AuthPicActivity extends AppCompatActivity {
    QMUIWrapContentListView mQMUIWrapContentListView;
    private List<Map<String, Drawable>> mList = new ArrayList<>();
    SharedPreferences sp;
    private static int REQ_CODE = 0X01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_pic);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        initViews();
    }

    public void initViews(){
        mQMUIWrapContentListView = findViewById(R.id.AuthListView);
        setPicData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = PickerManager.getInstance().files.get(0).getPath();
        if(requestCode == REQ_CODE){
            if(str.contains(".jpg") || str.contains(".png") || str.contains(".bmp") || str.contains(".JPG") || str.contains(".PNG")) {
                String username = sp.getString("username", "");
                if(username != null && !username.equals("")){
                    MySQLHelper.readImage2DB(username, str, "pic_data");
                    setPicData();
                }

            }
        }
    }

    public void setPicData(){
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint textPaint=new Paint();
                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setStrokeWidth(8);
                textPaint.setTextSize(50);
                textPaint.setTextAlign(Paint.Align.CENTER);

                RectF rectF=new RectF(100, 100, 100, 100);
                String text="未上传";
                //计算baseline
                Paint.FontMetrics fontMetrics=textPaint.getFontMetrics();
                float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
                float baseline=rectF.centerY()+distance;
                canvas.drawText(text, rectF.centerX(), baseline, textPaint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return OPAQUE;
            }
        };

        String username = sp.getString("username", "");
        String targetPath = getFilesDir().getAbsolutePath() + File.separator + "interpreter" + File.separator + "pic.jpg";
        MySQLHelper.readDB2Image(username, targetPath, "pic_data");
        File file = new File(targetPath);
        if(file.exists()){
            try
            {
                FileInputStream fis = new FileInputStream(file);
                Bitmap bitmap  = BitmapFactory.decodeStream(fis);
                drawable = new BitmapDrawable(getResources(), bitmap);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        mList.clear();
        Map map = new HashMap();
        map.put(username, drawable);
        mList.add(map);
        mQMUIWrapContentListView.setAdapter(new AuthAdapter(AuthPicActivity.this, mList));
    }
}
