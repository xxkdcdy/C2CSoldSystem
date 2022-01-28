package com.cdy.mobiledesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.util.LogisticsInfoAdapter;
import com.cdy.mobiledesign.util.LogisticsInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetialActivity extends Activity {
    private String orderId;
    TextView tvPackName, tvPackPhone,tvPackCode;
    String pack_name, pack_phone, pack_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detial);

        //获取传过来的编号信息
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");

        RecyclerView rv_logistics = findViewById(R.id.rv_logistics);
        tvPackName = findViewById(R.id.tvPackName);
        tvPackPhone = findViewById(R.id.tvPackPhone);
        tvPackCode = findViewById(R.id.tvPackCode);

        rv_logistics.setLayoutManager(new LinearLayoutManager(this));
        rv_logistics.setFocusable(false);
        //解决ScrollView嵌套RecyclerView出现的系列问题
        rv_logistics.setNestedScrollingEnabled(false);
        rv_logistics.setHasFixedSize(true);
        rv_logistics.setAdapter(new LogisticsInfoAdapter(this, R.layout.item_logistics, getData()));
    }

    private List<LogisticsInfoBean> getData() {
        List<LogisticsInfoBean> data = new ArrayList<>();
        LogisticsInfoBean bean;
        List<HashMap<String, String>> list = MySQLHelper.queryProcessById(orderId);
        System.out.println(orderId + "bean: " + list.toString());
        HashMap<String, String> map;
        if(list != null && !(list.size() == 0)){
            for(int i = 0 ; i < list.size(); i++){
                map = list.get(i);
                String acceptTime = map.get("acceptTime");
                acceptTime = acceptTime.substring(0, acceptTime.indexOf("."));
                String acceptStation = map.get("acceptStation");
                bean = new LogisticsInfoBean(acceptTime, acceptStation);
                data.add(bean);
            }
        }
        Map<String, String> detial_map = MySQLHelper.queryOneDetialById(orderId);
        pack_name = detial_map.get("pack_name");
        pack_phone = detial_map.get("pack_phone");
        pack_code = detial_map.get("pack_code");
        if(pack_name != null && !pack_name.equals("") &&
                pack_phone != null && !pack_phone.equals("") &&
                pack_code != null && !pack_code.equals("")) {
            if (data.size() == 1) {
                //只有一条流程信息，说明刚刚创建订单完成，不能显示所有的取件信息
                int length = pack_name.length() - 1;
                pack_name = pack_name.substring(0, 1);
                for (int i = 0; i < length; i++)
                    pack_name += "*";
                length = pack_phone.length() - 3;
                pack_phone = pack_phone.substring(0, 3);
                for (int i = 0; i < length; i++)
                    pack_phone += "*";
                length = pack_code.length() - 2;
                pack_code = pack_code.substring(0, 2);
                for (int i = 0; i < length; i++)
                    pack_code += "*";
            }
            tvPackName.setText("取件姓名：" + pack_name);
            tvPackPhone.setText("取件电话：" + pack_phone);
            tvPackCode.setText("取件号码：" + pack_code);
        }
        return data;
    }
}
