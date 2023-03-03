package com.hao.lib.net;

/**
 * *****************************************************************************************
 *
 * @className: 反射自定义异常枚举
 * @author: Atar
 * @createTime:2014-5-18下午11:08:34
 * @modifyTime:
 * @version: 1.0.0
 * @description: *****************************************************************************************
 */
public class ExceptionEnum {

    /**
     * @className:反射错误异常基类
     * @author: Atar
     * @createTime:2014-5-18下午11:12:03
     * @modifyTime:
     * @version: 1.0.0
     * @description:反射中出现的异常 或  反射调用其它方法中出现的异常   都自定义继承此异常基类
     */
    @SuppressWarnings("serial")
    public static class RefelectException extends RuntimeException {
        public RefelectException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className: HTTP协议错误
     * @author: Atar
     * @createTime:2014-5-18下午11:17:28
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpProtocolException extends RefelectException {
        public HttpProtocolException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:HTTPSocket通讯错误
     * @author: Atar
     * @createTime:2014-5-18下午11:20:20
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpIOException extends RefelectException {
        public HttpIOException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:XML解析错误
     * @author: Atar
     * @createTime:2014-5-18下午11:20:38
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class XmlParserException extends RefelectException {
        public XmlParserException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:XMLSocket通讯错误
     * @author: Atar
     * @createTime:2014-5-18下午11:21:27
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class XmlIOException extends RefelectException {
        public XmlIOException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:连接服务器超时，空对象作为反射时产生异常
     * @author: Atar
     * @createTime:2014-5-18下午11:23:27
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionTimeOutException extends RefelectException {
        public ReflectionTimeOutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:没有找到反射类
     * @author: Atar
     * @createTime:2014-5-18下午11:24:44
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionClassNotFoundException extends RefelectException {
        public ReflectionClassNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    /**
     * @className: 反射函数安全错误
     * @author: Atar
     * @createTime:2014-5-18下午11:26:02
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionSecurityException extends RefelectException {
        public ReflectionSecurityException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:反射指针引用错误
     * @author: Atar
     * @createTime:2014-5-18下午11:26:26
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionIllegalAccessException extends RefelectException {
        public ReflectionIllegalAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:反射方法中有传入为null的参数
     * @author: Atar
     * @createTime:2014-5-18下午11:27:11
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionParamHasNullException extends RefelectException {
        public ReflectionParamHasNullException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:反射类中调用的方法不存在
     * @author: Atar
     * @createTime:2014-5-18下午11:27:54
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionNoSuchMethodErrorException extends RefelectException {
        public ReflectionNoSuchMethodErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * @className:反射方法中传入参数不合法
     * @author: Atar
     * @createTime:2014-5-18下午11:28:10
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionIllegalArgumentException extends RefelectException {
        public ReflectionIllegalArgumentException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * GsonAnalyException:Json解析出错
     *
     * @author: Atar
     * @createTime:2014-5-19下午8:41:21
     * @modifyTime:
     * @version: 1.0.0
     * @description:
     */
    @SuppressWarnings("serial")
    public static class GsonJsonParserException extends RefelectException {
        public GsonJsonParserException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 服务器找Host不对
     *
     * @author :Atar
     * @createTime:2014-12-12下午2:18:05
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionUnknownHostException extends RefelectException {
        public ReflectionUnknownHostException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 服务端出错
     *
     * @author :Atar
     * @createTime:2015-9-16下午2:46:56
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionUnknownServiceException extends RefelectException {
        public ReflectionUnknownServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 通信编码错误
     *
     * @author :Atar
     * @createTime:2015-9-16下午2:54:11
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionUnsupportedEncodingException extends RefelectException {
        public ReflectionUnsupportedEncodingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 所以Activity已经关闭，网络请求不作回掉处理
     *
     * @author :Atar
     * @createTime:2015-9-21下午4:16:06
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class ReflectionActivityFinished extends RefelectException {
        public ReflectionActivityFinished(String message, Throwable cause) {
            super(message, cause);
        }
    }

	/* ***********************************************************************以下特殊几个常见code处理****************************************************************************************** */

    /**
     * http请求400
     *
     * @author :Atar
     * @createTime:2014-12-8上午11:44:12
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse400 extends RefelectException {
        public HttpRequestFalse400(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * http请求403
     *
     * @author :Atar
     * @createTime:2014-12-8上午11:44:12
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse403 extends RefelectException {
        public HttpRequestFalse403(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * http请求404
     *
     * @author :Atar
     * @createTime:2014-12-10上午9:56:32
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse404 extends RefelectException {
        public HttpRequestFalse404(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * http请求405
     *
     * @author :Atar
     * @createTime:2014-12-10上午9:56:32
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse405 extends RefelectException {
        public HttpRequestFalse405(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * http请求502
     *
     * @author :Atar
     * @createTime:2014-12-10上午9:47:58
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse502 extends RefelectException {
        public HttpRequestFalse502(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * http请求503
     *
     * @author :Atar
     * @createTime:2014-12-10上午9:52:02
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse503 extends RefelectException {
        public HttpRequestFalse503(String message, Throwable cause) {
            super(message, cause);
        }

    }

    /**
     * http请求504
     *
     * @author :Atar
     * @createTime:2014-12-10上午9:53:15
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse504 extends RefelectException {
        public HttpRequestFalse504(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * http请求500
     *
     * @author :Atar
     * @createTime:2014-12-10上午9:53:15
     * @version:1.0.0
     * @modifyTime:
     * @modifyAuthor:
     * @description:
     */
    @SuppressWarnings("serial")
    public static class HttpRequestFalse500 extends RefelectException {
        public HttpRequestFalse500(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class HttpRequestFalse401 extends RefelectException {
        public HttpRequestFalse401(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
