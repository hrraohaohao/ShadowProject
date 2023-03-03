package com.hao.lib.download;

import android.app.Activity;


import com.hao.lib.interfaces.HandlerListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:文件下载的BEAN
 */
public class DownLoadFileBean {

    /**
     * 下载失败
     */
    public final static int DOWLOAD_FLAG_FAIL = 98765850;
    /**
     * 下载成功
     */
    public final static int DOWLOAD_FLAG_SUCCESS = 98765851;
    /**
     * 正在下载
     */
    public final static int DOWLOAD_FLAG_ING = 98765852;
    /**
     * 下载失败
     */
    public final static int DOWLOAD_FLAG_ABORT = 98765853;

    /**
     * 文件长度
     */
    private long fileLength = -1;

    /**
     * 文件的网络地址
     */
    private String fileSiteURL;

    /**
     * 文件保存的路径
     */
    private String fileSavePath;

    /**
     * 保存的文件名
     */
    private String fileSaveName;

    /**
     * 临时文件
     */
    private String fileTempName;

    /**
     * 单个文件 多线程数下载
     */
    private int fileThreadNum;

    /**
     * 下载回调HandlerListener
     */
    private Map<String, HandlerListener> HandlerListeners = new HashMap<String, HandlerListener>();
    /**
     * 区分哪一下载，主要用于多个下载区分哪一个
     */
    private int which;

    private final static int FILE_THREAD_NUM = 1;

    private boolean isDownSuccess = false;

    /**
     * 下载暂停标志
     */
    private volatile boolean mAbortDownload;

    /**
     * 业务描述
     */
    private String bussMemo;

    /**
     * 是否支持断点续传
     */
    private boolean isRange = true;

    /**
     * 弱引用持有UI Activity
     */
    private WeakReference<Activity> weakReference;

    public DownLoadFileBean() {
        super();
        // this.latch = latch;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isRange() {
        return this.isRange;
    }

    public void setIsRange(boolean isRange) {
        this.isRange = isRange;
    }

    /**
     * @return the fileSiteURL
     */
    public String getFileSiteURL() {
        return fileSiteURL;
    }

    /**
     * @param fileSiteURL the fileSiteURL to set
     */
    @SuppressWarnings("unused")
    public void setFileSiteURL(String fileSiteURL) {
        // 重新编码所含中文
        String strTemp = "\\%20";
        String strRight = fileSiteURL.substring(fileSiteURL.lastIndexOf("/") + 1, fileSiteURL.length() - 1);
        try {
            this.fileSiteURL = fileSiteURL.replaceAll(strRight, URLEncoder.encode(strRight, "utf-8"));
            // handle replace "+"->\\%20
            this.fileSiteURL = this.fileSiteURL.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // this.fileSiteURL = fileSiteURL;
        // this.fileSiteURL = "http://192.168.115.171:8337/wcf//Resources/images/DishSort/smallimage/Blue%20hills.jpg";
    }

    /**
     * @return the fileSavePath
     */
    public String getFileSavePath() {
        return fileSavePath;
    }

    /**
     * @param fileSavePath the fileSavePath to set
     */
    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    /**
     * @return the fileSaveName
     */
    public String getFileSaveName() {
        return fileSaveName;
    }

    /**
     * @param fileSaveName the fileSaveName to set
     */
    public void setFileSaveName(String fileSaveName) {
        this.fileSaveName = fileSaveName;
    }

    /**
     * @return the fileThreadNum
     */
    public int getFileThreadNum() {
        if (fileThreadNum == 0) {
            return FILE_THREAD_NUM;
        }
        return fileThreadNum;
    }

    /**
     * @param fileThreadNum the fileThreadNum to set
     */
    public void setFileThreadNum(Integer fileThreadNum) {
        this.fileThreadNum = fileThreadNum;
    }

    /**
     * @return the fileTempName
     */
    public String getFileTempName() {
        if (fileTempName == null) {
            return ".tmp" + which;
        }
        return fileTempName;
    }

    /**
     * @param fileTempName the fileTempName to set
     */
    public void setFileTempName(String fileTempName) {
        this.fileTempName = fileTempName;
    }

    public File getSaveFile() {
        File file = new File(this.getFileSavePath() + File.separator + this.getFileSaveName());
        return file;
    }

    public File getTempFile() {
        File file = new File(this.getFileSavePath() + File.separator + this.getFileSaveName() + this.getFileTempName());
        return file;
    }

    /**
     * @return the isDownSuccess
     */
    public boolean isDownSuccess() {
        return isDownSuccess;
    }

    /**
     * @param isDownSuccess the isDownSuccess to set
     */
    public void setDownSuccess(boolean isDownSuccess) {
        this.isDownSuccess = isDownSuccess;
    }

    /**
     * @return the bussMemo
     * 业务描述
     */
    public String getBussMemo() {
        return bussMemo;
    }

    /**
     * 业务描述
     *
     * @param bussMemo the bussMemo to set
     */
    public void setBussMemo(String bussMemo) {
        this.bussMemo = bussMemo;
    }

    public void addHandlerListener(HandlerListener handlerListener) {
        if (handlerListener != null) {
            String key = handlerListener.toString();
            if (HandlerListeners != null && !HandlerListeners.containsKey(key)) {
                HandlerListeners.put(key, handlerListener);
            }
        }
    }

    public void removeHandlerListener(HandlerListener handlerListener) {
        if (handlerListener != null) {
            String key = handlerListener.toString();
            if (HandlerListeners != null && HandlerListeners.containsKey(key)) {
                HandlerListeners.remove(key);
            }
        }
    }


    /**
     * 下载通知消息.进度/成功/失败
     *
     * @return
     */
    public Map<String, HandlerListener> getHandlerListener() {
        return HandlerListeners;
    }

    /**
     * 设置下载终止标志位
     *
     * @param bl
     */
    public void setPauseDownloadFlag(boolean bl) {
        mAbortDownload = bl;
    }

    /**
     * 获得下载终止标志位
     *
     * @return
     */
    public boolean getPauseDownloadFlag() {
        if (weakReference != null && weakReference.get() != null && weakReference.get().isFinishing()) {
            mAbortDownload = true;
            DownLoadFileManager.getInstance().pauseDownload(which);
        }
        return mAbortDownload;
    }

    public int getWhich() {
        return which;
    }

    public void setWhich(int whcih) {
        this.which = whcih;
    }

    public WeakReference<Activity> getWeakReference() {
        return weakReference;
    }

    public void setWeakReference(WeakReference<Activity> weakReference) {
        this.weakReference = weakReference;
    }
}
