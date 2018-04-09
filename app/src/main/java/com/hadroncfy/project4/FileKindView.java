package com.hadroncfy.project4;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Environment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import static com.hadroncfy.project4.FileListActivity.ACTION_ENCRYPT;

public class FileKindView extends LinearLayoutCompat implements View.OnClickListener{
    private AppCompatTextView text;
    private String extension;
    private Context ctx;

    public FileKindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        inflate(context, R.layout.file_kind_view, this);
        text = findViewById(R.id.fileKindName);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FileKindView);

        text.setText(a.getString(R.styleable.FileKindView_text));
        extension = a.getString(R.styleable.FileKindView_extension);

        a.recycle();
        setOnClickListener(this);
        setBackgroundResource(R.drawable.file_view_background);
    }

    @Override
    public void onClick(View view) {
        Intent it = new Intent();
        it.setClass(ctx, FileListActivity.class);
        it.putExtra(FileListActivity.KEY_EXTENSION, extension);
        it.putExtra(FileListActivity.KEY_PATH, Environment.getExternalStorageDirectory().getAbsolutePath());
        it.setAction(ACTION_ENCRYPT);
        ctx.startActivity(it);
    }
}
