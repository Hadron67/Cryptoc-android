package com.hadroncfy.project4;

/**
 * Created by cfy on 18-3-29.
 */

public class Cryptoc {
    static {
        System.loadLibrary("crypt-lib");
    }
    public static final String[] cryptMode = {"AES-128", "AES-192", "AES-256", "DES", "SM4"};

    public static native int encryptFile(String inFile, String outFile, String mode, String passwd);
    public static native int decryptFile(String inFile, String outFile, String passwd);
}
