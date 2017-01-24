package com.max.reinforce.util;

/**
 * Created by Administrator on 2016/12/26.
 */

public class Apktool {

    public static final String APKTOOL_JAR_PATH = "apktool/apktool_2.1.1.jar";

    public static void unpackApk(String apkName) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("java -jar ").append(APKTOOL_JAR_PATH);
        sb.append(" -f d ").append(apkName);
        Utils.execmd(sb.toString());
    }

    public static void repackApk(String apkPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("java -jar ").append(APKTOOL_JAR_PATH);
        sb.append(" -f b ").append(apkPath);
        Utils.execmd(sb.toString());
    }
}
