package com.cdy.mobiledesign.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdy.mobiledesign.OrderDetialActivity;
import com.cdy.mobiledesign.R;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoodsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Goods> mList;
    GoodItemListener mGoodItemListener;
    public GoodsAdapter(Context mContext,List<Goods> mList) {
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
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_item, null,true);
            holder.good_code=(TextView) convertView.findViewById(R.id.good_code);
            holder.good_address=(TextView) convertView.findViewById(R.id.good_address);
            holder.good_weight=(TextView) convertView.findViewById(R.id.good_weight);
            holder.good_fee=(TextView)convertView.findViewById(R.id.good_fee);
            holder.good_time=(TextView) convertView.findViewById(R.id.good_time);
            holder.good_state=(TextView) convertView.findViewById(R.id.good_state);
            holder.button = convertView.findViewById(R.id.good_button);
            holder.button_control = convertView.findViewById(R.id.control_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (this.mList != null) {
            final Goods good = mList.get(position);
            holder.good_code.setText("订单编号：" + good.getGood_code());
            holder.good_address.setText("收货地址：" + good.getGood_address());
            holder.good_weight.setText("包裹重量：" + good.getGood_weight());
            holder.good_fee.setText("订单佣金：" + good.getGood_fee());
            holder.good_time.setText("预期时间：" + good.getGood_time());
            holder.good_state.setText("订单状态：" + good.getGood_state());
            holder.button.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), OrderDetialActivity.class);
                intent.putExtra("orderId", good.getGood_code());
                ((Activity)v.getContext()).startActivityForResult(intent,1);
            });
            holder.button_control.setOnClickListener(v -> {
                mGoodItemListener.onGoodItemClick(good);    //调用接口根据对应的Fragment采取措施
            });
        }
        return convertView;
    }
    /*定义item对象*/
    public class ViewHolder {
        TextView good_code;
        TextView good_address;
        TextView good_weight;
        TextView good_fee;
        TextView good_time;
        TextView good_state;
        com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton button;
        com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton button_control;
    }

    //回调接口，用来对数据项做出反应
    public interface GoodItemListener {
        void onGoodItemClick(Goods good);
    }

    public void setOnGoodItemClickListener (GoodItemListener  goodItemListener) {
        this.mGoodItemListener = goodItemListener;
    }
}
