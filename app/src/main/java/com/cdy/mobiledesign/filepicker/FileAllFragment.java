package com.cdy.mobiledesign.filepicker;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdy.mobiledesign.R;
import com.cdy.mobiledesign.filepicker.adapter.AllFileAdapter;
import com.cdy.mobiledesign.filepicker.adapter.OnFileItemClickListener;
import com.cdy.mobiledesign.filepicker.model.FileEntity;
import com.cdy.mobiledesign.filepicker.util.FileUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：chs on 2017-08-24 11:05
 * 邮箱：657083984@qq.com
 * 全部文件
 */

public class FileAllFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView,tv_back;
    private String mPath;
    private String rootPath;
    private List<FileEntity> mListFiles;
    private FileSelectFilter mFilter;
    //筛选类型条件
    private String[] mFileTypes = new String[]{};
    private AllFileAdapter mAllFileAdapter;
    private OnUpdateDataListener mOnUpdateDataListener;

    public void setOnUpdateDataListener(OnUpdateDataListener onUpdateDataListener) {
        mOnUpdateDataListener = onUpdateDataListener;
    }
    public static FileAllFragment newInstance(){
        return new FileAllFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_all,null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rl_all_file);
        mRecyclerView.setLayoutManager(layoutManager);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        tv_back = (TextView) view.findViewById(R.id.tv_back);
    }

    private void initData() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        getData();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(getContext(),"读写sdk权限被拒绝",Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    private void getData(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), R.string.not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilter = new FileSelectFilter(mFileTypes);
        mListFiles = getFileList(mPath);
        mAllFileAdapter = new AllFileAdapter(getContext(),mListFiles,mFilter);
        mRecyclerView.setAdapter(mAllFileAdapter);
    }

    private void initEvent() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null || mPath.equals(rootPath)) {
                    Toast.makeText(getContext(),"最外层了",Toast.LENGTH_SHORT).show();
                    return;
                }
                mPath = tempPath;
                mListFiles = getFileList(mPath);
                mAllFileAdapter.updateListData(mListFiles);
                mAllFileAdapter.notifyDataSetChanged();
            }
        });
        mAllFileAdapter.setOnItemClickListener(new OnFileItemClickListener() {
            @Override
            public void click(int position) {
                FileEntity entity = mListFiles.get(position);
                ArrayList<FileEntity> files = PickerManager.getInstance().files;
                files.clear();
                //如果是文件夹点击进入文件夹
                if (entity.getFile().isDirectory()) {
                    getIntoChildFolder(position);
                }else {
                    File file = entity.getFile();
                    PickerManager.getInstance().files.clear();
                    System.out.println(" array size:" + PickerManager.getInstance().files.size());
                    if(files.contains(entity)){
                        files.remove(entity);
                        if(mOnUpdateDataListener!=null){
                            mOnUpdateDataListener.update(-file.length());
                        }
                        entity.setSelected(!entity.isSelected());
                        mAllFileAdapter.notifyDataSetChanged();
                    }else {
                        if(PickerManager.getInstance().files.size()<PickerManager.getInstance().maxCount){
                            files.add(entity);
                            if(mOnUpdateDataListener!=null){
                                mOnUpdateDataListener.update(file.length());
                            }
                            entity.setSelected(!entity.isSelected());
                            mAllFileAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getContext(),getString(R.string.file_select_max,PickerManager.getInstance().maxCount),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    //进入子文件夹
    private void getIntoChildFolder(int position) {
        mPath = mListFiles.get(position).getFile().getAbsolutePath();
        //更新数据源
        mListFiles = getFileList(mPath);
        mAllFileAdapter.updateListData(mListFiles);
        mAllFileAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序
     *
     * @param path
     * @return List<File>
     */
    private List<FileEntity> getFileList(String path) {
        List<FileEntity> fileListByDirPath = FileUtils.getFileListByDirPath(path, mFilter);
        if(fileListByDirPath.size()>0){
            mEmptyView.setVisibility(View.GONE);
        }else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        return fileListByDirPath;
    }
}
