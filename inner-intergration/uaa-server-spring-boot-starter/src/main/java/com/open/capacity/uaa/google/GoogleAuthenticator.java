package com.open.capacity.uaa.google;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

/**
 * Google身份验证器
 * 
 */
public class GoogleAuthenticator {
	// 生成的key长度( Generate secret key length)
	public static final int SECRET_SIZE = 10;
	public static final String SEED = "g8GjEvTbW5oVSV7avL47357438reyhreyuryetredLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";
	// Java实现随机数算法
	public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
	// 最多可偏移的时间，默认3，最大17
	int window_size = 10;
	// default 3 - max 17

	/**
	 * set the windows size. This is an integer value representing the number of 30
	 * second windows we allow The bigger the window, the more tolerant of clock
	 * skew we are.
	 * 
	 * window size - must be >=1 and <=17. Other values are ignored 5
	 * 
	 */
	public void setWindowSize(int size) {
		if (size >= 1 && size <= 17) {
			window_size = size;
		}
	}

	/**
	 * Generate a random secret key. This must be saved by the server and associated
	 * with the users account to verify the code displayed by Google Authenticator.
	 * The user must register this secret on their device. 生成一个随机秘钥
	 * 
	 */
	public static String generateSecretKey() {
		SecureRandom sr = null;
		try {
			sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
			sr.setSeed(Base64.decodeBase64(SEED));
			byte[] buffer = sr.generateSeed(SECRET_SIZE);
			Base32 codec = new Base32();
			byte[] bEncodedKey = codec.encode(buffer);
			String encodedKey = new String(bEncodedKey);
			return encodedKey;
		} catch (NoSuchAlgorithmException e) {
			// should never occur... configuration error
		}

		return null;
	}

	/**
	 * Return a URL that generates and displays a QR barcode. The user scans this
	 * bar code with the Google Authenticator application on their smartphone to
	 * register the auth code. They can also manually enter the secret if desired
	 * 
	 */
	public static String getQRBarcodeURL(String user, String host, String secret) {
		String format = "http://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s?secret=%s";
		return String.format(format, user, host, secret);
	}

	/**
	 * 生成一个google身份验证器，识别的字符串，只需要把该方法返回值生成二维码扫描就可以了
	 * 
	 */
	public static String getQRBarcode(String user, String secret) {
		String format = "otpauth://totp/%s?secret=%s";
		return String.format(format, user, secret);
	}

	/**
	 * Check the code entered by the user to see if it is valid 验证code是否合法
	 * 
	 */
	public boolean checkCode(String secret, long code, long timeMsec) {
		Base32 codec = new Base32();
		byte[] decodedKey = codec.decode(secret);
		// convert unix msec time into a 30 second "window"
		// this is per the TOTP spec (see the RFC for details)
		long t = (timeMsec / 1000L) / 30L;
		// Window is used to check codes generated in the near past.
		// You can use this value to tune how far you're willing to go.
		for (int i = -window_size; i <= window_size; ++i) {
			long hash;
			try {
				hash = verifyCode(decodedKey, t + i);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			if (hash == code) {
				return true;
			}
		}
		return false;
	}

	private static int verifyCode(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] data = new byte[8];
		long value = t;
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}
		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);
		int offset = hash[20 - 1] & 0xF;
		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = 0; i < 4; ++i) {
			truncatedHash <<= 8;
			// We are dealing with signed bytes:
			// we just keep the first byte.
			truncatedHash |= (hash[offset + i] & 0xFF);
		}
		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;
		return (int) truncatedHash;
	}
}
