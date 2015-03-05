package com.ray.utils.util;

public class ArrayUtil {

	public static int[][] add2row(int[][] oldArray, int[] data){
		int[][] newDatas = new int[oldArray.length + 1][];
		for(int i=0; i<oldArray.length; i++){
//			复制原始数据
			newDatas[i] = new int[oldArray[i].length];
			System.arraycopy(oldArray[i], 0, newDatas[i], 0, oldArray[i].length);
		}
		newDatas[newDatas.length-1] = data;
		return newDatas;
	}
	public static int[][] copy(int[][] oldArray){
		int[][] newDatas = new int[oldArray.length][];
		for(int i=0; i<oldArray.length; i++){
//			复制原始数据
			newDatas[i] = new int[oldArray[i].length];
			System.arraycopy(oldArray[i], 0, newDatas[i], 0, oldArray[i].length);
		}
		return newDatas;
	}
}
