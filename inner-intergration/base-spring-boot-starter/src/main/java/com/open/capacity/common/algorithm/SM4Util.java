package com.open.capacity.common.algorithm;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import com.open.capacity.common.exception.BusinessException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class SM4Util extends GMBaseUtil {
	public static final String ALGORITHM_NAME = "SM4";
	public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
	public static final String ALGORITHM_NAME_ECB_NOPADDING = "SM4/ECB/NoPadding";
	public static final String ALGORITHM_NAME_CBC_PADDING = "SM4/CBC/PKCS5Padding";
	public static final String ALGORITHM_NAME_CBC_NOPADDING = "SM4/CBC/NoPadding";
	public static final int DEFAULT_KEY_SIZE = 128;
	public static final String DEFAULT_KEY = "0123456789abcdeffedcba9876543210";

	public static final boolean ENCRYPTIONBREAK = true;

	public static String decrypt(String text, String key) {
		try {
			byte[] paramsByte = ByteUtils.fromHexString(text);
			byte[] decryptedData = SM4Util.decryptEcbPadding(
					ByteUtils.fromHexString(
							StringUtils.isNotEmpty(key) ? key.substring(0, 32) : DEFAULT_KEY.substring(0, 32)),
					paramsByte);
			return new String(decryptedData, "UTF-8");
		} catch (Exception e) {
			throw new BusinessException("请求数据解密失败！");
		}
	}

	public static String encrypt(String text, String key) {
		byte[] cipherText = null;
		try {
			byte[] resultByte = text.getBytes("UTF-8");
			cipherText = SM4Util.encryptEcbPadding(
					ByteUtils.fromHexString(
							StringUtils.isNotEmpty(key) ? key.substring(0, 32) : DEFAULT_KEY.substring(0, 32)),
					resultByte);
		} catch (Exception e) {
			throw new BusinessException("响应数据加密失败！");
		} finally {
			return ByteUtils.toHexString(cipherText);
		}

	}

	public static byte[] generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		return generateKey(DEFAULT_KEY_SIZE);
	}

	public static byte[] generateKey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
		kg.init(keySize, new SecureRandom());
		return kg.generateKey().getEncoded();
	}

	public static byte[] encryptEcbPadding(byte[] key, byte[] data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static byte[] decryptEcbPadding(byte[] key, byte[] cipherText)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(cipherText);
	}

	public static byte[] encryptEcbNoPadding(byte[] key, byte[] data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_NOPADDING, Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	public static byte[] decryptEcbNoPadding(byte[] key, byte[] cipherText)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_NOPADDING, Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(cipherText);
	}

	public static byte[] encryptCbcPadding(byte[] key, byte[] iv, byte[] data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		Cipher cipher = generateCbcCipher(ALGORITHM_NAME_CBC_PADDING, Cipher.ENCRYPT_MODE, key, iv);
		return cipher.doFinal(data);
	}

	public static byte[] decryptCbcPadding(byte[] key, byte[] iv, byte[] cipherText)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {
		Cipher cipher = generateCbcCipher(ALGORITHM_NAME_CBC_PADDING, Cipher.DECRYPT_MODE, key, iv);
		return cipher.doFinal(cipherText);
	}

	public static byte[] encryptCbcNoPadding(byte[] key, byte[] iv, byte[] data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		Cipher cipher = generateCbcCipher(ALGORITHM_NAME_CBC_NOPADDING, Cipher.ENCRYPT_MODE, key, iv);
		return cipher.doFinal(data);
	}

	public static byte[] decryptCbcNoPadding(byte[] key, byte[] iv, byte[] cipherText)
			throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidAlgorithmParameterException {
		Cipher cipher = generateCbcCipher(ALGORITHM_NAME_CBC_NOPADDING, Cipher.DECRYPT_MODE, key, iv);
		return cipher.doFinal(cipherText);
	}

	public static byte[] doCMac(byte[] key, byte[] data) throws NoSuchProviderException, NoSuchAlgorithmException,
			InvalidKeyException {
		Key keyObj = new SecretKeySpec(key, ALGORITHM_NAME);
		return doMac("SM4-CMAC", keyObj, data);
	}

	public static byte[] doGMac(byte[] key, byte[] iv, int tagLength, byte[] data) {
		org.bouncycastle.crypto.Mac mac = new GMac(new GCMBlockCipher(new SM4Engine()), tagLength * 8);
		return doMac(mac, key, iv, data);
	}

	/**
	 * 默认使用PKCS7Padding/PKCS5Padding填充的CBCMAC
	 *
	 * @param key
	 * @param iv
	 * @param data
	 * @return
	 */
	public static byte[] doCBCMac(byte[] key, byte[] iv, byte[] data) {
		SM4Engine engine = new SM4Engine();
		org.bouncycastle.crypto.Mac mac = new CBCBlockCipherMac(engine, engine.getBlockSize() * 8, new PKCS7Padding());
		return doMac(mac, key, iv, data);
	}

	public static byte[] doCBCMac(byte[] key, byte[] iv, BlockCipherPadding padding, byte[] data) {
		SM4Engine engine = new SM4Engine();
		org.bouncycastle.crypto.Mac mac = new CBCBlockCipherMac(engine, engine.getBlockSize() * 8, padding);
		return doMac(mac, key, iv, data);
	}


	private static byte[] doMac(org.bouncycastle.crypto.Mac mac, byte[] key, byte[] iv, byte[] data) {
		CipherParameters cipherParameters = new KeyParameter(key);
		mac.init(new ParametersWithIV(cipherParameters, iv));
		mac.update(data, 0, data.length);
		byte[] result = new byte[mac.getMacSize()];
		mac.doFinal(result, 0);
		return result;
	}

	private static byte[] doMac(String algorithmName, Key key, byte[] data) throws NoSuchProviderException,
			NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
		mac.init(key);
		mac.update(data);
		return mac.doFinal();
	}

	private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key)
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			InvalidKeyException {
		Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
		Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
		cipher.init(mode, sm4Key);
		return cipher;
	}

	private static Cipher generateCbcCipher(String algorithmName, int mode, byte[] key, byte[] iv)
			throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException {
		Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
		Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		cipher.init(mode, sm4Key, ivParameterSpec);
		return cipher;
	}
}
