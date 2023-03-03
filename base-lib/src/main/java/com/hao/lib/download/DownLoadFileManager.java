/**
 *
 */
package com.hao.lib.download;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.hao.lib.download.handler.CommonHandler;
import com.hao.lib.interfaces.HandlerListener;
import com.hao.lib.utils.FileUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ****************************************************************************************************************************************************************************
 * 多线程异步下载管理器 ，可同时下载多个,可暂停, 可断点续传
 *
 * @author :Atar
 * @createTime:2011-8-18上午11:46:11
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description: ****************************************************************************************************************************************************************************
 */
public class DownLoadFileManager {
    private String TAG = DownLoadFileManager.class.getSimpleName();

    private static DownLoadFileManager instance;
    private ExecutorService newSingleThreadExecutor;


    /**
     * 弱引用管理多个同时下载
     */
    private Map<String, WeakReference<DownLoadFile>> map;

    public static DownLoadFileManager getInstance() {
        if (instance == null) {
            instance = new DownLoadFileManager();
            instance.map = new HashMap<String, WeakReference<DownLoadFile>>();
        }
        return instance;
    }

    /**
     * 下载文件
     *
     * @param activity:弱引用持有UI    Activity 在activity finish时自动停止下载
     * @param handlerListener     下载回调监听
     * @param which               哪一个下载，用于同时下载多个 which值必须不相同 接收时为 msg.arg2
     * @param fileUrl             文件url
     * @param fileThreadNum       单个文件 多线程数下载 可减少单个文件下载时间 注：如果不是专作如迅雷下载那类，最好不要把fileThreadNum 设置超过3，一般1就可以了
     * @param deleteOnExit        如果存在是否强制删除后再下载
     * @param strDownloadFileName 本地文件名
     * @param strDownloadDir      本地文件目录
     * @author :Atar
     * @createTime:2011-8-18下午1:59:19
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void downLoad(final Activity activity, final HandlerListener handlerListener, final int which, final String fileUrl, final int fileThreadNum, final boolean deleteOnExit,
                         final String strDownloadFileName, final String strDownloadDir) {
        try {
            if (map != null && !map.containsKey(Integer.toString(which))) {
                if (newSingleThreadExecutor == null) {
                    newSingleThreadExecutor = Executors.newSingleThreadExecutor();
                }
                newSingleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DownLoadFile mDownLoadFile = new DownLoadFile();
                            if (map != null) {
                                map.put(Integer.toString(which), new WeakReference<DownLoadFile>(mDownLoadFile));
                            }

                            if (deleteOnExit) {
                                File file = new File(strDownloadDir + File.separator + strDownloadFileName);
                                File tempFile = new File(strDownloadDir + File.separator + strDownloadFileName + ".tmp" + which);
                                if (file.exists() && !tempFile.exists()) {
                                    file.delete();
                                }
                            }
                            mDownLoadFile.downLoad(activity, handlerListener, which, fileUrl, fileThreadNum, strDownloadFileName, strDownloadDir);
                        } catch (Exception e) {
                            Log.e(TAG, "downLoad-->" + e);
                        }
                    }
                });
            } else {
                if (map != null && map.get(Integer.toString(which)) != null) {
                    DownLoadFile mDownLoadFile = map.get(Integer.toString(which)).get();
                    if (mDownLoadFile != null) {
                        mDownLoadFile.addHandlerListener(handlerListener);
                    }
                }
                Log.i(TAG, "该" + which + "号下载 已经在下载");
            }
        } catch (Exception e) {
            Log.e(TAG, "downLoad-->" + e);
        }
    }

    /**
     * 下载文件
     *
     * @param activity:弱引用持有UI    Activity 在activity finish时自动停止下载
     * @param handlerListener     下载回调监听
     * @param which               哪一个下载，用于同时下载多个 which值必须不相同 接收时为 msg.arg2
     * @param fileUrl             文件url
     * @param strDownloadFileName 本地文件名
     * @param strDownloadDir      本地文件目录
     * @author :Atar
     * @createTime:2011-8-18下午1:59:19
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void downLoad(final Activity activity, final HandlerListener handlerListener, final int which, final String fileUrl, final String strDownloadFileName, final String strDownloadDir) {
        downLoad(activity, handlerListener, which, fileUrl, 1, false, strDownloadFileName, strDownloadDir);
    }

    /**
     * 暂停下载
     *
     * @param which 哪一个下载，用于同时下载多个 接收时为 msg.arg2
     * @author :Atar
     * @createTime:2011-8-18下午2:03:52
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void pauseDownload(final int which) {
        if (newSingleThreadExecutor == null) {
            newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        newSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (map != null && map.get(Integer.toString(which)) != null) {
                        DownLoadFile mDownLoadFile = map.get(Integer.toString(which)).get();
                        mDownLoadFile.pauseDownload();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "pauseDownload-->" + e);
                }
            }
        });
    }

    public void removeHandlerListener(final int which, HandlerListener handlerListener) {
        if (newSingleThreadExecutor == null) {
            newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        newSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (map != null && map.get(Integer.toString(which)) != null) {
                        DownLoadFile mDownLoadFile = map.get(Integer.toString(which)).get();
                        mDownLoadFile.removeHandlerListener(handlerListener);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "removeHandlerListener-->" + e);
                }
            }
        });
    }

    /**
     * 获得上次没有下载完的文件进度百分比值
     *
     * @return
     * @author :Atar
     * @createTime:2017-8-18下午3:30:55
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void initTempFilePercent(final int which, final HandlerListener handlerListener, final String fileUrl, final String strDownloadFileName, final String strDownloadDir) {
        if (newSingleThreadExecutor == null) {
            newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        newSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(strDownloadDir + strDownloadFileName);
                    File tempFile = new File(strDownloadDir + strDownloadFileName + ".tmp" + which);
                    if (file.exists() && !tempFile.exists()) {
                        CommonHandler.getInstatnce().handlerMessage(handlerListener, DownLoadFileBean.DOWLOAD_FLAG_SUCCESS, 0, which, 100);
                    } else {
                        if (file.exists()) {
                            long tempSize = file.length();
                            long fileLength = FileUtils.getUrlFileSize(fileUrl);
                            int nPercent = 0;
                            if (fileLength != 0) {
                                nPercent = (int) (tempSize * 100 / fileLength);
                            }
                            CommonHandler.getInstatnce().handlerMessage(handlerListener, DownLoadFileBean.DOWLOAD_FLAG_ING, 0, which, nPercent);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "initTempFilePercent-->" + e);
                }
            }
        });
    }

    /**
     * 是否存在某个文件
     *
     * @param strDownloadFileName
     * @param strDownloadDir
     * @return
     */
    public boolean isExistsFile(final String strDownloadFileName, final String strDownloadDir, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        File file = new File(strDownloadDir + strDownloadFileName);
        File tempFile = new File(strDownloadDir + strDownloadFileName + ".tmp" + url.hashCode());
        if (file.exists() && !tempFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 下载动作结束移除 管理中的此次下载对象
     *
     * @param which
     * @author :Atar
     * @createTime:2011-8-18下午2:10:46
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void remove(int which) {
        try {
            if (map != null && map.containsKey(Integer.toString(which))) {
                map.remove(Integer.toString(which));
            }
        } catch (Exception e) {
            Log.e(TAG, "remove-->" + e);
        }
    }
}
