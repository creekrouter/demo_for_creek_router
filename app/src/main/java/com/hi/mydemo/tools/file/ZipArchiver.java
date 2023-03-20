package com.hi.mydemo.tools.file;


import com.hi.mydemo.tools.download.StatusListener;
import com.hi.mydemo.tools.load.PluginApk;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipArchiver {

    public static boolean doUnArchiver(PluginApk apk, StatusListener listener) {

        FileTool.DeleteFolder(apk.unArchiverPath());

        File src = new File(apk.pluginPath());
        if (!src.exists())
            return false;
        try {
            ZipFile zFile = new ZipFile(apk.pluginPath());
            zFile.setRunInThread(true);
            Field field = findField(zFile, "progressMonitor");
            if (field != null) {
                MyProgressMonitor progressMonitor = new MyProgressMonitor();
                progressMonitor.setListener(listener);
                field.set(zFile, progressMonitor);
            }
            zFile.setCharset(getEncoding(apk.pluginPath()));

            if (!zFile.isValidZipFile()) {
                return false;
            }

            listener.onStart(null);
            File destDir = new File(apk.unArchiverPath());
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            zFile.extractAll(apk.unArchiverPath());
        } catch (Exception error) {
            listener.onFail(null, error.getMessage());
            return false;
        }
        return true;

    }

    private static Field findField(Object instance, String name) {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }
        return null;
    }

    /**
     * 判断该使用哪种编码方式解压
     *
     * @param path
     * @return
     * @throws Exception
     */
    private static Charset getEncoding(String path) {
        Charset encoding = Charset.forName("GBK");
        try {
            ZipFile zipFile = new ZipFile(path);
            zipFile.setCharset(encoding);
            List<FileHeader> list = zipFile.getFileHeaders();
            for (int i = 0; i < list.size(); i++) {
                FileHeader fileHeader = list.get(i);
                String fileName = fileHeader.getFileName();
                if (isMessyCode(fileName)) {
                    encoding = StandardCharsets.UTF_8;
                    break;
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return encoding;
    }

    private static boolean isMessyCode(String strName) {
        try {
            Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
            Matcher m = p.matcher(strName);
            String after = m.replaceAll("");
            String temp = after.replaceAll("\\p{P}", "");
            char[] ch = temp.trim().toCharArray();

            int length = (ch != null) ? ch.length : 0;
            for (int i = 0; i < length; i++) {
                char c = ch[i];
                if (!Character.isLetterOrDigit(c)) {
                    String str = "" + ch[i];
                    if (!str.matches("[\u4e00-\u9fa5]+")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
