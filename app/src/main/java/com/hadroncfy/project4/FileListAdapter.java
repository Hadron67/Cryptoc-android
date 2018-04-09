package com.hadroncfy.project4;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> implements Comparator<FileListAdapter.ListItem>, Runnable, Handler.Callback {
    private List<ListItem> files;

    private Queue<File> fq;
    private FileFilter fileFiler;
    private boolean search = false;
    private Handler h = new Handler(this);
    private OnFileListUpdateListener onFileListUpdateListener;
    private OnFileSelectedListener onFileSelectedListener;

    private File selectedFile = null;
    private int level;
    private String enc;
    private Runnable initializer;
    private boolean needRefresh;

    private static final int MSG_SEARCH = 1;

    public FileListAdapter(Context ctx){
        files = null;
        enc = MetaData.getEncryptedExtensionName(ctx);
    }
    public void setOnFileListUpdateListener(OnFileListUpdateListener l){
        onFileListUpdateListener = l;
    }
    public void setOnFileSelectedListener(OnFileSelectedListener l){
        onFileSelectedListener = l;
    }
    public File getSelectedFile(){
        return selectedFile;
    }

    public void fromPath(String path){
        level = 0;
        fromPath0(new File(path));
    }


    private void fromPath0(@NonNull File pf){
//        files = new ArrayList<>();
//        for(File f: pf.listFiles()){
//            files.add(new ListItem(f, false));
//        }
//        if(level > 0){
//            files.add(new ListItem(pf.getParentFile(), true));
//        }
//        Collections.sort(files, this);
//        notifyDataSetChanged();
        initializer = new PathInitializer(pf);
        initializer.run();
    }

    public void fromSearch(String basePath, FileFilter filter){
//        fq = new ArrayDeque<>();
//        fq.offer(new File(basePath));
//        files = new ArrayList<>();
//        fileFiler = filter;
//        search = true;
//        new Thread(this).start();
        initializer = new SearchInitializer(basePath, filter);
        initializer.run();
    }

    public void refresh(){
        initializer.run();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View l = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, parent, false);
        ViewHolder holder = new ViewHolder(l);
        holder.image = l.findViewById(R.id.fileIcon);
        holder.title = l.findViewById(R.id.fileName);
        holder.subtitle = l.findViewById(R.id.filePath);
        l.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItem it = files.get(position);
        holder.it = it;
        if(it.isParent){
            holder.image.setImageResource(R.drawable.ic_left_arrow);
            holder.title.setText("..");
            holder.subtitle.setText(it.f.getPath());
        }
        else {
            File f = it.f;
            if(f.isDirectory()){
                holder.image.setImageResource(R.drawable.ic_folder);
            }
            else if(f.isFile()){
                String name = f.getName();
                if(name.endsWith('.' + enc)){
                    holder.image.setImageResource(R.drawable.ic_lock);
                }
                else {
                    holder.image.setImageResource(R.drawable.ic_file);
                }
            }
            holder.title.setText(f.getName());
            holder.subtitle.setText(f.getPath());
        }
    }

    public void destroy(){
        search = false;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public int compare(ListItem l1, ListItem l2) {
        File f1 = l1.f;
        File f2 = l2.f;
        if(l1.isParent){
            return -1;
        }
        if(l2.isParent){
            return 1;
        }
        if(f1.isDirectory() && !f2.isDirectory()){
            return -1;
        }
        else if(!f1.isDirectory() && f2.isDirectory()){
            return 1;
        }
        else {
            return f1.getName().compareTo(f2.getName());
        }
    }
    private synchronized boolean isNeedRefresh(){
        return needRefresh;
    }
    private synchronized void setNeedRefresh(boolean s){
        needRefresh = s;
    }
    @Override
    public void run() {
        search = true;
        int fileCnt = 0;
        do {
            setNeedRefresh(false);
            while(search && !fq.isEmpty()){
                File p = fq.poll();
                for(File f: p.listFiles()){
                    if(f.isDirectory()){
                        fq.offer(f);
                    }
                    else if(fileFiler.accept(f)){
                        files.add(new ListItem(f, false));
                        fileCnt++;
                    }
                }
                if(fileCnt > 10){
                    h.sendEmptyMessage(MSG_SEARCH);
                    fileCnt = 0;
                }
            }
            h.sendEmptyMessage(MSG_SEARCH);
        } while(isNeedRefresh());
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what){
            case MSG_SEARCH:
                onFileListUpdateListener.onFileListUpdated();
                break;
        }
        return false;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatImageView image;
        AppCompatTextView title, subtitle;
        ListItem it;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            if(it.isParent){
                level--;
                fromPath0(it.f);
            }
            else {
                File f = it.f;
                if(f.isDirectory()){
                    level++;
                    fromPath0(f);
                }
                else {
                    selectedFile = f;
                    onFileSelectedListener.onFileSelected(f);
                }
            }
        }
    }

    static class ListItem {
        File f;
        boolean isParent;
        ListItem(File f, boolean isParent){
            this.f = f;
            this.isParent = isParent;
        }
    }

    class PathInitializer implements Runnable {
        File pf;
        PathInitializer(File pf){
            this.pf = pf;
        }
        @Override
        public void run() {
            files = new ArrayList<>();
            for(File f: pf.listFiles()){
                files.add(new ListItem(f, false));
            }
            if(level > 0){
                files.add(new ListItem(pf.getParentFile(), true));
            }
            Collections.sort(files, FileListAdapter.this);
            onFileListUpdateListener.onFileListUpdated();
        }
    }
    class SearchInitializer implements Runnable {
        String basePath;
        FileFilter filter;
        SearchInitializer(String basePath, FileFilter filter){
            this.basePath = basePath;
            this.filter = filter;
        }
        @Override
        public void run() {
            if(search){
                setNeedRefresh(true);
            }
            else {
                fq = new ArrayDeque<>();
                fq.offer(new File(basePath));
                files = new ArrayList<>();
                fileFiler = filter;
                new Thread(FileListAdapter.this).start();
            }
        }
    }
}
