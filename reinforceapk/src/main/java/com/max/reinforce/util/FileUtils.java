package com.max.reinforce.util;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public class FileUtils {

    public static String readFile(String filePath) throws Exception {
        File file = new File(filePath);
        if(!file.exists()) {
            throw new Exception("shell apk AndroidManifest.xml is not exists!");
        }

        BufferedReader reader = null;
        StringBuilder builder = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            int readLen;
            char[] buffer = new char[1024];
            builder = new StringBuilder();
            while ((readLen = reader.read(buffer)) > 0) {
                builder.append(new String(buffer, 0, readLen));
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }

        return builder == null ? null : builder.toString();
    }

    public static void writeFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        writer.write(content);
        writer.close();

    }

    public static List<File> getSubFiles(String parent, String subName) throws Exception {
        File parentFile = new File(parent);
        if(!parentFile.exists()) {
            throw new Exception("getSubFile failed, parent file " + parent + " is not exists");
        }

        ArrayList<File> files = new ArrayList<>();
        File[] subFiles = parentFile.listFiles();
        for(File file : subFiles) {
            if(file.getName().contains(subName)) {
                files.add(file);
            }
        }

        return files;
    }

    public static void copyFile(String src, String dest) throws Exception {
        File srcFile = new File(src);
        if(!srcFile.exists()) {
            throw new Exception("copy file failed, source file " + src + " is not exists");
        }

        File destFile = new File(dest);
        if(destFile.exists()) {
            destFile.delete();
        }

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {

            inputStream = new FileInputStream(srcFile);
            outputStream= new FileOutputStream(destFile);

            int readBytes = 0;
            byte[] buffer = new byte[1024];
            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }

            if(outputStream != null) {
                outputStream.close();
            }
        }

    }

    public static String readStringRes(String path, String resName) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(path);
        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element node = iterator.next();
            if(node.getName().equals("string")) {
                Attribute attribute = node.attribute("name");
                if(resName.equals(attribute.getValue())) {
                    return node.getTextTrim();
                }
            }
        }

        return null;
    }

    public static void writeBytes2File(String fileName, byte[] bytes) throws IOException {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if(!parent.exists()) {
            parent.mkdirs();
        }

        if(file.exists()) {
            file.delete();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }



    public static byte[] readFileBytes(File file) throws IOException {
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

    public static void deleteFile(File file) {
        if(file.exists()) {
            if(file.isFile()) {
                file.delete();
            } else if(file.isDirectory()){
                File[] subFiles = file.listFiles();
                for(File sub : subFiles) {
                    deleteFile(sub);
                }

                file.delete();
            }
        }
    }
}
