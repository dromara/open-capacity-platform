package com.open.capacity.uaa.common.util;

import com.open.capacity.common.algorithm.RsaUtils;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.utils.JsonUtil;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.stream.Collectors;

/**
 * jwt工具类
 *
 * @author someday
 * @date 2018/7/21
 */
@UtilityClass
@SuppressWarnings("all")
public class JwtUtils {

	private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjd3fpGLrlQ3tdWGnvYpXFN2u6/W1nvscakCFE/7XbUNkEuJpQuiO2xNcbMeZxX7Sb01FbiQmPa9Rjug3WckVxgRNXKSGSIZ6BV/VN8UPmYMgC0/kM1l33ywrMRa1+T8/tZIX8zPeBcfBi3PbKKJNkPHZtb8w+qVGuAdsBNGaPe4Xq7rOrOIR5/cSiF3a0wJkQTRC99egIf6UPjpvKIXOLvKnjLKLR1wVvA0ERoAKeeNliB084cPjfyaIssOFOcJR48nA4aPeqUYjpOwF8wiDr/hqlJveBsnjswCPVWjnQqZtotG0jzrqABncfIwzNfIHiN6YUVQBick+u29z/bkJBwIDAQAB";
	private static final String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCN3d+kYuuVDe11Yae9ilcU3a7r9bWe+xxqQIUT/tdtQ2QS4mlC6I7bE1xsx5nFftJvTUVuJCY9r1GO6DdZyRXGBE1cpIZIhnoFX9U3xQ+ZgyALT+QzWXffLCsxFrX5Pz+1khfzM94Fx8GLc9sook2Q8dm1vzD6pUa4B2wE0Zo97herus6s4hHn9xKIXdrTAmRBNEL316Ah/pQ+Om8ohc4u8qeMsotHXBW8DQRGgAp542WIHTzhw+N/Joiyw4U5wlHjycDho96pRiOk7AXzCIOv+GqUm94GyeOzAI9VaOdCpm2i0bSPOuoAGdx8jDM18geI3phRVAGJyT67b3P9uQkHAgMBAAECggEAH6BAxRLQVfA+6ZYVoOmANESfRDcIgCKW2RD9GdIMEG2fl54lvSa/AhO03QM6Qo7dNrbRLZadd87nraYTAM8VLYmz/V0oGm7wMNaGX+JscdqLgDoVM2VUlcnw7waTFreE6mNhN3RuoHdBrGbtiod0J0XjNOMkdhv2QfxVllVrvzZJ1IOZ4ZUv0QD/SsZPHo/kTOcejfX6qA1oikFxaP3yVUqjXrkG0wxbbdWNWEd6wmaylLDHVbIcoxCtiVgUMngB6K0XqSgH5NKzHd1m8H3vOyacu0/rkVRL0IlC4yd+p69dwycvuzlveJYHejxLdvcXJWgEWuX1JtWYAwAFx4jR0QKBgQC+6t1T9ULj0nF2IUBBPgDC16MvDyrMO/Ba5X/1xERL4AsLiI/gv5mYn3a76CU9WCNxcSVkuLw4AgqgeDRC2VrtT7/fW/UiE0nsJore9kOn3hWq+ysq0MQ1gDV9uVzrMdY7/0/0G65vdstlj3VYvNaf2Ro5+AviK1drtts2Z+MhZQKBgQC+OmjPMFTTfZ/1j1qLTb5CiHN5hNvqrVFA9QbydnSLra7cXnkg+rhRRIVD9LdV6Z45NUr4LsB9Gq5Y92vNY3L8agCxgcNTDU0is9Axtn1NfDRndrwZsbtLx2Xny1Cs2C5VRRSSmyDqOQdAxxrjhwuHJdi7y98p1VZnmJs8XdLv+wKBgCP35ajarTZ0wFGMJCnmf4g1zhWgmuSNalQFptzrlB5jYFFU8h5ampppeJogO1vaThsiQPQ/5Z37rHrdCqOJhZruCm0PSxiXX7bw7/rflJF8wsJKvbA8Uqrts6YzFPFumzkEedC4ol13mQxSQb5tHVZSNaG0PLDoj3jIo3YVjnINAoGAVwOs0alQA/Xg55iNWE9VimWUPK/TUMO6+TWb2ejpYsMty0vDqJxHF5V6SB12h2v3nj+MbX4lOpSrz7JNs4OcI23xp0CuMh1RfWA3CaZqJrl71x/u5unvvLgXGzJbUtUITKTuxikDo2A2uyDYYd9yHH3pZ+3XxPD7NyQNTk20oWUCgYBukenIrkE++27VSyLhmsiWCFGkN8Pw7WDpCYoHdkCD3zRQUWouZoPMl6A34+wcLT9ZgH4yo1BMwIF/SnhaIB5DPZ2C2Xxnsc+fXEKtRP8IKS4+P94Jz/jRlo4iv2w6ioJyV/JgCL3QGjSDw6hhHxeE/wJSZbmiprGcwrVR2PjbNQ==";

	/**
	 * {"exp":1563256084,"user_name":"admin","authorities":["ADMIN"],"jti":"4ce02f54-3d1c-4461-8af1-73f0841a35df","client_id":"webApp","scope":["app"]}
	 * 
	 * @param jwtToken token值
	 * @return
	 */
	@SneakyThrows
	public JsonNode decodeAndVerify(String jwtToken) {

		SignatureVerifier rsaVerifier = new RsaVerifier(RsaUtils.getRsaPublicKey(PUBLIC_KEY));
		Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, rsaVerifier);
		return JsonUtil.parse(jwt.getClaims());

	}

	/**
	 * 判断jwt是否过期
	 * 
	 * @param claims   jwt内容
	 * @param currTime 当前时间
	 * @return 未过期：true，已过期：false
	 */
	public boolean checkExp(JsonNode claims, long currTime) {
		long exp = claims.get("exp").asLong();
		if (exp < currTime) {
			return false;
		}
		return true;
	}

	/**
	 * 判断jwt是否过期
	 * 
	 * @param claims jwt内容
	 * @return 未过期：true，已过期：false
	 */
	public boolean checkExp(JsonNode claims) {
		return checkExp(claims, System.currentTimeMillis());
	}

	@SneakyThrows
	public Jwt encode(CharSequence content) {
		Signer rsaSigner = new RsaSigner(RsaUtils.getRsaPrivateKey(PRIVATE_KEY));
		return JwtHelper.encode(content, rsaSigner);
	}

	public String encodeStr(CharSequence content) {
		return encode(content).getEncoded();
	}
}
