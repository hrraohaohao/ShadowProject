package com.hao.lib.download;


import android.util.Log;

import com.hao.lib.download.handler.CommonHandler;
import com.hao.lib.net.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * Title:子线程下载
 * </p>
 *
 * <p>
 * Description:描述
 * </p>
 */
public class SubDownLoadFileThead extends Thread {
    private final static String TAG = SubDownLoadFileThead.class.getCanonicalName();

    private long startPos; // 开始下载的指针位置
    private long endPos; // 结束下载的指针位置
    private int threadId; // 子线程号
    private final CountDownLatch latch;// 子信号量
    private RandomAccessFile file = null;// 存放的文件
    private RandomAccessFile tempFile = null;// 指针文件
    private DownLoadFileBean downLoadFileBean;
    private int timeout = 10000;// 超时时间
    private int reTryNum = 3;// 超时重试次数
    private int curNum;// 当前重试次数
    private boolean isOK = false;// 下载完成
    private String mis = "";// 提示信息

    private boolean isRange;// 是否支持断点续传

    public SubDownLoadFileThead(DownLoadFileBean downLoadFileBean, CountDownLatch latch, long startPos, long endPos, int threadId) {
        this.isRange = true;
        this.latch = latch;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downLoadFileBean = downLoadFileBean;
        this.threadId = threadId;
        this.mis = "[(" + downLoadFileBean.getFileSiteURL() + ")子线程:" + threadId + "]";
        try {
            file = new RandomAccessFile(downLoadFileBean.getSaveFile(), "rw");
            tempFile = new RandomAccessFile(downLoadFileBean.getTempFile(), "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SubDownLoadFileThead(DownLoadFileBean downLoadFileBean, CountDownLatch latch) {
        this.isRange = false;
        this.latch = latch;
        this.downLoadFileBean = downLoadFileBean;
        this.threadId = 0;
        this.mis = "[(" + downLoadFileBean.getFileSiteURL() + ")子线程:" + threadId + "]";
        try {
            file = new RandomAccessFile(downLoadFileBean.getSaveFile(), "rw");
            tempFile = new RandomAccessFile(downLoadFileBean.getTempFile(), "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        Log.i(TAG, mis + "开始下载!");
        curNum = 0;
        while (curNum < reTryNum && !isOK) {
            if (curNum > 0) {
                Log.i(TAG, mis + "第" + curNum + "次重试下载:");
            }
            downLoad();
        }
        latch.countDown();// 完成之后结束通知
    }

    /**
     * 首次连接,初使化长度
     */
    private void downLoad() {
        InputStream inputStream = null;
        HttpURLConnection con = null;
        Long myFileLength = 0L;// 临时文件长度,用于减少下载进度消息数量
        try {
            curNum++;
            long count = 0;
            URL url = new URL(downLoadFileBean.getFileSiteURL());
            con = HttpRequest.getHttpURLConnection(url, timeout);
            HttpRequest.setConHead(con);
            if (startPos < endPos && isRange) {
                // 设置下载数据的起止区间
                con.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                Log.i(TAG, "'" + downLoadFileBean.getFileSiteURL() + "'-Thread号:" + threadId + " 开始位置:" + startPos + ",结束位置：" + endPos);
                file.seek(startPos);// 转到文件指针位置
            }
            int responseCode = con.getResponseCode();
            // 判断http status是否为HTTP/1.1 206 Partial Content或者200 OK
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                inputStream = con.getInputStream();// 打开输入流
                int len = 0;
                byte[] b = new byte[1024];
                long seek = 4 + 16 * threadId;

                while (!downLoadFileBean.getPauseDownloadFlag() && (len = inputStream.read(b)) != -1) {
                    file.write(b, 0, len);// 写入临时数据文件,外性能需要提高
                    count += len;
                    startPos += len;
                    tempFile.seek(seek);
                    tempFile.writeLong(startPos);// 写入断点数据文件
                    if (downLoadFileBean.getHandlerListener() != null && (count - myFileLength) > 1024) {
                        myFileLength = count;
                        long tempSize = 0;
                        File file = new File(downLoadFileBean.getFileSavePath() + File.separator + downLoadFileBean.getFileSaveName());
                        if (file.exists()) {
                            tempSize = file.length();
                        }
                        int nPercent = (int) (tempSize * 100 / downLoadFileBean.getFileLength());
                        if (downLoadFileBean.getWeakReference() != null && downLoadFileBean.getWeakReference().get() != null && !downLoadFileBean.getWeakReference().get().isFinishing()) {
                            CommonHandler.getInstatnce().handlerMessage(downLoadFileBean.getHandlerListener(), DownLoadFileBean.DOWLOAD_FLAG_ING, 0, downLoadFileBean.getWhich(), nPercent);
                        }
                    }
                }

                if (startPos >= endPos) {
                    isOK = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, mis + "异常:" + e.getMessage());// logger.debug
        } finally {
            try {
                // 关闭连接
                if (con != null) {
                    con.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (file != null) {
                    // 关闭文件
                    file.close();
                }
                if (tempFile != null) {
                    // 文件指针文件
                    tempFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
