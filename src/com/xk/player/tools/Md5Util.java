package com.xk.player.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.xml.internal.ws.util.StringUtils;


public class Md5Util {
	// 全局数组
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	public Md5Util() {
	}

	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	// 返回形式只为数字
	private static String byteToNum(byte bByte) {
		int iRet = bByte;
		System.out.println("iRet1=" + iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		return String.valueOf(iRet);
	}

	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	public static String MD5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);// buf.toString();
			System.out.println("MD5(" + sourceStr + ",32) = " + result);
			System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		return result;
	}

	public static String GetMD5Code(String strObj) {
		String resultString = null;
		MessageDigest md = null;
		try {
			resultString = new String(strObj);
			md = MessageDigest.getInstance("MD5");
			// md.digest() 该函数返回值为存放哈希值结果的byte数组
			resultString = byteToString(md.digest(strObj.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException ex) {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw, true));
		} catch (UnsupportedEncodingException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			if (md != null)
				resultString = byteToString(md.digest(strObj.getBytes()));
		}
		return resultString;
	}

}
