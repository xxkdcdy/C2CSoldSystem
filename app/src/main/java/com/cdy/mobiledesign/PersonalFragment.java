package com.cdy.mobiledesign;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.cdy.mobiledesign.DB.MySQLHelper;
import com.cdy.mobiledesign.Login.VerifyActivity;
import com.cdy.mobiledesign.filepicker.FilePickerActivity;
import com.cdy.mobiledesign.filepicker.PickerManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by panchengjia on 2016/12/15.
 */
public class PersonalFragment extends Fragment {
    private View view;
    QMUIGroupListView mGroupListView;
    com.qmuiteam.qmui.widget.QMUIRadiusImageView imgView;
    Drawable headImage;
    private static int REQ_CODE = 0X01;
    SharedPreferences sp;
    private String username;
    QMUICommonListItemView item4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //共享首选项设置
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        username = sp.getString("username", "");

        //通过参数中的布局填充获取对应布局
        view =inflater.inflate(R.layout.fragment_personal,container,false);
        initViews();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void initViews(){
        //条目布局设置
        mGroupListView = view.findViewById(R.id.groupListViewPersonal);
        imgView = view.findViewById(R.id.headImgView);
        setIcon();
        imgView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilePickerActivity.class);
            startActivityForResult(intent,REQ_CODE);
        });

        //第一个组，用来显示和编辑个人信息
        QMUICommonListItemView item1 = mGroupListView.createItemView("头像");
        item1.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        ImageView imgView = new ImageView(getActivity());
        imgView.setImageDrawable(getResources().getDrawable(R.drawable.home_page_phone));
        item1.addAccessoryCustomView(imgView);

        String nickname = sp.getString("nickname", "");
        QMUICommonListItemView item2 = mGroupListView.createItemView("昵称");
        if(nickname != null && !nickname.equals("")){
            item2.setDetailText(nickname);
        }
        else {
            item2.setDetailText("未设置");
        }

        String sex = sp.getString("sex", "");
        QMUICommonListItemView item3 = mGroupListView.createItemView("性别");
        if(sex != null && !sex.equals("")){
            item3.setDetailText(sex);
        }
        else{
            item3.setDetailText("未设置");
        }


        String validate = sp.getString("is_validate", "");
        item4 = mGroupListView.createItemView("身份认证");
        if(validate != null && !validate.equals("") && !validate.equals("0")) {
            item4.setDetailText("已认证");
        }
        else {
            item4.setDetailText("未认证");
        }
        item4.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        String tel = sp.getString("tel", "");
        QMUICommonListItemView item5 = mGroupListView.createItemView("绑定手机");
        if(tel != null && !tel.equals("")) {
            item5.setDetailText("已绑定");
        }
        else{
            item5.setDetailText("未绑定");
        }
        item5.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.Section section1 = QMUIGroupListView.newSection(getActivity());
        section1.setTitle("个人信息")
                .addItemView(item2, v ->{
                    final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                    builder.setTitle("昵称")
                            .setPlaceholder("在此输入您的昵称")
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .addAction("取消", (dialog, index) -> dialog.dismiss())
                            .addAction("确定", (dialog, index) -> {
                                CharSequence text = builder.getEditText().getText();
                                if (text != null && text.length() > 0) {
                                    try {
                                        MySQLHelper.updateStringMessage(username, "nickname", text.toString());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("nickname", text.toString());
                                    editor.commit();
                                    item2.setDetailText(sp.getString("nickname", ""));
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "请填入昵称", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                })
                .addItemView(item3, v ->{
                    final String[] items = new String[]{"男", "女"};
                    new QMUIDialog.MenuDialogBuilder(getActivity())
                            .addItems(items, (dialog, which) -> {
                                try {
                                    MySQLHelper.updateStringMessage(username, "sex", items[which]);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("sex", items[which]);
                                editor.commit();
                                item3.setDetailText(sp.getString("sex", ""));
                                dialog.dismiss();
                            })
                            .show();
                })
                .addItemView(item4, v -> {
                    Intent intent = new Intent(getActivity(), AuthPicActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                })
                .addItemView(item5, v ->{
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    String username = sp.getString("username", "");
                    intent.putExtra("username", username);
                    startActivity(intent);
                })
                .addTo(mGroupListView);

        //第二组，用来增加一些功能
        String gold = sp.getString("gold", "0");
        QMUICommonListItemView item6 = mGroupListView.createItemView("保证金管理");
        item6.setDetailText(gold + "元");
        item6.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView item7 = mGroupListView.createItemView("统计收入");
        item7.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView item8 = mGroupListView.createItemView("重置密码");
        item8.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView item9 = mGroupListView.createItemView("退出登录");
        item9.setBackgroundColor(Color.rgb(255,0,0));

        QMUICommonListItemView item10 = mGroupListView.createItemView("新建订单");
        item8.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.Section section2 = QMUIGroupListView.newSection(getActivity());
        section2.setTitle("操作")
                .addItemView(item6, v -> {
                    final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                    builder.setTitle("保证金")
                            .setPlaceholder("在此输入您的保证金")
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .addAction("取消", (dialog, index) -> dialog.dismiss())
                            .addAction("确定", (dialog, index) -> {
                                CharSequence text = builder.getEditText().getText();
                                if (text != null && text.length() > 0) {
                                    try {
                                        MySQLHelper.updateNumberMessage(username, "gold", text.toString());
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("gold", text.toString());
                                    editor.commit();
                                    item6.setDetailText(sp.getString("gold", "") + "元");
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "请填入金额", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                })
                .addItemView(item10, v -> {
                    Intent intent = new Intent(getActivity(), NewOrderActivity.class);
                    intent.putExtra("username", username);
                    getActivity().startActivity(intent);
                })
                .addItemView(item7, v ->{
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    String username = sp.getString("username", "");
                    intent.putExtra("username", username);
                    startActivity(intent);
                })
                .addItemView(item8, v ->{
                    Intent intent = new Intent(getActivity(), AuthActivity.class);
                    String username = sp.getString("username", "");
                    intent.putExtra("username", username);
                    startActivity(intent);
                })
                .addItemView(item9, v ->{
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("退出程序")
                            .setMessage("确定要清楚登录状态并退出程序吗？")
                            .addAction("取消", (dialog, index) -> dialog.dismiss())
                            .addAction(0, "确认", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index) -> {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("username");
                                editor.remove("accessTime");
                                editor.commit();
                                Intent intent = new Intent(getActivity(), VerifyActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                                dialog.dismiss();
                            })
                            .show();
                })
                .addTo(mGroupListView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = PickerManager.getInstance().files.get(0).getPath();
        if(requestCode == REQ_CODE){
            if(str.contains(".jpg") || str.contains(".png") || str.contains(".bmp") || str.contains(".JPG") || str.contains(".PNG")) {
                String username = sp.getString("username", "");
                if(username != null && !username.equals("")){
                    MySQLHelper.readImage2DB(username, str, "icon");
                    setIcon();
                }

            }
        }
    }

    public void setIcon(){
        Drawable drawable= getResources().getDrawable(R.drawable.ic_launcher_foreground);

        String username = sp.getString("username", "");
        String targetPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + "interpreter" + File.separator + "pic.jpg";
        MySQLHelper.readDB2Image(username, targetPath, "icon");
        File file = new File(targetPath);
        if(file.exists()){
            System.out.println("im in");
            try
            {
                FileInputStream fis = new FileInputStream(file);
                int length = fis.available();
                if(length > 100) {
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    drawable = new BitmapDrawable(getResources(), bitmap);
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(drawable != null){
            imgView.setImageDrawable(drawable);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        String is_validate = sp.getString("is_validate", "0");
        if(is_validate != null && !is_validate.equals("")){
            if(is_validate.equals("1")){
                item4.setDetailText("已认证");
            }
            else{
                item4.setDetailText("未认证");
            }
        }
    }
}