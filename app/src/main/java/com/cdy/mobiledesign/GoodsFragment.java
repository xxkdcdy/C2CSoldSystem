package com.cdy.mobiledesign;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.R;
import com.cdy.mobiledesign.util.Goods;
import com.cdy.mobiledesign.util.GoodsAdapter;
import com.qmuiteam.qmui.widget.QMUIWrapContentListView;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by panchengjia on 2016/12/15.
 */
public class GoodsFragment extends Fragment {
    private View view;
    QMUIPullRefreshLayout mQMUIPullRefreshLayout;
    QMUIWrapContentListView mQMUIWrapContentListView;
    private List<Goods> mList = new ArrayList<>();
    SharedPreferences sp;
    private String username;
    GoodsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //通过参数中的布局填充获取对应布局
        view =inflater.inflate(R.layout.fragment_goods,container,false);
        //共享首选项设置
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        initViews();

        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    public void initViews(){
        mQMUIPullRefreshLayout = view.findViewById(R.id.goodsRefreshLayout);
        mQMUIWrapContentListView = view.findViewById(R.id.goodsListView);

        //下拉刷新监听注册
        mQMUIPullRefreshLayout.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {
                mQMUIPullRefreshLayout.postDelayed(() -> {
                    //扫描时间
                    long scanTime = 0;
                    final QMUITipDialog tipDialog;

                    getList();
                    boolean flag = true;
                    boolean is_refresh = false;

                    while(flag) {
                        if (mList.size() > 0) {
                            //显示服务信息
                            adapter.notifyDataSetChanged();
                            is_refresh = true;
                            flag = false;
                        }
                        if ((System.currentTimeMillis() - scanTime) > 10000) {
                            scanTime = System.currentTimeMillis();
                        } else {
                            //超时退出
                            flag = false;
                        }
                    }
                    if (!is_refresh){
                        tipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                .setTipWord("扫描超时，请稍候重试")
                                .create();
                        tipDialog.show();

                        //显示两秒关闭
                        mQMUIWrapContentListView.postDelayed(() -> tipDialog.dismiss(),2000);

                    }
                    else{
                        tipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                .setTipWord("扫描成功")
                                .create();
                        tipDialog.show();

                        //显示两秒关闭
                        mQMUIWrapContentListView.postDelayed(() -> tipDialog.dismiss(),2000);
                    }
                    mQMUIPullRefreshLayout.finishRefresh();


                }, 3000);
            }
        });


        //订单项目获取以及订单处理按钮回调事件的注册

        getList();
        adapter = new GoodsAdapter(getActivity(), mList);
        adapter.setOnGoodItemClickListener(( good ) -> {
            String orderId = good.getGood_code();
            try {
                String state = MySQLHelper.queryStateById(orderId);
                if(!MySQLHelper.checkPermission(username)){
                    Toast.makeText(getActivity(), "账号权限不足，请绑定手机号，保证金在100以上，且通过身份验证", Toast.LENGTH_LONG).show();
                    return;
                }
                if (state.equals("未抢单")) {
                    MySQLHelper.updateStringOrder(orderId, "state", "已抢单");
                    MySQLHelper.updateStringOrder(orderId, "deliver", username);
                    MySQLHelper.insertProcess(orderId, "接单成功");
                }
                else{
                    Toast.makeText(getActivity(), "抢单失败，请刷新再试", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Toast.makeText(getActivity(), "抢单成功！", Toast.LENGTH_SHORT).show();
            mList.remove(good);
            adapter.notifyDataSetChanged();
        });
        mQMUIWrapContentListView.setAdapter(adapter);
    }

    public void getList(){
        mList.clear();
        List<HashMap<String, String>> list = MySQLHelper.queryAllOrderByNoUserName(username);
        if (list == null || list.size() == 0)
            return;
        Goods good;
        Map<String, String> map;
        for(int i = 0; i < list.size(); i++){
            map = list.get(i);
            String good_code = map.get("orderId");
            String good_address = map.get("address");
            String good_weight = map.get("weight");
            String good_fee = map.get("fee") + "元";
            String good_time = map.get("expectedtime");
            good_time = good_time.substring(0, good_time.indexOf("."));
            String good_state = map.get("state");
            String owner = map.get("owner");

            good = new Goods(good_code, good_address, good_weight, good_fee, good_time, good_state, owner);
            mList.add(good);
        }
    }
}