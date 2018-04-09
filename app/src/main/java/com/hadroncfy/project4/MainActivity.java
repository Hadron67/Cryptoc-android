package com.hadroncfy.project4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import static com.hadroncfy.project4.FileListActivity.ACTION_DECRYPT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnEncrypt, btnDecrypt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEncrypt = findViewById(R.id.btn_encrypt);
        btnDecrypt = findViewById(R.id.btn_decrypt);
        btnEncrypt.setOnClickListener(this);
        btnDecrypt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent();
        switch(view.getId()){
            case R.id.btn_encrypt: i.setClass(this, FileSelectActivity.class); break;
            case R.id.btn_decrypt:
                i.setClass(this, FileListActivity.class);
                i.setAction(ACTION_DECRYPT);
                i.putExtra(FileListActivity.KEY_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
                i.putExtra(FileListActivity.KEY_EXTENSION, MetaData.getEncryptedExtensionName(this));
                break;
            default: throw new AssertionError("unreachable");
        }
        startActivity(i);
    }

}
