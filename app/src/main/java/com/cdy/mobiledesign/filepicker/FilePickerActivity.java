package com.cdy.mobiledesign.filepicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cdy.mobiledesign.R;
import com.cdy.mobiledesign.filepicker.util.FileUtils;


public class FilePickerActivity extends AppCompatActivity implements View.OnClickListener,OnUpdateDataListener {
    private Button btn_common,btn_all;
    private TextView tv_size,tv_confirm;
    private Fragment commonFileFragment,allFileFragment;
    private boolean isConfirm = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        initView();
        initEvent();
        setFragment(1);
    }

    private void initEvent() {
        btn_common.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    private void initView() {
        btn_common = (Button) findViewById(R.id.btn_common);
        btn_all = (Button) findViewById(R.id.btn_all);
        tv_size = (TextView) findViewById(R.id.tv_size);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
    }

    private void setFragment(int type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);
        switch (type){
            case 1:
                if(commonFileFragment==null){
                    commonFileFragment = FileCommonFragment.newInstance();
                    ((FileCommonFragment)commonFileFragment).setOnUpdateDataListener(this);
                    fragmentTransaction.add(R.id.fl_content,commonFileFragment);
                }else {
                    fragmentTransaction.show(commonFileFragment);
                }
                break;
            case 2:
                if(allFileFragment==null){
                    allFileFragment = FileAllFragment.newInstance();
                    ((FileAllFragment)allFileFragment).setOnUpdateDataListener(this);
                    fragmentTransaction.add(R.id.fl_content,allFileFragment);
                }else {
                    fragmentTransaction.show(allFileFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }
    private void hideFragment(FragmentTransaction transaction) {
        if (commonFileFragment != null) {
            transaction.hide(commonFileFragment);
        }
        if (allFileFragment != null) {
            transaction.hide(allFileFragment);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_common:
                setFragment(1);
                btn_common.setBackgroundResource(R.mipmap.no_read_pressed);
                btn_common.setTextColor(ContextCompat.getColor(this,R.color.white));
                btn_all.setBackgroundResource(R.mipmap.already_read);
                btn_all.setTextColor(ContextCompat.getColor(this,R.color.blue));
                break;
            case R.id.btn_all:
                setFragment(2);
                btn_common.setBackgroundResource(R.mipmap.no_read);
                btn_common.setTextColor(ContextCompat.getColor(this,R.color.blue));
                btn_all.setBackgroundResource(R.mipmap.already_read_pressed);
                btn_all.setTextColor(ContextCompat.getColor(this,R.color.white));
                break;
            case R.id.tv_confirm:
                isConfirm = true;
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
    private long currentSize;
    @Override
    public void update(long size) {
        currentSize+=size;
        tv_size.setText(getString(R.string.already_select, FileUtils.getReadableFileSize(currentSize)));
        String res = "("+PickerManager.getInstance().files.size()+"/"+PickerManager.getInstance().maxCount+")";
        tv_confirm.setText(getString(R.string.file_select_res,res));
    }

    @Override
    public void clear(){
        currentSize = 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isConfirm){
            PickerManager.getInstance().files.clear();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
