package com.cdy.mobiledesign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.util.VitalAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Context context;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton btn3;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton btn4;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton btnNewPwd;
    private com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton btnVital;
    private EditText et1;
    private EditText et2;
    private EditText etNewPwd;
    private EditText etVital;
    ListView vitalListView;
    private String username;
    Long d_time = new Long(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        context = this;

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btnNewPwd = findViewById(R.id.btnNewPwd);
        btnVital = findViewById(R.id.btnVital);
        vitalListView = findViewById(R.id.vitalListView);

        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        etNewPwd = findViewById(R.id.etNewPwd);
        etVital = findViewById(R.id.etVital);
        etVital.setFocusable(false);

        etVital.setOnClickListener(this);
        btnVital.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btnNewPwd.setOnClickListener(this);

        //注册回调
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn3://获取验证码
                String phone = et1.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(context, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.getVerificationCode("86", phone);
                    Toast.makeText(context, "正在获取验证码,手机号是：" + phone, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn4://发送验证码
                String phone2 = et1.getText().toString().trim();
                String code = et2.getText().toString().trim();
                if (TextUtils.isEmpty(phone2) || TextUtils.isEmpty(code)) {
                    Toast.makeText(context, "手机号和验证码均不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.submitVerificationCode("+86", phone2, code);
                    Toast.makeText(context, "手机号是：" + phone2 + "验证码是：" + code, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnNewPwd:
                if(etNewPwd.getText().length() < 6){
                    Toast.makeText(context, "密码不少于6位数", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        MySQLHelper.updateStringMessage(username, "password", etNewPwd.getText().toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "修改密码成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.etVital:
                final String[] items = new String[]{"一周内", "一月内","半年内"};
                new QMUIDialog.MenuDialogBuilder(AuthActivity.this)
                        .addItems(items, (dialog, which) -> {
                            etVital.setText(items[which]);
                            switch (which){
                                case 0:
                                    d_time = new Long((long)7*24*60*60*1000);
                                    break;
                                case 1:
                                    d_time = new Long((long)30*24*60*60*1000);
                                    break;
                                case 2:
                                    d_time = new Long((long)180*24*60*60*1000);
                                    break;
                            }
                            dialog.dismiss();
                        })
                        .show();
                break;
            case R.id.btnVital:
                if(d_time == 0){
                    Toast.makeText(AuthActivity.this, "请先选择时限", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<HashMap<String, String>> list = MySQLHelper.queryFeeByTime(username, d_time);
                    if(list != null && !(list.size() == 0)) {
                        vitalListView.setAdapter(new VitalAdapter(AuthActivity.this, list));
                    }
                }
                break;
        }
    }


    //防止内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    private EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            super.afterEvent(event, result, data);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    @SuppressWarnings("unchecked") HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    Log.d(TAG, username + "提交验证码成功--country=" + country + "--phone" + phone);
                    //开放权限
                    btnNewPwd.setEnabled(true);
                    btnVital.setEnabled(true);

                    //保存手机号码
                    try {
                        MySQLHelper.updateStringMessage(username, "tel", et1.getText().toString().trim());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.d(TAG, "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                Toast.makeText(AuthActivity.this, data.toString(), Toast.LENGTH_LONG).show();
                ((Throwable) data).printStackTrace();
            }
        }
    };
}
