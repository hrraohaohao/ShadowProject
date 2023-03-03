package com.hao.lib.download;




import android.util.Log;

import com.hao.lib.net.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownLoadFileTask extends Thread {
    private final static String TAG = DownLoadFileTask.class.getCanonicalName();
    /**
     * 文件信息 Bean
     */
    private DownLoadFileBean downLoadFileBean = null;

    /**
     * 开始位置
     */
    private long[] startPos;

    /**
     * 结束位置
     */
    private long[] endPos;

    /**
     * 文件长度
     */
    private long fileLength = -1;

    /**
     * 文件下载的临时信息
     */
    private File tempFile;

    /**
     * 下载是否成功的标记
     */
    private boolean isLoadSuccess = false;

    /**
     * 外部信号量
     */
    private final CountDownLatch selfLatch;
    /**
     * 提示信息
     */
    private String mis;

    public DownLoadFileTask(DownLoadFileBean downLoadFileBean, CountDownLatch selfLatch) {
        this.selfLatch = selfLatch;
        this.init(downLoadFileBean);
    }

    public DownLoadFileTask(DownLoadFileBean downLoadFileBean) {
        this.selfLatch = null;
        this.init(downLoadFileBean);
    }

    public void init(DownLoadFileBean downLoadFileBean) {
        this.downLoadFileBean = downLoadFileBean;
        this.mis = "{下载:(" + downLoadFileBean.getFileSiteURL() + "}";
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        if (startConnet()) {
            // 1:读取上次下载信息
            initThread();
            // 2:分多个线程下载文件
            if (!isLoadSuccess) {
                int threadNum = downLoadFileBean.getFileThreadNum();
                ExecutorService exec = Executors.newFixedThreadPool(threadNum);
                CountDownLatch latch = new CountDownLatch(threadNum);
                Log.i(TAG, this.mis + "开始");

                boolean isRange = downLoadFileBean.isRange();
                if (isRange) {
                    for (int i = 0; i < threadNum; i++) {
                        // 开启子线程，并执行。
                        SubDownLoadFileThead thread;
                        thread = new SubDownLoadFileThead(downLoadFileBean, latch, startPos[i], endPos[i], i);
                        exec.execute(thread);// 线程交给线程池做处理
                    }
                } else {
                    SubDownLoadFileThead thread;
                    thread = new SubDownLoadFileThead(downLoadFileBean, latch);
                    exec.execute(thread);// 线程交给线程池做处理
                }
                try {
                    // 等待CountdownLatch信号为0，表示所有子线程都结束。
                    latch.await();
                    exec.shutdown();
                    // this.latch.countDown();//计数器-1
                    File file = downLoadFileBean.getSaveFile();
                    // 删除临时文件
                    long downloadFileSize = file.length();
                    String msg = "失败";
                    if (downloadFileSize == this.fileLength) {
                        tempFile.delete();// 临时文件删除
                        msg = "成功";
                        downLoadFileBean.setDownSuccess(true);// 下载成功
                        // 下载成功,处理解析文件
                    }
                    long end = System.currentTimeMillis();
                    Log.i(TAG, msg + "下载'" + downLoadFileBean.getFileSaveName() + "'花时：" + (double) (end - start) / 1000 + "秒");
                    DownLoadFileManager.getInstance().remove(downLoadFileBean.getWhich());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                downLoadFileBean.setDownSuccess(true);// 下载成功
            }
        }
        if (selfLatch != null)
            selfLatch.countDown();

    }

    private void initThread() {
        RandomAccessFile tempFileFos = null;
        try {
            File file = downLoadFileBean.getSaveFile();
            tempFile = downLoadFileBean.getTempFile();// 断点文件
            int threadNum = downLoadFileBean.getFileThreadNum();
            startPos = new long[threadNum];// 初使化个数
            endPos = new long[threadNum];// 初使化个数
            if (file.exists()) {
                long localFileSize = file.length();// 本地的文件大小
                if (localFileSize < this.fileLength || tempFile.exists()) {// 小于的开始断点下载
                    Log.i(TAG, "重新断点续传..");
                    /** 线程数下载的大小 */
                    tempFileFos = new RandomAccessFile(tempFile, "rw");
                    // 从临时文件读取断点位置
                    int num = tempFileFos.readInt();
                    Log.i(TAG, "启动的线程数" + num);
                    for (int i = 0; i < threadNum; i++) {
                        startPos[i] = tempFileFos.readLong();// 开始的位置
                        endPos[i] = tempFileFos.readLong();// 结束的位置
                    }

                } else {
                    isLoadSuccess = true;
                }
            } else {
                // 目标文件不存在，则创建新文件
                file.createNewFile();
                tempFile.createNewFile();
                tempFileFos = new RandomAccessFile(tempFile, "rw");
                long fileThreadSize = this.fileLength / threadNum;// 每个线程需要下载的大小
                tempFileFos.writeInt(threadNum);// 首个写入线程数量
                for (int i = 0; i < threadNum; i++) {
                    // 创建子线程来负责下载数据，每段数据的起始位置为(threadLength * i)
                    startPos[i] = fileThreadSize * i;
                    /*
                     * 设置子线程的终止位置，非最后一个线程即为(threadLength * (i + 1) - 1) 最后一个线程的终止位置即为下载内容的长度
                     */
                    if (i == threadNum - 1) {
                        endPos[i] = this.fileLength;
                    } else {
                        endPos[i] = fileThreadSize * (i + 1) - 1;
                    }
                    // end position
                    tempFileFos.writeLong(startPos[i]);
                    // current position
                    tempFileFos.writeLong(endPos[i]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "异常：" + e.getMessage());
        } finally {
            try {
                if (tempFileFos != null) {
                    tempFileFos.close();// 非空关闭临时数据文件
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 首次连接,增加重试机制
     */
    private boolean startConnet() {
        try {
            Thread.sleep(300);// 本线程等待300再访问连接
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        int curNum = 0;// 重试计数器
        int tryNum = 1;// 重试的次数
        int waitTime = 500;// 等待500ms
        boolean connect = false;
        // 将有3次的重试机会.大概花时3s
        while (curNum < tryNum && !connect) {
            if (curNum > 0) {
                Log.i(TAG, this.mis + "第" + curNum + "次重试连接:");
                try {
                    Thread.sleep(waitTime * curNum);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            connect = connnect();
            curNum++;
        }
        return connect;
    }

    /**
     * 实体连接,初使化长度
     */
    private boolean connnect() {
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(downLoadFileBean.getFileSiteURL());
            httpConnection = HttpRequest.getHttpURLConnection(url, 10000);
            HttpRequest.setConHead(httpConnection);
            httpConnection.connect();
            int responseCode = httpConnection.getResponseCode();
            if (responseCode <= 400) {
                fileLength = httpConnection.getContentLength();// 设置下载长度
                this.downLoadFileBean.setFileLength(fileLength);
                return true;// 失败成功
            }
            Log.i(TAG, this.mis + "-请求返回responseCode=" + responseCode + ",连接失败");

        } catch (Exception e) {
            Log.e(TAG, this.mis + "-请求连接" + downLoadFileBean.getFileSiteURL() + "异常:" + e.getMessage());
        } finally {
            if (httpConnection != null)
                httpConnection.disconnect();// 关闭连接
        }
        return false;// 失败返回
    }
}
