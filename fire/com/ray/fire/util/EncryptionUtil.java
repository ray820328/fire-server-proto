package com.ray.fire.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.ray.communicate.FSystem;

public class EncryptionUtil {
	private static Logger logger = Logger.getLogger(EncryptionUtil.class);

	private static Key key;

	private static final String ENCRYPTION_ALGORITHM = "DES";
	private static final String KEY_PATH = "key.ser";

	static {
		try {
			key = readKey();
		}
		catch (Exception ex) {
			logger.debug("An error occurred while reading a key from the file.", ex);
		}
	}

	private static Key readKey() throws Exception {
		ClassLoader classLoader = Class.forName("com.ray.communicate.util.EncryptionUtil").getClassLoader();
		InputStream is = classLoader.getResourceAsStream(KEY_PATH);
		ObjectInputStream in = new ObjectInputStream(is);
		key = (Key) in.readObject();
		return key;
	}

	public static void writeKey() throws Exception {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(KEY_PATH));
			out.writeObject(generateKey());
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static Key generateKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance("DES");
		generator.init(new SecureRandom()); // generates a 56-bit random key
		return generator.generateKey();
	}

	public static String generateBase64Key() throws NoSuchAlgorithmException {
		return new String(Base64.encodeBase64(generateKey().getEncoded(), false)); // prevent chunking the data ('\n\r' is appended to the result)
	}

	public static byte[] encrypt(byte[] bytes) throws Exception {
		Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(bytes);
	}

	public static String encryptToBase64(byte[] bytes) throws Exception {
		return new String(Base64.encodeBase64(encrypt(bytes), false));
	}

	/**
	 * 编码成16进制可显示字符
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encryptToHex(byte[] bytes) throws Exception {
//		return new String(Hex.encodeHex(encrypt(bytes)));
		return new String(Hex.encodeHex(bytes));
	}

	public static byte[] decrypt(byte[] encryptedBytes) throws Exception {
		Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(encryptedBytes);
	}

	public static String decryptFromBase64(byte[] bytes) throws Exception {
		return new String(decrypt(Base64.decodeBase64(bytes)));
	}

	/**
	 * 还原成byte流
	 * @param hexChars
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptFromHex(char[] hexChars) throws Exception {
//		return new String(decrypt(Hex.decodeHex(hexChars)));
//		byte[] bytes = new byte[hexChars.length];
//		for(int i=0; i<bytes.length; i++){
//			bytes[i] = hexchar2byte(hexChars[i]);
//		}
//		return bytes;
		return Hex.decodeHex(hexChars);
	}
	
	public static byte hexchar2byte(char hexChar){
		switch(hexChar){
		case '0':
			return 0 & 0xFF;
		case '1':
			return 1 & 0xFF;
		case '2':
			return 2 & 0xFF;
		case '3':
			return 3 & 0xFF;
		case '4':
			return 4 & 0xFF;
		case '5':
			return 5 & 0xFF;
		case '6':
			return 6 & 0xFF;
		case '7':
			return 7 & 0xFF;
		case '8':
			return 8 & 0xFF;
		case '9':
			return 9 & 0xFF;
		case 'a':
			return 10 & 0xFF;
		case 'b':
			return 11 & 0xFF;
		case 'c':
			return 12 & 0xFF;
		case 'd':
			return 13 & 0xFF;
		case 'e':
			return 14 & 0xFF;
		case 'f':
			return 15 & 0xFF;
			
			default:
				throw new RuntimeException("hexchar2byte 错误: " + hexChar);
		}
	}

	public static String hashMD5(String plainText) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] digestedBytes = messageDigest.digest(plainText.getBytes("UTF-8"));
		BigInteger number = new BigInteger(1, digestedBytes);
		return number.toString(16);
	}

	public static String hashMD5Base64(String text) throws Exception {
		return new String(Base64.encodeBase64(hashMD5(text).getBytes(), false));
	}

	public static byte[] encryptXOR(byte[] datas, int start, int end, byte[] key) {
		for(int i=start; i<end; i++){
			datas[i] = (byte)(datas[i] ^ key[0]);
		}
		return datas;
	}
	
	public static void main(String[] args) throws Exception {
		byte[] datas = "12".getBytes();
		System.out.println(decryptFromBase64("Mkh6H4NpVGs=".getBytes()));
		System.out.println(decryptFromBase64("0tMatY+aJCQ=".getBytes()));
		System.out.println(encryptToHex("client4".getBytes()));
		System.out.println(decryptFromHex("0d8bc86788cc4815".toCharArray()));
		
		byte[] dest = encryptXOR(datas, 0, datas.length, FSystem.encryptKey);
		System.out.println(new String(dest));
		datas = dest;
		dest = encryptXOR(datas, 0, datas.length, FSystem.encryptKey);
		System.out.println(new String(dest));
	}
}