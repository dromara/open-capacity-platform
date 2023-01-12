package com.open.capacity.common.algorithm;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class GMBaseUtil {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
}
