package com.max.reinforce.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/12/23.
 */

public class Utils {

    public static final String GAME_APK_NAME = "reinforce.apk";
    public static final String GAME_APK_UNPACK_PATH = "reinforce";
    public static final String SHELL_APK_NAME = "shell.apk";
    public static final String SHELL_APK_UNPACK_PATH = "shell";
    public static final String SHELL_LAUCHER_ICON_PATH = "shell/res/mipmap-xhdpi-v4/icon";
    public static final String SHELL_APP_NAME = "ReinforceApk";
    public static final String SHELL_STRING_RES_PATH = "shell/res/values/strings.xml";
    public static final String GAME_APK_STRING_RES_PATH = "reinforce/res/values/strings.xml";
    public static final String SHELL_APKTOOL_YML_PATH = "shell/apktool.yml";
    public static final String GAME_APK_ENCRYPT_PATH = "shell/assets/Ldal.bin";
    public static final String OUTPUT_SIGNED_APK_NAME = "signed.apk";
    public static final String OUTPUT_DIR = "outputs/";
    public static final String REPACK_SHELL_APK_PATH = "shell/dist/shell.apk";

    /**
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(String str) {
        String os;
        StringBuilder sb = null;
        try {
            os = Base64.encode(str.getBytes("utf-8"));
            sb = new StringBuilder();
            for (int i = 0; i < os.length(); i++) {
                int c = os.charAt(i);
                sb.append((char) (c + 1));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int c = str.charAt(i);
            sb.append((char) (c - 1));
        }
        try {
            byte[] strBytes = Base64.decode(sb.toString());
            if(strBytes == null) {
                return null;
            }
            return new String(strBytes,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] encrypt(byte[] data) {
        for(int i = 0; i < data.length; i++) {
            data[i] = (byte) (0xa1 ^ data[i]);
        }
        return data;
    }

    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for(int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }

        return b;
    }

    public static void execmd(String cmd) throws Exception {
        System.out.println(cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        InputStreamRunnable outPrinter = new InputStreamRunnable(p.getInputStream(), System.out);
        InputStreamRunnable errPrinter = new InputStreamRunnable(p.getErrorStream(), System.err);

        outPrinter.start();
        errPrinter.start();

        int ret = p.waitFor();
        if(ret != 0) {
            throw new Exception("run cmd failed: " + cmd);
        }
    }
}
