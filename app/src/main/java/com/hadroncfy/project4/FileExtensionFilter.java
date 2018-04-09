package com.hadroncfy.project4;

import java.io.File;
import java.io.FileFilter;

public class FileExtensionFilter implements FileFilter {
    private String[] exts;
    public FileExtensionFilter(String ext){
        exts = ext.trim().split("[ ]*\\|[ ]*");
    }
    @Override
    public boolean accept(File pathname) {
        for(String ext: exts){
            if(pathname.getName().endsWith(ext)){
                return true;
            }
        }
        return false;
    }
}
