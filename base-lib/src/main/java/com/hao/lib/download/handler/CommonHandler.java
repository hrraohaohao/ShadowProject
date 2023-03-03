/**
 *
 */
package com.hao.lib.download.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.hao.lib.interfaces.HandlerListener;
import com.hao.lib.net.NetWorkMsg;

import java.util.Map;


/**
 * ****************************************************************************************************************************************************************************
 * 全局公共 只用一个唯一的 handler 加回调接口  处理异步线程到UI之间的通信
 *
 * @author :Atar
 * @createTime:2011-8-11下午1:55:31
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description: 如果我们在每个activity或是其他类里面都new 一个handler其实是比较低效率的做法，完全可以只用一个全局的静态handler来进行线程和主线程的通信。
 * ****************************************************************************************************************************************************************************
 */
public class CommonHandler {
    private String TAG = CommonHandler.class.getSimpleName();

    private static CommonHandler instance;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static synchronized CommonHandler getInstatnce() {
        if (instance == null) {
            instance = new CommonHandler();
        }
        return instance;
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * 模拟Handler 回调到主线程
     *
     * @param mHandlerListener
     * @param msgWhat
     * @param msgArg1
     * @param msgArg2
     * @param obj
     * @author :Atar
     * @createTime:2011-8-16下午1:39:11
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void handlerMessage(final HandlerListener mHandlerListener, final int msgWhat, final int msgArg1, final int msgArg2, final Object obj) {
        if (handler != null && mHandlerListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message msg = handler.obtainMessage(msgWhat, msgArg1, msgArg2, obj);
                        mHandlerListener.onHandlerData(msg);
                    } catch (Exception e) {
                        Log.e(TAG, "handlerMessage-->" + e);
                    }
                }
            });
        }
    }

    /**
     * 模拟Handler 回调到主线程
     *
     * @param mHandlerListener
     * @param msg
     * @author :Atar
     * @createTime:2011-8-16下午1:39:11
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    public void handlerMessage(final HandlerListener mHandlerListener, final Message msg) {
        if (handler != null && mHandlerListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandlerListener.onHandlerData(msg);
                    } catch (Exception e) {
                        Log.e(TAG, "handlerMessage-->" + e);
                    }
                }
            });
        }
    }

    public void handlerMessage(final Map<String, HandlerListener> mHandlerListeners, final int msgWhat, final int msgArg1, final int msgArg2, final Object obj) {
        if (handler != null && mHandlerListeners != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message msg = handler.obtainMessage(msgWhat, msgArg1, msgArg2, obj);
                        for (HandlerListener handlerListener : mHandlerListeners.values()) {
                            if (handlerListener != null) {
                                handlerListener.onHandlerData(msg);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "handlerMessage-->" + e);
                    }
                }
            });
        }
    }

    public void handlerMessage(final Map<String, HandlerListener> mHandlerListeners, final Message msg) {
        if (handler != null && mHandlerListeners != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (HandlerListener handlerListener : mHandlerListeners.values()) {
                            if (handlerListener != null) {
                                handlerListener.onHandlerData(msg);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "handlerMessage-->" + e);
                    }
                }
            });
        }
    }
}
