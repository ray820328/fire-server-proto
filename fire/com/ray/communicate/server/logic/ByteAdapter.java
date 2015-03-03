package com.ray.communicate.server.logic;
/**
 * 不同语言传输数据类型和byte之间的转化可能不同，如
 * c中，31L的16进制从左往右输出结果是1F00000000000000，java中是000000000000001F
 * @author RaYi
 *
 */
public abstract class ByteAdapter {

	/**
	 * 语言适配器名字，介于两种语言
	 * @return
	 */
	public String getAdapterName(){
		return "java2java";
	}
	
	/**
	 * 将目标语言传输过来的byte流转换成java对应数值
	 * @param data
	 * @return
	 */
	public abstract int fromBytes(byte[] data);
	
	/**
	 * 将java对应数值转换成目标语言等值的编码byte流
	 * @param value
	 * @return
	 */
	public abstract byte[] toBytes(int value);
}
