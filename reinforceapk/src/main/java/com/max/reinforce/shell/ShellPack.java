package com.max.reinforce.shell;

import com.max.reinforce.bean.ManifestInfo;
import com.max.reinforce.util.Apktool;
import com.max.reinforce.util.FileUtils;
import com.max.reinforce.util.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.max.reinforce.util.Utils.encrypt;
import static java.awt.SystemColor.info;


/**
 * Created by Administrator on 2016/12/23.
 */

public class ShellPack {

    private String keystoreFile;
    private String keystorePass;
    private String keyAlias;

    public void unpackShellApk(String apkPath) throws Exception {
        Apktool.unpackApk(apkPath);
    }

    /**
     * replace package name
     * add application info such as android component, meta data and so on
     * @param gameInfo
     * @param dir
     */
    public void rewriteManifest(ManifestInfo gameInfo, String dir) throws Exception {
        String filePath = dir + File.separator + "AndroidManifest.xml";

        String shellManifest = FileUtils.readFile(filePath);
        if(shellManifest == null || shellManifest.isEmpty()) {
            throw  new Exception("read shell manifest failed, read from file is empty");
        }

        StringBuilder sb = new StringBuilder();
        // replace package name
        String regex = "(package=\".*?\")";
        String replace = "package=\"" + gameInfo.packageName + "\"";
        shellManifest = shellManifest.replaceAll(regex, replace);

        String[]split = shellManifest.split("<application.*>");
        int startIndex = split[0].length();
        int endIndex = shellManifest.indexOf(split[1]);
        String applicationStr = shellManifest.substring(startIndex, endIndex);
        String content = gameInfo.content.toString().replace("@style/lttransparent", "@android:style/Theme.Translucent.NoTitleBar.Fullscreen");
        if(gameInfo.applicationName != null && !gameInfo.applicationName.isEmpty()) {
            split[1] = split[1].replace("null", gameInfo.applicationName);
        }

        sb.append(split[0]);
        sb.append(gameInfo.features);
        sb.append(applicationStr);
        sb.append(content);
        sb.append(split[1]);

        FileUtils.writeFile(filePath, sb.toString());
    }

    public void copyLauncherIcon(String gameIconRes) throws Exception {
        String iconName = gameIconRes.substring(gameIconRes.indexOf("/") + 1) + ".";
        List<File> drawableDirs = FileUtils.getSubFiles(Utils.GAME_APK_UNPACK_PATH + "/res", "drawable");
        if(drawableDirs == null || drawableDirs.size() == 0) {
            throw new Exception("game apk drawable dir is null");
        }

        for(File file : drawableDirs) {
            List<File> iconFiles = FileUtils.getSubFiles(file.getAbsolutePath(), iconName);
            if(iconFiles != null && iconFiles.size() > 0) {
                for(File icFile : iconFiles) {
                    String name = icFile.getName();
                    String fileName = name.substring(0, name.indexOf(".") + 1);
                    if(fileName.equals(iconName)) {
                        String suffix = name.replace(iconName, ".");
                        String destName = Utils.SHELL_LAUCHER_ICON_PATH + suffix;
                        FileUtils.copyFile(icFile.getAbsolutePath(), destName);
                        return;
                    }
                }

            }
        }
    }

    public void changeAppName(String gameAppNameRes) throws Exception {
        String appResName = gameAppNameRes.substring(gameAppNameRes.indexOf("/") + 1);
        String  appName = FileUtils.readStringRes(Utils.GAME_APK_STRING_RES_PATH, appResName);
        System.out.println("game apk app name: " + appName);
        if(appName == null || appName.isEmpty()) {
            throw new Exception("read game apk app name failed, app name is empty");
        }

        String stringsResContent = FileUtils.readFile(Utils.SHELL_STRING_RES_PATH);
        if(stringsResContent == null || stringsResContent.isEmpty()) {
            throw new Exception("shell strings resource is empty");
        }

        stringsResContent = stringsResContent.replace(Utils.SHELL_APP_NAME, appName);

        System.out.println("changeAppName new strings content: " + stringsResContent);
        FileUtils.writeFile(Utils.SHELL_STRING_RES_PATH, stringsResContent);
    }

    public void changeVersion(String gameVerCode, String gameVerName) throws Exception {
        String apktoolContent = FileUtils.readFile(Utils.SHELL_APKTOOL_YML_PATH);
        if(apktoolContent == null || apktoolContent.isEmpty()) {
            throw new Exception("read apktool.yml failed");
        }

        String versionCodeStr = "versionCode: '";
        int codeStartIndex = apktoolContent.indexOf(versionCodeStr);
        int codeEndIndex = apktoolContent.lastIndexOf("'");
        String versionCode = apktoolContent.substring(codeStartIndex, codeEndIndex);
        System.out.println(versionCode);

        String versionNameStr = "versionName: ";
        int nameStartIndex = apktoolContent.indexOf(versionNameStr);
        String name = apktoolContent.substring(nameStartIndex);
        apktoolContent = apktoolContent.replace(versionCode, versionCodeStr + gameVerCode);
        apktoolContent = apktoolContent.replace(name, versionNameStr + gameVerName);

        FileUtils.writeFile(Utils.SHELL_APKTOOL_YML_PATH, apktoolContent);
    }

    public void repackShell() throws Exception {
        Apktool.repackApk(Utils.SHELL_APK_UNPACK_PATH);
    }

    private void buildSignKey(String packageName) throws Exception {
        String signFileName = "sign." + packageName + ".keystore";
        keystoreFile = Utils.OUTPUT_DIR + packageName + File.separator + signFileName;
        File file = new File(keystoreFile);
        File parent = file.getParentFile();
        if(!parent.exists()) {
            parent.mkdirs();
        }

        keystorePass = encrypt(packageName + System.currentTimeMillis());
        String[] splits = packageName.split("\\.");
        int index = (int) (Math.random() * splits.length);
        index = index >= splits.length ? index - 1 : index;
        keyAlias = splits[index];

//        if(file.exists()) {
//            buildSignProperty(packageName);
//            return;
//        }

        System.out.println("keyAlias: " + keyAlias + ", password: " + keystorePass);

        int keyAliasLen = keyAlias.length();
        int storepassLen = keystorePass.length();
        String cn = keyAlias.substring(0, (int) (Math.random() * keyAliasLen));
        cn = cn.length() == 0 ? keyAlias : cn;
        index++;
        String ou = splits[index < splits.length ? index : 0];
        String o = packageName.substring(packageName.lastIndexOf(".") + 1);
        int start = (int) (Math.random() * (storepassLen - 5));
        int end = (int) (Math.random() * storepassLen);
        String l;
        if(start < end) {
            l = keystorePass.substring(start, end);
        } else if(start > end){
            l = keystorePass.substring(end, start);
        } else {
            start = start == 0 ? 5 : start;
            l = keystorePass.substring(0, start);
        }
        String st = cn + ou;
        String c = o + l;

        String info = String.format("CN=%s,OU=%s,O=%s,L=%s,ST=%s,C=%s", cn, ou, o, l, st, c);
        String cmd = String.format("keytool -genkey -alias %s -keyalg RSA -validity 20000 -keystore %s -storepass %s -keypass %s -dname %s", new Object[] { keyAlias, keystoreFile, keystorePass, keystorePass, info });
        Utils.execmd(cmd);

//        buildSignProperty(packageName);
    }

    private void buildSignProperty(String packageName) throws IOException {
        String propertyPath = Utils.OUTPUT_DIR + packageName + File.separator + "signkey.property";
        File file = new File(propertyPath);
        if(file.exists()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("key.store.pass=").append(keystorePass).append("\n");
        sb.append("key.alias=").append(keyAlias).append("\n");
        sb.append("key.alias.pass=").append(keystorePass);
        FileUtils.writeFile(propertyPath, sb.toString());
    }

    public void signShellApk(String packageName) throws Exception {
        File outputsDir = new File(Utils.OUTPUT_DIR + packageName);
        FileUtils.deleteFile(outputsDir);
        outputsDir.mkdirs();

        buildSignKey(packageName);

        File file = new File(outputsDir, Utils.OUTPUT_SIGNED_APK_NAME);
//        if(file.exists()) {
//            file.delete();
//        }
        StringBuilder sb = new StringBuilder();
        sb.append("jarsigner -verbose -keystore ");
        sb.append(keystoreFile).append(" ");
        sb.append("-storepass ").append(keystorePass).append(" ");
        sb.append("-keypass ").append(keystorePass).append(" ");
        sb.append("-sigfile CERT -digestalg SHA1 -sigalg MD5withRSA ");
        sb.append("-signedjar ").append(file.getAbsolutePath()).append(" ");
        sb.append("shell/dist/shell.apk ");
        sb.append(keyAlias);
        String cmd = sb.toString();
        Utils.execmd(cmd);

        FileUtils.deleteFile(new File(keystoreFile));
    }

    public void copyGameApkToDex() throws Exception {
        File apkFile = new File(Utils.REPACK_SHELL_APK_PATH);
        byte[] apkFileBytes = Utils.encrypt(FileUtils.readFileBytes(apkFile));
        System.out.println("read reinforce apk file byte, length: " + apkFileBytes.length);
        byte[] dexFileBytes = readDexFile();
        System.out.println("read shell apk file byte, length: " + dexFileBytes.length);
        int apkFileLen = apkFileBytes.length;
        int dexFileLen = dexFileBytes.length;
        int totalLength = apkFileLen + dexFileLen + 4;
        System.out.println("new dex file length: " + totalLength);
        byte[] newDexBytes = new byte[totalLength];
        // copy dex file
        System.out.println("copy shell dex file bytes to new byte array");
        System.arraycopy(dexFileBytes, 0, newDexBytes, 0, dexFileLen);

        // copy apk file
        System.out.println("copy reinforce apk file bytes to new byte array");
        System.arraycopy(apkFileBytes, 0, newDexBytes, dexFileLen, apkFileLen);

        // set apk file len
        System.out.println("write apk file length to byte array");
        System.arraycopy(Utils.intToByte(apkFileLen), 0, newDexBytes, totalLength - 4, 4);

        System.out.println("fix new dex file size head");
        fixFileSizeHead(newDexBytes);

        System.out.println("fix new dex file sha1 head");
        fixSHA1Head(newDexBytes);

        System.out.println("fix new dex file check sum head");
        fixCheckSumHead(newDexBytes);

        System.out.println("write new dex file to shell apk");
        writeDexBytesToFile(newDexBytes);
    }

    private byte[] readDexFile() throws IOException {
        ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(Utils.SHELL_APK_NAME)));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            if("classes.dex".equals(zipEntry.getName())) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    dexByteArrayOutputStream.write(buffer, 0, len);
                }

                zipInputStream.closeEntry();
                break;
            }

            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
        byte[] dexBuffer = dexByteArrayOutputStream.toByteArray();
        dexByteArrayOutputStream.close();
        return dexBuffer;
    }

    private void fixFileSizeHead(byte[] dexBytes) {
        byte[] fileLenBytes = Utils.intToByte(dexBytes.length);
        byte[] trans = new byte[4];
        for(int i = 0; i < 4; i++) {
            trans[i] = fileLenBytes[3 - i];
        }

        System.arraycopy(trans, 0, dexBytes, 32, 4);  //size head position is 32
    }

    private void fixSHA1Head(byte[] dexBytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(dexBytes, 32, dexBytes.length - 32); //from 32 position to end
        byte[] newDgt = md.digest();
        System.arraycopy(newDgt, 0, dexBytes, 12, 20); // sha1 head position is 12
    }

    private void fixCheckSumHead(byte[] dexBytes) {
        Adler32 adler = new Adler32();
        adler.update(dexBytes, 12, dexBytes.length - 12); // check sum from position 12
        long value = adler.getValue();
        int va = (int) value;
        byte[] vaBytes = Utils.intToByte(va);
        byte[] trans = new byte[4];
        for(int i = 0; i < 4; i++) {
            trans[i] = vaBytes[3 - i];
        }

        System.arraycopy(trans, 0, dexBytes, 8, 4); // check sum position is 8
    }

    private void writeDexBytesToFile(byte[] dexBytes) throws Exception {
        String tempApk = "shell/dist/temp.apk";
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(Utils.REPACK_SHELL_APK_PATH)));
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tempApk));
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            if("classes.dex".equals(zipEntry.getName())) {
                zipEntry = zipInputStream.getNextEntry();
                zipInputStream.closeEntry();
                continue;
            }

            ZipEntry entry = new ZipEntry(zipEntry.getName());
            zipOutputStream.putNextEntry(entry);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = zipInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }

            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        ZipEntry entry = new ZipEntry("classes.dex");
        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(dexBytes, 0, dexBytes.length);
        zipOutputStream.closeEntry();
        zipOutputStream.close();
        zipInputStream.close();

        FileUtils.copyFile(tempApk, Utils.REPACK_SHELL_APK_PATH);
    }

}
