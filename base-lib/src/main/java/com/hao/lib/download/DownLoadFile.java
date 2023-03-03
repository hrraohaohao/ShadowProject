package com.hao.lib.download;

import android.app.Activity;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.hao.lib.download.handler.CommonHandler;
import com.hao.lib.interfaces.HandlerListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownLoadFile {
    private String TAG = DownLoadFile.class.getSimpleName();
    /**
     * 默认超时时长
     */
    public final static int DEFAULT_WAITTIME = 5000;

    /**
     * 下载Bean
     */
    private DownLoadFileBean mDownLoadBean;
    /**
     * 下载计数器,用于同步
     */
    private volatile CountDownLatch mPauseLatch;

    public void addHandlerListener(HandlerListener handlerListener) {
        if (mDownLoadBean != null) {
            mDownLoadBean.addHandlerListener(handlerListener);
        }
    }

    public void removeHandlerListener(HandlerListener handlerListener) {
        if (mDownLoadBean != null) {
            mDownLoadBean.removeHandlerListener(handlerListener);
        }
    }

    /**
     * 下载文件
     *
     * @param activity:弱引用持有UI    Activity 在activity finishi时自动停止下载
     * @param handlerListener     下载回调监听
     * @param which               哪一个下载，用于同时下载多个 which值必须不相同
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
    public void downLoad(final Activity activity, HandlerListener handlerListener, int which, String fileUrl, int fileThreadNum, String strDownloadFileName, String strDownloadDir) {
        if (!TextUtils.isEmpty(strDownloadDir)) {
            File file = new File(strDownloadDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            CountDownLatch latch = new CountDownLatch(1);
            mDownLoadBean = new DownLoadFileBean();
            if (activity != null) {
                mDownLoadBean.setWeakReference(new WeakReference<Activity>(activity));// 设置 弱引用持有UI activity
            }
            mDownLoadBean.setFileSiteURL(fileUrl);// 设置远程文件地址
            mDownLoadBean.setFileSaveName(strDownloadFileName);// 设置本地文件名
            mDownLoadBean.setFileSavePath(strDownloadDir);// 设置本地文件路径
            mDownLoadBean.setFileThreadNum(fileThreadNum);// fileThreadNum 个线程下载
            mDownLoadBean.addHandlerListener(handlerListener);// 下载通知消息
            mDownLoadBean.setPauseDownloadFlag(false);// 下载终止标志位设为false
            mDownLoadBean.setWhich(which);// 设置哪一个下载 区别多个同时下载

            DownLoadFileTask zipT = new DownLoadFileTask(mDownLoadBean, latch);
            ExecutorService executor = null;
            try {
                executor = Executors.newSingleThreadExecutor();
                executor.execute(zipT);
                latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
                // 如果不是被调用者停止,则发送成功失败消息
                if (mPauseLatch == null) {
                    Message msg = new Message();
                    if (mDownLoadBean != null) {
                        msg.arg2 = mDownLoadBean.getWhich();
                        DownLoadFileManager.getInstance().remove(msg.arg2);
                        if (mDownLoadBean.getWeakReference() != null && mDownLoadBean.getWeakReference().get() != null && mDownLoadBean.getWeakReference().get().isFinishing()) {
                            Log.i(TAG, "---activity已经关闭---异步线程执行到此结束-------->");
                            mDownLoadBean = null;
                            return;
                        }

                        msg.what = mDownLoadBean.isDownSuccess() ? DownLoadFileBean.DOWLOAD_FLAG_SUCCESS : DownLoadFileBean.DOWLOAD_FLAG_FAIL;
                        CommonHandler.getInstatnce().handlerMessage(mDownLoadBean.getHandlerListener(), msg);
                        mDownLoadBean = null;
                    }
                } else {
                    mPauseLatch.countDown();
                }
            }
        }
    }

    /**
     * 暂停下载
     *
     * @author :Atar
     * @createTime:2017-8-18下午3:26:01
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void pauseDownload() {
        if (mDownLoadBean != null) {
            mDownLoadBean.setPauseDownloadFlag(true);
            mPauseLatch = new CountDownLatch(1);
            try {
                mPauseLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mDownLoadBean.getWeakReference() != null && mDownLoadBean.getWeakReference().get() != null && mDownLoadBean.getWeakReference().get().isFinishing()) {
                    DownLoadFileManager.getInstance().remove(mDownLoadBean.getWhich());
                    Log.i(TAG, "---activity已经关闭---异步线程执行到此结束-------->");
                    mPauseLatch = null;
                    mDownLoadBean = null;
                    return;
                }

                // 发送停止下载消息
                Message msg = new Message();
                msg.arg2 = mDownLoadBean.getWhich();
                msg.what = DownLoadFileBean.DOWLOAD_FLAG_ABORT;
                CommonHandler.getInstatnce().handlerMessage(mDownLoadBean.getHandlerListener(), msg);
                mPauseLatch = null;
                mDownLoadBean = null;
            }
        }
    }
}
