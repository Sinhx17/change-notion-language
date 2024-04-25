package com;


import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    private static final String PATH = "\\AppData\\Roaming\\Notion\\notionAssetCache-v2";
    private static final String ASSETS_JSON = "assets.json";
    private static final String EN_US = "en-US";
    private static final String ZH_CN = "zh-CN";
    private static final String SERVER_PATHS = "serverPaths";
    private static final String LOCALE_HTML = "localeHtml";

    public static void main(String[] args) {
        String sysDir = System.getProperty("user.home");
        File directory = new File(sysDir, PATH);
        if (directory.exists() && directory.isDirectory()) {
            processDirectory(directory);
        }
    }

    /**
     * 遍历目录
     *
     * @param directory 目录
     */
    private static void processDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                // 是子目录，打开子目录下的assets.json文件
                if (file.isDirectory()) {
                    File assetsJsonFile = new File(file, ASSETS_JSON);
                    if (assetsJsonFile.exists() && assetsJsonFile.isFile()) {
                        updateAssetsFile(assetsJsonFile);
                    }
                }
            }
        }
    }

    /**
     * 更新assets.json文件
     *
     * @param assetsJsonFile assets.json文件
     */
    private static void updateAssetsFile(File assetsJsonFile) {
        try (FileInputStream fis = new FileInputStream(assetsJsonFile)) {
            byte[] bytes = new byte[(int) assetsJsonFile.length()];
            int read = fis.read(bytes);
            String content = new String(bytes, StandardCharsets.UTF_8);
            JSONObject fileJson = JSONObject.parseObject(content);
            JSONObject serverPaths = JSONObject.parseObject(fileJson.getString(SERVER_PATHS));
            JSONObject localeHtmlJson = JSONObject.parseObject(serverPaths.getString(LOCALE_HTML));
            localeHtmlJson.put(EN_US, localeHtmlJson.getString(ZH_CN));
            serverPaths.put(LOCALE_HTML, localeHtmlJson);
            fileJson.put(SERVER_PATHS, serverPaths);

            JSONObject localeHtml = JSONObject.parseObject(fileJson.getString(LOCALE_HTML));
            localeHtml.put(EN_US, localeHtml.getString(ZH_CN));
            fileJson.put(LOCALE_HTML, localeHtml);

            Files.write(assetsJsonFile.toPath(), fileJson.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}