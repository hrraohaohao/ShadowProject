/**
 * 
 */
package com.hao.lib.net;

/**
 *****************************************************************************************************************************************************************************
 * 
 * @author :Atar
 * @createTime:2011-8-9下午5:01:53
 * @version:1.0.0
 * @modifyTime:
 * @modifyAuthor:
 * @description:
 *****************************************************************************************************************************************************************************
 */
public class NetWorkMsg {

	public int what, arg1, arg2, arg3;
	public Object obj;

	public NetWorkMsg(int what, int arg1, int arg2, int arg3, Object obj) {
		super();
		this.what = what;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.obj = obj;
	}

}
