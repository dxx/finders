package io.github.dxx.finders.util;

import io.github.dxx.finders.exception.FindersRuntimeException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 utils.
 *
 * @author dxx
 */
public class MD5Utils {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
        '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    
    public static String getMD5String(String str){
        try {
			MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest();
            return byteToHexString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new FindersRuntimeException(e);
        }
    }
    
    private static String byteToHexString(byte[] tmp) {
		String s;
		char[] str = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
		  byte byte0 = tmp[i];
		  str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
		  str[k++] = HEX_DIGITS[byte0 & 0xf];
		}
		s = new String(str);
		return s;
    }
}
