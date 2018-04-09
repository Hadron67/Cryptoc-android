package com.hadroncfy.project4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;

public class FileListActivity extends AppCompatActivity implements OnFileListUpdateListener, Runnable, OnFileSelectedListener {
    public static final String ACTION_ENCRYPT = "encrypt";
    public static final String ACTION_DECRYPT = "decrypt";

    public static final String KEY_EXTENSION = "extension";
    public static final String KEY_PATH = "path";

    public static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 0;

    private static final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private RecyclerView fileList;
    private String action;
    private FileListAdapter adapter;
    private RecyclerView.LayoutManager fileListMgr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list_activity);
        fileList = findViewById(R.id.fileList);
        fileListMgr = new LinearLayoutManager(this);

        requestReadPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.destroy();
    }

    private void initFileList(){
        Intent it = getIntent();
        action = it.getAction();
        String ext = it.getStringExtra(KEY_EXTENSION);
        String path = it.getStringExtra(KEY_PATH);
        adapter = new FileListAdapter(this);
        fileList.setLayoutManager(fileListMgr);
        fileList.setAdapter(adapter);
        adapter.setOnFileListUpdateListener(this);
        adapter.setOnFileSelectedListener(this);
        if(ext.length() == 0){
            adapter.fromPath(path);
        }
        else {
            adapter.fromSearch(path, new FileExtensionFilter(ext));
        }
    }

    private void requestReadPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(permissions, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
        else {
            initFileList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initFileList();
                }
                break;
        }
    }


    @Override
    public void onFileListUpdated() {
        fileList.post(this);
    }

    @Override
    public void run() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFileSelected(File file) {
        Intent intent = new Intent(this, CryptActivity.class);
        intent.putExtra(CryptActivity.KEY_IS_INTERNAL, true);
        intent.putExtra(CryptActivity.KEY_FILE, file.getAbsolutePath());
        if(ACTION_ENCRYPT.equals(action)){
            intent.putExtra(CryptActivity.KEY_MODE, CryptActivity.MODE_ENCRYPT);
        }
        else if(ACTION_DECRYPT.equals(action)){
            intent.putExtra(CryptActivity.KEY_MODE, CryptActivity.MODE_DECRYPT);
        }
        else {
            throw new AssertionError("action = \"" + action + "\", which shouldn't happen!");
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.refresh();
    }
}
