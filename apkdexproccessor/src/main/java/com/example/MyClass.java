package com.example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

public class MyClass {

    private static final String ENCRYPT_APK_DEST_PATH = "loader/assets/Ldal.bin";
    private static final String FINAL_APK_PATH = "dest/";

    public static void main(String[] args) {
        try {
            encryptApkFile("apkdexproccessor/Encrypt.apk", "app/src/main/assets/Ldal.bin");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void buildLoaderApk() {
        File[] srcFiles = listSrcFiles();

        for(File file : srcFiles) {
            try {
                String destApkFile = FINAL_APK_PATH + file.getName();
                encryptApkFile(file.getAbsolutePath(), ENCRYPT_APK_DEST_PATH);
                buildApk(destApkFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void buildApk(String destFile) throws IOException {
        Runtime.getRuntime().exec("apktool.jar b loader o " + destFile);
    }

    private static File[] listSrcFiles() {
        File file = new File("src");
        return file.listFiles();
    }

    private static void encryptApkFile(String srcFilePath, String destPath) throws IOException {
        File apkFile = new File(srcFilePath);
        byte[] apkFileBytes = encrypt(readFileBytes(apkFile));

        writeBytes2File(destPath, apkFileBytes);
    }

    private static void copyFile2Dex() {
        File apkFile = new File("apkdexproccessor/PopStar_baibao.apk");
        File dexFile = new File("apkdexproccessor/oldclasses.dex");
        try {
            byte[] apkFileBytes = encrypt(readFileBytes(apkFile));//readFileBytes(apkFile); //
            byte[] dexFileBytes = readFileBytes(dexFile);
            int apkFileLen = apkFileBytes.length;
            int dexFileLen = dexFileBytes.length;
            int totalLength = apkFileLen + dexFileLen + 4;
            System.out.println("old dex file length: " + dexFileLen);
            byte[] newDexBytes = new byte[totalLength];
            // copy dex file
            System.arraycopy(dexFileBytes, 0, newDexBytes, 0, dexFileLen);
            // copy apk file
            System.arraycopy(apkFileBytes, 0, newDexBytes, dexFileLen, apkFileLen);
            // set apk file len
            System.arraycopy(intToByte(apkFileLen), 0, newDexBytes, totalLength - 4, 4);

            fixFileSizeHead(newDexBytes);

            fixSHA1Head(newDexBytes);

            fixCheckSumHead(newDexBytes);

            writeDexBytesToFile(newDexBytes);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFileBytes(File file) throws IOException {
        byte[] buffers = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream is = new FileInputStream(file);
        int i = 0;
        while ((i = is.read(buffers)) > 0) {
            baos.write(buffers, 0, i);
        }

        byte[] fileBytes = baos.toByteArray();
        baos.close();
        is.close();

        return fileBytes;
    }

    private static byte[] encrypt(byte[] data) {
        for(int i = 0; i < data.length; i++) {
            data[i] = (byte) (0xa1 ^ data[i]);
        }
        return data;
    }

    private static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for(int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }

        return b;
    }

    private static void fixFileSizeHead(byte[] dexBytes) {
        byte[] fileLenBytes = intToByte(dexBytes.length);
        byte[] trans = new byte[4];
        for(int i = 0; i < 4; i++) {
            trans[i] = fileLenBytes[3 - i];
        }

        System.arraycopy(trans, 0, dexBytes, 32, 4);  //size head position is 32
    }

    private static void fixSHA1Head(byte[] dexBytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(dexBytes, 32, dexBytes.length - 32); //from 32 position to end
        byte[] newDgt = md.digest();
        System.arraycopy(newDgt, 0, dexBytes, 12, 20); // sha1 head position is 12
    }

    private static void fixCheckSumHead(byte[] dexBytes) {
        Adler32 adler = new Adler32();
        adler.update(dexBytes, 12, dexBytes.length - 12); // check sum from position 12
        long value = adler.getValue();
        int va = (int) value;
        byte[] vaBytes = intToByte(va);
        byte[] trans = new byte[4];
        for(int i = 0; i < 4; i++) {
            trans[i] = vaBytes[3 - i];
        }

        System.arraycopy(trans, 0, dexBytes, 8, 4); // check sum position is 8
    }

    private static void writeDexBytesToFile(byte[] dexBytes) throws IOException {
        writeBytes2File("apkdexproccessor/classes.dex", dexBytes);
    }

    private static void writeBytes2File(String fileName, byte[] bytes) throws IOException {
        File file = new File(fileName);
        if(file.exists()) {
            file.delete();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

}
