package org.rhino.octopus.slaver.executor.model;

/**
 * 任务在跑的时候的参数
 * @author 王铁
 */
public class JobRunParam {

	/**
	 * 要执行的类
	 */
	private String clsName;

	/**
	 * 运行时参数
	 */
	private String param;
	
	/**
	 * jar的类路径
	 */
	private String libPath;

	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getLibPath() {
		return libPath;
	}

	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}
}
