package com.ray.communicate.util;

import com.ray.utils.util.EncryptionUtil;

/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2010-8-10)</p>
 */
public class DigitalUtil {

	/**
	 * 字节数组的低位是整型的高位字节位
	 * @param bytes
	 * @return
	 */
	public static int bytes2Int(byte[] bytes){
		if(bytes == null){
			throw new RuntimeException("byte values is null.");
		}
//		System.out.println("int bs = " + bytes.length);
		switch(bytes.length){
		case 0:
			throw new RuntimeException("no byte values");
		case 1:
			return bytes[0];
		case 2:
			return bytes[0]<<8 | bytes[1];
		case 3:
			return bytes[0]<<16 | bytes[1]<<8 | bytes[2];
		}
		return (bytes[0] & 0xFF)<<24 | (bytes[1] & 0xFF)<<16 | (bytes[2] & 0xFF)<<8 | bytes[3] & 0xFF;
	}
	
	/**
	 * 字节数组的低位是整型的高位字节位
	 * @param bytes
	 * @return
	 */
	public static byte[] int2bytes(int value){
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (value >> 24 & 0xFF);
		bytes[1] = (byte) (value >> 16 & 0xFF);
		bytes[2] = (byte) (value >> 8 & 0xFF);
		bytes[3] = (byte) (value & 0xFF);
//		System.out.println(value + "\n");
		return bytes;
	}
	
	public static byte[] wrapInts(int[] datas){
		byte[] bytes = new byte[datas.length * 4];
		int index = 0;
		for(int i=0; i<datas.length; i++){
			byte[] dataBytes = int2bytes(datas[i]);
			bytes[index++] = dataBytes[0];
			bytes[index++] = dataBytes[1];
			bytes[index++] = dataBytes[2];
			bytes[index++] = dataBytes[3];
		}
		return bytes;
	}
	
	/**
	 * 字节数组的低位是整型的高位字节位
	 * @param bytes
	 * @return
	 */
	public static short bytes2Short(byte[] bytes){
		if(bytes == null){
			throw new RuntimeException("byte values is null.");
		}
		switch(bytes.length){
		case 0:
			throw new RuntimeException("no byte values");
		case 1:
			return bytes[0];
		}
		return (short)((bytes[0] & 0xFF)<<8 | (bytes[1] & 0xFF));
	}
	
	/**
	 * 字节数组的低位是整型的高位字节位
	 * @param bytes
	 * @return
	 */
	public static byte[] short2bytes(short value){
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ((value >> 8) & 0xFF);
		bytes[1] = (byte) (value & 0xFF);
		return bytes;
	}
	
	public static String getHex(byte[] datas)throws Exception{
		return EncryptionUtil.encryptToHex(datas);
	}
	
	public static void main(String[] args){
		try{
			int aa = 173173173;
			byte[] bytes = int2bytes(aa);
			String hexString = EncryptionUtil.encryptToHex(bytes);
			System.out.println(hexString);
			
			aa = bytes2Int(int2bytes(aa));
			System.out.println(aa);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}