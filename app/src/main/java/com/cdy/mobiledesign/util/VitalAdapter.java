package com.cdy.mobiledesign.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdy.mobiledesign.OrderDetialActivity;
import com.cdy.mobiledesign.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VitalAdapter extends BaseAdapter {
    private Context mContext;
    private List<HashMap<String, String>> mList;
    public VitalAdapter(Context mContext,List<HashMap<String, String>> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (mList==null) {
            return 0;
        }else {
            return this.mList.size();
        }
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (mList == null) {
            return null;
        } else {
            return this.mList.get(position);
        }
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.vital_item, null,true);
            holder.vital_orderId=(TextView) convertView.findViewById(R.id.vital_orderId);
            holder.vital_fee=(TextView) convertView.findViewById(R.id.vital_fee);
            holder.vital_time=(TextView) convertView.findViewById(R.id.vital_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (this.mList != null) {
            Map<String, String> map = mList.get(position);
            String vital_orderId = "订单编号：" + map.get("orderId");
            String vital_fee ="订单金额：" + map.get("fee") + "元";
            String vital_time = "完成时间：" + map.get("finishTime");
            holder.vital_orderId.setText(vital_orderId);
            holder.vital_fee.setText(vital_fee);
            holder.vital_time.setText(vital_time);
        }
        return convertView;
    }
    /*定义item对象*/
    public class ViewHolder {
        TextView vital_orderId;
        TextView vital_fee;
        TextView vital_time;
    }
}
