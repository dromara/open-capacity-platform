package com.open.capacity.common.algorithm;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import lombok.experimental.UtilityClass;

/**
 * RSA加解密工具类
 *
 * @author someday
 * @date 2018/7/16
 */
@UtilityClass
public class RsaUtils {
	private static final int DEFAULT_KEY_SIZE = 2048;

	private static final String KEY_ALGORITHM = "RSA";

	// 生成秘钥对
	public static KeyPair getKeyPair() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGenerator.initialize(DEFAULT_KEY_SIZE);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return keyPair;
	}

	// 获取公钥(Base64编码)
	public static String getPublicKey(KeyPair keyPair) {
		PublicKey publicKey = keyPair.getPublic();
		byte[] bytes = publicKey.getEncoded();
		return byte2Base64(bytes);
	}
	 //获取私钥(Base64编码)
    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }

	// 字节数组转Base64编码
	public static String byte2Base64(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	// Base64编码转字节数组
	public static byte[] base642Byte(String base64Key) throws IOException {
		return Base64.decodeBase64(base64Key);
	}

	// 获取公钥
	public static RSAPublicKey getRsaPublicKey(String key) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	// 获取私钥
	public static RSAPrivateKey getRsaPrivateKey(String key) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

//	@SneakyThrows
//	public static void main(String[] args) {
//		
//		KeyPair pair = RsaUtils.getKeyPair() ;
//		
//		System.out.println(RsaUtils.getPublicKey(pair));
//		
//		System.out.println(RsaUtils.getPrivateKey(pair));
//		
//		System.out.println(RsaUtils.getRsaPublicKey(
//				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnYc57WQIkob6S0DMaKogRQ24fl8dn0zVRGdJ/XXvqb8g2FfEVHUt5jNnka1c00pbUJ40fEfZAnQe/DRx3m+XFevCQ64ZTzaDS8T2GiquHY5i1IbNYcS2oFBoac4Bpw4lFVQM/je+nTU0EhOHDXHSwlykTX07nGjG3ebrjFTfineCBJvHuG75p30Wl3m83rbrEy4Y/15chq83hUvaRwyCxaIJk+3MNgv3PGGI2LVlb/mVwp2mMb0xqmhMIBIon80BY1o9DZbONYy7cd9ygWrV+OE3xkQqUn3iDhNRXZCPKVJOn7rXmDvlO02IrpjQLyROrmUVs6TYsZafZj+UBUvHkwIDAQAB"));
//		System.out.println(RsaUtils.getRsaPrivateKey(
//				"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdhzntZAiShvpLQMxoqiBFDbh+Xx2fTNVEZ0n9de+pvyDYV8RUdS3mM2eRrVzTSltQnjR8R9kCdB78NHHeb5cV68JDrhlPNoNLxPYaKq4djmLUhs1hxLagUGhpzgGnDiUVVAz+N76dNTQSE4cNcdLCXKRNfTucaMbd5uuMVN+Kd4IEm8e4bvmnfRaXebzetusTLhj/XlyGrzeFS9pHDILFogmT7cw2C/c8YYjYtWVv+ZXCnaYxvTGqaEwgEiifzQFjWj0Nls41jLtx33KBatX44TfGRCpSfeIOE1FdkI8pUk6futeYO+U7TYiumNAvJE6uZRWzpNixlp9mP5QFS8eTAgMBAAECggEBAI3L22jEUrMSrNpwSY8tFD8USq32EEQEdTRNhVyRfFnfoUEcP62GjPpZ6zBGTPgkRm4a+kOkqMJ8pCeBTb5b1DP3M7aYUE/tUeIyORT0tiYEtRF5Bgare3hy6InU1cf6A4dfURLEOuBns6dRzI9dlck+eucFg1MXPWshIYYAH/gSkT33I8Mc5SANHP2sau1yynaKOiRGVOsjojFFV+TFBogdqSmB5vC2MSYWfyWl2NrwBRFuUASkyUrh+ZStrp/pPCX2V5syVNEpmt4VgvnnKWEY6rDnKI75ME3+8U2rabzXn9HkeYMMIP9t3WlyP6UbStYWAxdiVTu0z/K5qet534ECgYEA0H+9iCRtIsUKOLdl3g4+b1+zoik7fiSlpWW7gf20fNAohXLr6CEcVTwIZ6DbgUzX2BCcADB9hhzoFeNkZM+5pKnYeQeuRn5GpNsJD48IFOMQ484+uDFc5+aRyHvPGDe8g9lqSrNljpPJ0Kq3Wkm7oUmfyschSLVkQomSqkVwaj0CgYEAwWq5lIUTglQIF2N5Rl2jW8pwvZhoeGEY/xyvM+3YJddSjv+zbfb0EYbyIz+bIU3u/D+Q4ft3BG24knwb8OKmTMIs3ApPFTSbghuXi3+Rm3xtDpAAnSa+zUTy7AwOBGmhDgObAKz9EIvQzaWFxPLU2ro9O7q08a+jR6b2P8E6pg8CgYB0PEK1e1k8YQzpwZSQzvAPtxK1/LamBFg0dRqrxXyxeHqeqwrJRmw9OFKXMx4sX2KVmezeTiosckEQssxtR0D/g5CjKJ0Hv64yicnrWpnuywfHeFuO5DYfMnNjiWJS5f+6oaKYMpsTeWCxa/r74s8vg5QJwCnWFwnl7jxNoJijIQKBgDnjln3EQq2MNwvBAwgzeHQ9p/anCgWVkiSw0iNpuXrB8nzIPUzZT/169TfljqmuKKwo3lU0rU+PA2IccURsobdxmnmVQEwpfa24Umg3Gz59RCrKoNRoR00qqMZLaVqavtBx9c+C9w8fubQDKYFj6J86hJ8+52860VTdvPAiBkqzAoGBAMLzchZaMT4ABcX2M8ptVBRC8OvdDc2D2TcRycnqg4GxJdM5Kc7553lBgaJ9Rr+m0YKMTOEVAPjPkfF2jRibFsG+xQouxyOvdGfCAXenaJbLcPG51nPKHaV3ubc79rcd6wx1LuobqFsG0mjQ/H1A2DJq3bzysWFGwO0+CJLx98FH"));
//	}
}
