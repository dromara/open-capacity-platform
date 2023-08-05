package com.open.capacity.common.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 根据 IP 地址或子网掩码匹配远程地址的请求。
 */
public final class IpAddressMatcher {

	private final int nMaskBits;

	private final InetAddress requiredAddress;

	/**
	 * 采用特定的 IP 地址或使用 IP/Netmask 指定的范围 (e.g. 192.168.1.0/24 or 202.24.0.0/14).
	 * 
	 * @param ipAddress 请求必须来自的地址或地址范围。
	 */
	public IpAddressMatcher(String ipAddress) {
		String separator = "/";
		if (ipAddress.indexOf(separator) > 0) {
			String[] addressAndMask = StringUtils.split(ipAddress, separator);
			assert addressAndMask != null;
			ipAddress = addressAndMask[0];
			this.nMaskBits = Integer.parseInt(addressAndMask[1]);
		} else {
			this.nMaskBits = -1;
		}
		this.requiredAddress = parseAddress(ipAddress);
		Assert.isTrue(this.requiredAddress.getAddress().length * 8 >= this.nMaskBits,
				String.format("IP 地址 %s 对于长度为 %d 的位掩码来说太短了", ipAddress, this.nMaskBits));
	}

	private InetAddress parseAddress(String address) {
		try {
			return InetAddress.getByName(address);
		} catch (UnknownHostException ex) {
			throw new IllegalArgumentException("解析地址失败" + address, ex);
		}
	}

	/**
	 * 匹配
	 * 
	 * @param address 通配地址
	 * @return 匹配结果
	 */
	public boolean matches(String address) {
		InetAddress remoteAddress = parseAddress(address);
		if (!this.requiredAddress.getClass().equals(remoteAddress.getClass())) {
			return false;
		}
		if (this.nMaskBits < 0) {
			return remoteAddress.equals(this.requiredAddress);
		}
		byte[] remAddr = remoteAddress.getAddress();
		byte[] reqAddr = this.requiredAddress.getAddress();
		int nMaskFullBytes = this.nMaskBits / 8;
		byte finalByte = (byte) (0xFF00 >> (this.nMaskBits & 0x07));
		for (int i = 0; i < nMaskFullBytes; i++) {
			if (remAddr[i] != reqAddr[i]) {
				return false;
			}
		}
		if (finalByte != 0) {
			return (remAddr[nMaskFullBytes] & finalByte) == (reqAddr[nMaskFullBytes] & finalByte);
		}
		return true;
	}

}
