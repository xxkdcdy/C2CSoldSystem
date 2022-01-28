package com.cdy.mobiledesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.sql.SQLException;
import java.util.Calendar;

public class NewOrderActivity extends AppCompatActivity  implements View.OnClickListener, DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    EditText etAddress,etWeight,etFee,etDate, etTime, etPackName, etPackPhone, etPackCode;
    com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton btnNewOrder;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        if(MySQLHelper.checkPermission(username)) {
            setContentView(R.layout.activity_new_order);
            initViews();
        }
        else{
            QMUIEmptyView mEmptyView = new QMUIEmptyView(NewOrderActivity.this);
            mEmptyView.show(false, "账号权限不足", "请确认身份信息完整，保证金100元以上，且绑定了手机号码",
                    null, null);
            setContentView(mEmptyView);
        }
    }

    public void initViews(){
        etAddress = findViewById(R.id.etAddress);
        etWeight = findViewById(R.id.etWeight);
        etFee = findViewById(R.id.etFee);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etPackName = findViewById(R.id.etPackName);
        etPackPhone = findViewById(R.id.etPackPhone);
        etPackCode = findViewById(R.id.etPackCode);
        btnNewOrder = findViewById(R.id.btnNewOrder);

        etWeight.setOnClickListener(this);
        etDate.setOnClickListener(this);
        etTime.setOnClickListener(this);
        btnNewOrder.setOnClickListener(this);

        //设置不可编辑状态
        etFee.setFocusable(false);
        etDate.setFocusable(false);
        etTime.setFocusable(false);
        etWeight.setFocusable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.etWeight:
                final String[] items = new String[]{"小于1kg", "1—5kg","5kg以上"};
                new QMUIDialog.MenuDialogBuilder(NewOrderActivity.this)
                        .addItems(items, (dialog, which) -> {
                            etWeight.setText(items[which]);
                            String feeStr = "";
                            switch (which){
                                case 0:
                                    feeStr = "1元";
                                    break;
                                case 1:
                                    feeStr = "2元";
                                    break;
                                case 2:
                                    feeStr = "3元";
                                    break;
                            }
                            etFee.setText(feeStr);
                            dialog.dismiss();
                        })
                        .show();
                break;
            case R.id.etDate:
                //获取实例，包含当前年月日
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this,this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MARCH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.etTime:
                calendar = Calendar.getInstance();
                TimePickerDialog tdialog = new TimePickerDialog(this,this,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);
                tdialog.show();
                break;
            case R.id.btnNewOrder:
                String address = etAddress.getText().toString();
                String weight = etWeight.getText().toString();
                String fee = etFee.getText().toString();
                String expectedtime = etDate.getText().toString() + etTime.getText().toString();
                String pack_name = etPackName.getText().toString();
                String pack_phone = etPackPhone.getText().toString();
                String pack_code = etPackCode.getText().toString();
                if(address != null && !address.equals("") &&
                        weight != null && !weight.equals("") &&
                        fee != null && !fee.equals("") &&
                        expectedtime != null && !expectedtime.equals("") &&
                        pack_name != null && !pack_name.equals("") &&
                        pack_phone != null && !pack_phone.equals("") &&
                        pack_code != null && !pack_code.equals("")){
                    try {
                        MySQLHelper.insertOrder(username, address, weight, fee, expectedtime, pack_name, pack_phone, pack_code);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(NewOrderActivity.this, "订单添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(NewOrderActivity.this, "订单添加失败,请确保所有信息填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        int NowYear = calendar.get(Calendar.YEAR);
        int NowMonth = calendar.get(Calendar.MARCH);
        int Nowdate = calendar.get(Calendar.DAY_OF_MONTH);
        if(NowYear <= year && NowMonth <= month && Nowdate < dayOfMonth) {
            etDate.setText(String.format("%d-%d-%d ", year, month + 1, dayOfMonth));
        }
        else{
            Toast.makeText(NewOrderActivity.this, "请选择当前日期之后的日期", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String desc = String.format("%d:%d", hourOfDay, minute);
        etTime.setText(desc);
    }
}
