package com.max.reinforce;

import com.max.reinforce.bean.ManifestInfo;
import com.max.reinforce.shell.ShellPack;
import com.max.reinforce.util.Apktool;
import com.max.reinforce.util.FileUtils;
import com.max.reinforce.util.Utils;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String... args) throws Exception {

        // unpack game apk
        System.out.println("------unpack game apk-----");
        Apktool.unpackApk(Utils.GAME_APK_NAME);

        // read game apk manifest info
        System.out.println("------read game apk manifest info-----");
        ManifestInfo gameInfo = ManifestInfo.loadManifest(new File(Utils.GAME_APK_UNPACK_PATH));
//        System.out.println(gameInfo.toString());

        // unpack shell apk
        System.out.println("------unpack shell apk-----");
        ShellPack shellPack = new ShellPack();
        shellPack.unpackShellApk(Utils.SHELL_APK_NAME);

        // change shell manifest info
        System.out.println("------change shell manifest info-----");
        shellPack.rewriteManifest(gameInfo, Utils.SHELL_APK_UNPACK_PATH);

        // change shell launcher icon
        System.out.println("------change shell launcher icon-----");
        shellPack.copyLauncherIcon(gameInfo.iconResName);

        // change shell app name
        System.out.println("------change shell app name-----");
        shellPack.changeAppName(gameInfo.appResName);

        // change shell app version name and version code
        System.out.println("------change shell app version name and version code-----");
        shellPack.changeVersion(gameInfo.versionCode, gameInfo.versionName);

        // encrypt game apk and copy to shell apk assets directory
//        encryptGameApk();

        // read channel list

        // change channel id in shell apk

        // repack shell apk
        System.out.println("------repack shell apk-----");
        shellPack.repackShell();

        // encrypt game apk and write encrypted apk to shell dex file
        System.out.println("------encrypt game apk and write encrypted apk to shell dex file-----");
        shellPack.copyGameApkToDex();

        // sign shell apk
        System.out.println("------sign shell apk-----");
        shellPack.signShellApk(gameInfo.packageName);
    }

    private static void encryptGameApk() throws IOException {
        File apkFile = new File(Utils.GAME_APK_NAME);
        byte[] apkFileBytes = encrypt(FileUtils.readFileBytes(apkFile));

        FileUtils.writeBytes2File(Utils.GAME_APK_ENCRYPT_PATH, apkFileBytes);
    }

    private static byte[] encrypt(byte[] data) {
        for(int i = 0; i < data.length; i++) {
            data[i] = (byte) (0xa1 ^ data[i]);
        }
        return data;
    }



}
