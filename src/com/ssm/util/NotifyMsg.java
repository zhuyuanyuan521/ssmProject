package com.ssm.util; 
/** 
 * @author 作者 陈怀傲 
 * @version 创建时间：2014年3月5日 下午2:01:20 
 * 类说明 
 */
public class NotifyMsg {
	private int key;//返回信息的标识键,默认为0，
	private String msg;//返回信息的提示信息
	
	public NotifyMsg(int key, String msg) {
		super();
		this.key = key;
		this.msg = msg;
	}
	public void init(int key, String msg){
		this.key = key;
		this.msg = msg;
	}
	public NotifyMsg() {
		super();
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
 