package com.cdy.mobiledesign.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.OrderDetialActivity;
import com.cdy.mobiledesign.R;
import com.cdy.mobiledesign.filepicker.FilePickerActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AuthAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String,Drawable>> mList;
    public AuthAdapter(Context mContext,List<Map<String,Drawable>> mList) {
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
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.auth_item, null,true);
            holder.img = convertView.findViewById(R.id.auth_pic);
            holder.buttonAuth = convertView.findViewById(R.id.auth_button);

            holder.buttonUpload = convertView.findViewById(R.id.auth_button_upload);
            holder.buttonUpload.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), FilePickerActivity.class);
                ((Activity)v.getContext()).startActivityForResult(intent,1);
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (this.mList != null) {
            Map map = mList.get(position);
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Object obj = i.next();
                String m_key = obj.toString();
                final String key = m_key.substring(0, m_key.indexOf("="));
                System.out.println("key: " + key);
                holder.img.setImageDrawable((Drawable)map.get(key));
                holder.buttonAuth.setOnClickListener(v -> {
                    try {
                        MySQLHelper.updateNumberMessage(key, "is_validate", "1");
                        Toast.makeText(v.getContext(), "验证通过！", Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = v.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("is_validate", "1");
                        editor.commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return convertView;
    }
    /*定义item对象*/
    public class ViewHolder {
        androidx.appcompat.widget.AppCompatImageView img;
        com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton buttonAuth;
        com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton buttonUpload;
    }
}