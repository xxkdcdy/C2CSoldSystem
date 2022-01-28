package com.cdy.mobiledesign.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.MainActivity;
import com.cdy.mobiledesign.R;
import com.mob.tools.FakeActivity;
import com.mob.tools.utils.ResHelper;
import com.mob.tools.utils.SharePrefrenceHelper;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.UserInterruptException;
import com.cdy.mobiledesign.Login.util.DemoResHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import cn.smssdk.gui.CountryPage;

/**
 * 验证页，包括短信验证和语音验证，默认使用中国区号
 */
public class VerifyActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "VerifyActivity";
    private static final String[] DEFAULT_COUNTRY = new String[]{"中国", "42", "86"};
    private static final int COUNTDOWN = 60;
    private static final String TEMP_CODE = "1319972";
    private static final String KEY_START_TIME = "start_time";
    private static final int REQUEST_CODE_VERIFY = 1001;
    private TextView tvSms;
    private TextView tvUsername;
    private TextView tvCountry;
    private EditText etPhone;
    private EditText etCode;
    private TextView tvCode;
    private TextView tvVerify;
    private TextView tvToast;
    private String currentId;
    private String currentPrefix;
    private FakeActivity callback;
    private Toast toast;
    private Handler handler;
    private EventHandler eventHandler;
    private int currentSecond;
    private SharePrefrenceHelper helper;
    private MySQLHelper mySQLHelper;

    private boolean is_sms = true;    //是否使用手机验证码登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smssdk_verify_activity);
        initViews();
        initListener();

        //默认获取短信和验证按钮不可点击，输入达到规范后，可点击
        tvVerify.setEnabled(false);
        tvCode.setEnabled(false);
        //默认使用短信验证
        tvSms.setSelected(true);
        //默认使用中国区号
        currentId = DEFAULT_COUNTRY[1];
        currentPrefix = DEFAULT_COUNTRY[2];
        tvCountry.setText(getString(R.string.smssdk_default_country) + " +" + DEFAULT_COUNTRY[2]);
        helper = new SharePrefrenceHelper(this);
        helper.open("sms_sp");
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_VERIFY) {
			etCode.setText("");
			etPhone.setText("");
			// 重置"获取验证码"按钮
			tvCode.setText(R.string.smssdk_get_code);
			tvCode.setEnabled(true);
			if (handler != null) {
				handler.removeCallbacksAndMessages(null);
			}
		}
	}

	private void initViews() {
        tvSms = findViewById(R.id.tvSms);
        tvUsername = findViewById(R.id.tvUsername);
        tvCountry = findViewById(R.id.tvCountry);
        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        tvCode = findViewById(R.id.tvCode);
    }

    private void initListener() {
        findViewById(R.id.ivBack).setOnClickListener(this);
        tvSms.setOnClickListener(this);
        tvUsername.setOnClickListener(this);
        findViewById(R.id.ivSelectCountry).setOnClickListener(this);
        tvCode.setOnClickListener(this);
        tvVerify = findViewById(R.id.tvVerify);
        tvVerify.setOnClickListener(this);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //手机号输入大于5位，获取验证码按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCode.setEnabled(etPhone.getText() != null && etPhone.getText().length() > 5);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //验证码输入6位并且手机大于5位，验证按钮可点击
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvVerify.setEnabled(etCode.getText() != null && etCode.getText().length() >= 6 && etPhone.getText() != null && etPhone.getText().length() > 5);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //回调选中的国家
        callback = new FakeActivity() {
            @Override
            public void onResult(HashMap<String, Object> data) {
                if (data != null) {
                    int page = (Integer) data.get("page");
                    if (page == 1) {
                        currentId = (String) data.get("id");
                        String[] country = SMSSDK.getCountry(currentId);
                        if (country != null) {
                            tvCountry.setText(country[0] + " " + "+" + country[1]);
                            currentPrefix = country[1];
                        }
                    }
                }
            }
        };
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (tvCode != null) {
                    if (currentSecond > 0) {
                        tvCode.setText(getString(R.string.smssdk_get_code) + " (" + currentSecond + "s)");
                        tvCode.setEnabled(false);
                        currentSecond--;
                        handler.sendEmptyMessageDelayed(0, 1000);
                    } else {
                        tvCode.setText(R.string.smssdk_get_code);
                        tvCode.setEnabled(true);
                    }
                }
            }
        };

        eventHandler = new EventHandler() {
            public void afterEvent(final int event, final int result, final Object data) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    runOnUiThread(() -> {
                        //手机验证码提交验证成功，跳转成功页面，否则toast提示
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            ResultActivity.startActivityForResult(
                                    VerifyActivity.this, REQUEST_CODE_VERIFY, true, "+" + currentPrefix + " " + etPhone.getText());

                            HashMap<String, String> map = MySQLHelper.queryOneByUserName(etPhone.getText().toString());
                            if(map == null) {
                                //向数据库插入一条数据
                                mySQLHelper = new MySQLHelper();
                                try {
                                    mySQLHelper.insertUserTel(etPhone.getText().toString(), etPhone.getText().toString());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            //共享首选项保存登录用户名，方便下次直接登录
                            SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.putString("username", etPhone.getText().toString());
                            editor.putLong("accessTime", System.currentTimeMillis());   //保存登录的时间戳
                            editor.commit();

                            finish();

                        } else {
                            processError(data);
                        }
                    });
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE || event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (result == SMSSDK.RESULT_COMPLETE) {
								currentSecond = COUNTDOWN;
								handler.sendEmptyMessage(0);
								helper.putLong(KEY_START_TIME, System.currentTimeMillis());
							} else {
								if (data != null && (data instanceof UserInterruptException)) {
									// 由于此处是开发者自己决定要中断发送的，因此什么都不用做
									return;
								}
								processError(data);
							}
						}
					});
				}
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvSms:
                tvSms.setSelected(true);
                tvUsername.setSelected(false);
                tvCode.setVisibility(View.VISIBLE);
                tvCountry.setEnabled(true);
                etPhone.setHint("手机号");
                etPhone.setText("");
                etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
                etCode.setHint("验证码");
                etCode.setText("");
                etCode.setInputType(InputType.TYPE_CLASS_NUMBER);
                etCode.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                etCode.setFilters( new InputFilter[]{ new InputFilter.LengthFilter( 6 )});
                is_sms = true;
                break;
            case R.id.tvUsername:
                tvUsername.setSelected(true);
                tvSms.setSelected(false);
                tvCode.setVisibility(View.INVISIBLE);
                tvCountry.setEnabled(false);
                etPhone.setHint("用户名");
                etPhone.setText("");
                etPhone.setInputType(InputType.TYPE_CLASS_TEXT);
                etCode.setHint("密码");
                etCode.setText("");
                etCode.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etCode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                etCode.setFilters(new InputFilter[]{ new InputFilter.LengthFilter( 32 )});
                is_sms = false;
                break;
            case R.id.ivSelectCountry:
                //将当前国家带入跳转国家列表
                CountryPage countryPage = new CountryPage();
                countryPage.setCountryId(currentId);
                countryPage.showForResult(VerifyActivity.this, null, callback);
                break;
            case R.id.tvVerify:
                if (!isNetworkConnected()) {
                    Toast.makeText(VerifyActivity.this, getString(R.string.smssdk_network_error), Toast.LENGTH_SHORT).show();
                    break;
                }
                if(is_sms) {
                    //使用手机验证码登录
                    SMSSDK.submitVerificationCode(currentPrefix, etPhone.getText().toString().trim(), etCode.getText().toString());
                }
                else{
                    //使用账密登录
                    HashMap<String, String> map = MySQLHelper.queryOneByUserName(etPhone.getText().toString());
                    if(map != null) {
                        //检查到用户名存在
                        System.out.println(map.toString());
                        if(etCode.getText().toString().equals(map.get("password"))){
                            //密码正确
                            //共享首选项保存登录用户名，方便下次直接登录
                            SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.putString("username", etPhone.getText().toString());
                            editor.putLong("accessTime", System.currentTimeMillis());   //保存登录的时间戳
                            editor.commit();

                            //打开结果页面
                            ResultActivity.startActivityForResult(
                                    VerifyActivity.this, REQUEST_CODE_VERIFY, true,  "用户名：" + " " + etPhone.getText());
                            finish();
                        }
                        else{
                            //密码不正确
                            Toast.makeText(VerifyActivity.this, "密码不正确，请重试！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        //用户名不存在
                        new QMUIDialog.MessageDialogBuilder(VerifyActivity.this)
                                .setTitle("提示")
                                .setMessage("用户名不存在，要创建吗？")
                                .addAction(0, "创建", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        //向数据库插入一条数据
                                        mySQLHelper = new MySQLHelper();
                                        try {
                                            mySQLHelper.insertUserPwd(etPhone.getText().toString(), etCode.getText().toString());
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                        //共享首选项保存登录用户名，方便下次直接登录
                                        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("username", etPhone.getText().toString());
                                        editor.commit();

                                        //打开结果页面
                                        ResultActivity.startActivityForResult(
                                                VerifyActivity.this, REQUEST_CODE_VERIFY, true,  "用户名：" + " " + etPhone.getText());
                                        finish();

                                        Toast.makeText(VerifyActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("取消", (dialog, index) -> dialog.dismiss())
                                .show();
                    }

                }
                break;
            case R.id.tvCode:
                //获取验证码间隔时间小于1分钟，进行toast提示，在当前页面不会有这种情况，但是当点击验证码返回上级页面再进入会产生该情况
                long startTime = helper.getLong(KEY_START_TIME);
                if (System.currentTimeMillis() - startTime < COUNTDOWN * 1000) {
                    showErrorToast(getString(R.string.smssdk_busy_hint));
                    break;
                }
                if (!isNetworkConnected()) {
                    Toast.makeText(VerifyActivity.this, getString(R.string.smssdk_network_error), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (tvSms.isSelected()) {
                    //手机验证码
                    SMSSDK.getVerificationCode(currentPrefix, etPhone.getText().toString().trim(), null, null);

                    //SMSSDK.getVerificationCode(currentPrefix, etPhone.getText().toString().trim(), TEMP_CODE, null);
                } else {
                    SMSSDK.getVoiceVerifyCode(currentPrefix, etPhone.getText().toString().trim());
                }
//                currentSecond = COUNTDOWN;
//                handler.sendEmptyMessage(0);
//                helper.putLong(KEY_START_TIME, System.currentTimeMillis());
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    private void showErrorToast(String text) {
        if (toast == null) {
            toast = new Toast(this);
            View rootView = LayoutInflater.from(this).inflate(R.layout.smssdk_error_toast_layout, null);
            tvToast = rootView.findViewById(R.id.tvToast);
            toast.setView(rootView);
            toast.setGravity(Gravity.CENTER, 0, ResHelper.dipToPx(this, -100));
        }
        tvToast.setText(text);
        toast.show();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void processError(Object data) {
		int status = 0;
		// 根据服务器返回的网络错误，给toast提示
		try {
			((Throwable) data).printStackTrace();
			Throwable throwable = (Throwable) data;

			JSONObject object = new JSONObject(
					throwable.getMessage());
			String des = object.optString("detail");
			status = object.optInt("status");
			if (!TextUtils.isEmpty(des)) {
				showErrorToast(des);
				return;
			}
		} catch (Exception e) {
			Log.w(TAG, "", e);
		}
		// 如果木有找到资源，默认提示
		int resId = DemoResHelper.getStringRes(getApplicationContext(),
				"smsdemo_network_error");
		String netErrMsg = getApplicationContext().getResources().getString(resId);
		showErrorToast(netErrMsg);
	}
}
