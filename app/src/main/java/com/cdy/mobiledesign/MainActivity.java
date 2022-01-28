package com.cdy.mobiledesign;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.filepicker.PickerManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {

    //fragment设置
    static final int NUM_ITEMS = 4;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private String[] strings = new String[]{"当前订单","抢单结果","我的订单","用户管理"};
    GoodsFragment mGoodsFragment;
    MyOrderFragment mMyOrderFragment;
    OrderPickedFragment mOrderPickedFragment;
    PersonalFragment pesonalFragment;
    SharedPreferences sharedPreferences;
    private static int REQ_CODE = 0X01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        //装载Fragment

        mGoodsFragment = new GoodsFragment();
        mOrderPickedFragment = new OrderPickedFragment();
        mMyOrderFragment = new MyOrderFragment();
        pesonalFragment = new PersonalFragment();

        fragmentList.add(mGoodsFragment);
        fragmentList.add(mOrderPickedFragment);
        fragmentList.add(mMyOrderFragment);
        fragmentList.add(pesonalFragment);
        initViews();

        initUserMessages();
    }

    private void  initViews(){
        //ViewPager适配Fragment
        TabLayout tab_layout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        MyAdapter fragmentAdater = new  MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdater);
        tab_layout.setupWithViewPager(viewPager);
    }

    public void initUserMessages(){
        //初始化登录用户的信息
        HashMap<String, String> map = MySQLHelper.queryOneByUserName(sharedPreferences.getString("username", ""));

        //共享首选项保存个人信息
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tel", map.get("tel"));    //电话号码
        editor.putString("gold", map.get("gold"));  //保证金
        editor.putString("is_validate", map.get("is_validate"));   //是否获得验证
        editor.putString("sex", map.get("sex"));   //性别
        editor.putString("nickname", map.get("nickname"));   //昵称

        editor.commit();
    }

    //ViewPager 适配 Fragment 的适配器
    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings[position];
        }
    }


}
