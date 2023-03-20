package com.hi.mydemo.tools.file;

import com.hi.mydemo.tools.download.StatusListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileTool {

    public static void writeFile(File file, InputStream is, long totalLength, final StatusListener listener) {

        int sBufferSize = 8192;
        OutputStream os = null;
        long currentLength = 0;
        boolean isCloseFlag = false;
        boolean osCloseFlag = false;
        boolean writeComplete = false;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                int progress = (int) (100 * currentLength / totalLength);
                listener.onProgress(progress, currentLength, totalLength);
            }
            writeComplete = true;
        } catch (IOException e) {
            writeComplete = false;
            listener.onFail(null,e.getMessage());
        } finally {
            try {
                is.close();
                isCloseFlag = true;
            } catch (IOException e) {
                isCloseFlag = false;
            }
            try {
                if (os != null) {
                    os.close();
                }
                osCloseFlag = true;
            } catch (IOException e) {
                osCloseFlag = false;
            }
        }
        if (writeComplete && isCloseFlag && osCloseFlag) {
            //下载完成，并返回保存的文件路径
            listener.onFinish(null,file.getAbsolutePath());
        } else {
            listener.onFail(null,"读写流错误！");
        }
    }

    protected static boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(filePath);
            } else {
                return deleteDirectory(filePath);
            }
        }
    }

    /* 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    protected static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    protected static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        if (files != null) {
            //遍历删除文件夹下的所有文件(包括子目录)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    //删除子文件
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) break;
                } else {
                    //删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) break;
                }
            }
            if (!flag) return false;
        }
        //删除当前空目录
        return dirFile.delete();
    }

}
