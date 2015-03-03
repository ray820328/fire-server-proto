package com.ray.fire.util;
/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2010-8-10)</p>
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	public static int readInt(InputStream in) throws IOException {
		byte[] bytes = new byte[4];
		in.read(bytes);
		return DigitalUtil.bytes2Int(bytes);
	}
	
	public static short readShort(InputStream in) throws IOException {
		byte[] bytes = new byte[2];
		in.read(bytes);
		return DigitalUtil.bytes2Short(bytes);
	}
	
	public static void writeInt(int value, OutputStream out) throws IOException {
		byte[] bytes = DigitalUtil.int2bytes(value);
		out.write(bytes);
	}
	
	public static void writeShort(short value, OutputStream out) throws IOException {
		byte[] bytes = DigitalUtil.short2bytes(value);
		out.write(bytes);
	}
}
