package com.hadroncfy.project4;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;

public class CryptActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String KEY_IS_INTERNAL = "IsInternal";
    public static final String KEY_FILE = "file";
    public static final String KEY_MODE = "mode";

    private String fileExtension;

    public static final String MODE_ENCRYPT = "encrypt";
    public static final String MODE_DECRYPT = "decrypt";

    private static final String[] permissions = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQUEST_CODE = 30;

    private String fileName;
    private String mode;
    private String cryptMode;

    private AppCompatCheckBox deleteOriginalFile;

    private AppCompatEditText pass;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();

        fileExtension = MetaData.getEncryptedExtensionName(this);
        requestPermissions();
        setContentView(R.layout.crypt_activity);
        Intent intent = getIntent();
        if(intent.getBooleanExtra(KEY_IS_INTERNAL, false)){
            fileName = intent.getStringExtra(KEY_FILE);
            mode = intent.getStringExtra(KEY_MODE);
            run();
        }
        else if(Intent.ACTION_SEND.equals(intent.getAction())){
            Bundle extra = intent.getExtras();
            if(extra.containsKey(Intent.EXTRA_STREAM)){
                Uri uri = extra.getParcelable(Intent.EXTRA_STREAM);
                File f = new File(uri.getPath());
                if(!f.exists()){
                    Toast.makeText(this, res.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                    finish();
                }
                fileName = f.getAbsolutePath();
                if(fileName.endsWith('.' + fileExtension)){
                    mode = MODE_DECRYPT;
                }
                else {
                    mode = MODE_ENCRYPT;
                }
            }
            run();
        }
    }

    private void run(){
        Resources res = getResources();
        AppCompatSpinner sp = findViewById(R.id.cryptMode);
        AppCompatButton btn = findViewById(R.id.btnCrypt);
        deleteOriginalFile = findViewById(R.id.deleteOriginalFile);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Cryptoc.cryptMode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
        btn.setOnClickListener(this);
        pass = findViewById(R.id.secretKey);
        if(mode.equals(MODE_DECRYPT)){
            btn.setText(res.getString(R.string.decrypt));
            sp.setEnabled(false);
        }
        else if(mode.equals(MODE_ENCRYPT)) {
            btn.setText(res.getString(R.string.encrypt));
        }
    }

    private void requestPermissions(){
        for(String perm: permissions){
            if(ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_DENIED){
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            for(int result: grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    break;
                }
            }
        }
        run();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cryptMode = adapter.getItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        cryptMode = null;
    }

    private String toEncryptedFileName(String fname){
        return fname + '.' + fileExtension;
    }

    private String toDecryptedFileName(String fname){
        if(fname.endsWith('.' + fileExtension)){
            return fname.substring(0, fname.length() - fileExtension.length() - 1);
        }
        else {
            return fname;
        }
    }

    @Override
    public void onClick(View v) {
        String passwd = pass.getText().toString();
        File f = new File(fileName);
        if(mode.equals(MODE_DECRYPT)){
            Cryptoc.decryptFile(fileName, toDecryptedFileName(fileName), passwd);
        }
        else if(mode.equals(MODE_ENCRYPT)) {
            Cryptoc.encryptFile(fileName, toEncryptedFileName(fileName), cryptMode, passwd);
        }
        if(deleteOriginalFile.isChecked()){
            f.delete();
        }
        finish();
    }
}
