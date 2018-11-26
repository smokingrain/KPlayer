package com.xk.player.tools;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密
 * @author 图灵机器人
 *
 */
public class Md5 {
	/**
	 * MD5加密算法
	 * 
	 * 说明：32位加密算法
	 * 
	 * @param 待加密的数据
	 * @return 加密结果，全小写的字符串
	 */
	public static byte[] MD5(byte[] bt) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			return md.digest(bt);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
