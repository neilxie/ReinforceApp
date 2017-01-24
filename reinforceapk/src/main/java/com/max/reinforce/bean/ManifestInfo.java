package com.max.reinforce.bean;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Administrator on 2016/12/22.
 */

public class ManifestInfo {

    public String versionCode;
    public String versionName;
    public String applicationName;
    public String packageName;
    public String iconResName;
    public String appResName;
    public StringBuilder features;
    public StringBuilder content;



    public static ManifestInfo loadManifest(File file) throws Exception {
        ManifestInfo info = new ManifestInfo();

        info.parseVersion(new File(file, "apktool.yml"));

        File manifestFile = new File(file, "AndroidManifest.xml");
        if(!manifestFile.exists()) {
            throw new IOException("AndroidManifest.xml is not exists");
        }

        info.parse(manifestFile);

        return info;
    }

    private void parseVersion(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        MetaInfo metaInfo = yaml.loadAs(new FileInputStream(file), MetaInfo.class);
        versionCode = metaInfo.versionInfo.versionCode;
        versionName = metaInfo.versionInfo.versionName;

        System.out.println("versionCode: " + versionCode + ", versionName: " + versionName);
    }

    private void parse(File file) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element root = document.getRootElement();
        listNodes(root);
    }

    private void listNodes(Element node) throws Exception {
        String nodeName = node.getName();
        if("manifest".equals(nodeName)) {
            packageName = readAttributeValue(node, "package");
            if(packageName == null) {
                throw new Exception("parse AndroidManifest.xml failed. package name is null");
            }
        } else if("application".equals(nodeName)) {
            iconResName = readAttributeValue(node, "icon");
            appResName = readAttributeValue(node, "label");
            applicationName = readAttributeValue(node, "name");
        } else if("uses-feature".equals(nodeName)
                || "supports-screens".equals(nodeName)
                || "uses-permission".equals(nodeName)){
            if(features == null) {
                features = new StringBuilder();
            }

            String xml = node.asXML();
            xml = xml.replace("xmlns:android=\"http://schemas.android.com/apk/res/android\" ", "");
            features.append(xml).append("\n");
        } else if("activity".equals(nodeName)
                || "service".equals(nodeName)
                || "receiver".equals(nodeName)
                || "provider".equals(nodeName)
                || "meta-data".equals(nodeName)) {
            if(content == null) {
                content = new StringBuilder();
            }

            String xml = node.asXML();
            xml = xml.replace("xmlns:android=\"http://schemas.android.com/apk/res/android\" ", "");
            content.append(xml).append("\n");
        }

        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            listNodes(iterator.next());
        }
    }


    private String readAttributeValue(Element node, String name) {
        Attribute attribute = node.attribute(name);
        if(attribute != null) {
            return attribute.getValue();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("versionCode: ").append(versionCode).append("\n");
        sb.append("versionName: ").append(versionName).append("\n");
        sb.append("applicationName: ").append(applicationName).append("\n");
        sb.append("packageName: ").append(packageName).append("\n");
        sb.append("iconResName: ").append(iconResName).append("\n");
        sb.append("appResName: ").append(appResName).append("\n");
//        sb.append("features: ").append(features).append("\n");
//        sb.append("content: ").append(content);
        return sb.toString();
    }

}
