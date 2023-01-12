package com.open.capacity.uaa.service.impl;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.open.capacity.common.algorithm.SM2Util;
import com.open.capacity.common.algorithm.SM3Util;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.constant.SecurityConstants;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.feign.SmsFeignClient;
import com.open.capacity.common.feign.UserFeignClient;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.utils.StringUtil;
import com.open.capacity.redis.repository.RedisRepository;
import com.open.capacity.uaa.exception.ValidateCodeException;
import com.open.capacity.uaa.google.GoogleOTPAuthUtil;
import com.open.capacity.uaa.service.IValidateCodeService;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author someday
 * @date 2018/12/10
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@Service
public class ValidateCodeServiceImpl implements IValidateCodeService {
    @Autowired
    private RedisRepository redisRepository;

    @Resource
    private UserFeignClient userFeignClient;

    @Autowired
    private SmsFeignClient smsFeignClient ;
    /**
     * 保存用户验证码，和randomStr绑定
     *
     * @param deviceId 客户端生成
     * @param imageCode 验证码信息
     */
    @Override
    public void saveImageCode(String deviceId, String imageCode) {
        redisRepository.setExpire(buildKey(deviceId), imageCode, SecurityConstants.DEFAULT_IMAGE_EXPIRE);
    }

    /**
     * 发送验证码
     * <p>
     * 1. 先去redis 查询是否 60S内已经发送
     * 2. 未发送： 判断手机号是否存 ? false :产生4位数字  手机号-验证码
     * 3. 发往消息中心-》发送信息
     * 4. 保存redis
     *
     * @param mobile 手机号
     * @return true、false
     */
    @Override
    public ResponseEntity sendSmsCode(String mobile) {
        Object tempCode = redisRepository.get(buildKey(mobile));
        if (tempCode != null) {
            log.error("用户:{}验证码未失效{}", mobile, tempCode);
            return ResponseEntity.failed("验证码未失效，请失效后再次申请");
        }

        SysUser user = userFeignClient.findByMobile(mobile);
        if (user == null) {
            log.error("根据用户手机号{}查询用户为空", mobile);
            return ResponseEntity.failed("手机号不存在");
        }
//        smsFeignClient.sendSmsCode(mobile) ;
        String code = RandomUtil.randomNumbers(4);
        log.info("短信发送请求消息中心 -> 手机号:{} -> 验证码：{}", mobile, code);
        redisRepository.setExpire(buildKey(mobile), code, SecurityConstants.DEFAULT_IMAGE_EXPIRE);
        return ResponseEntity.succeed("true");
    }
    
	@Override
	@SneakyThrows
	public BufferedImage createQrCode(String username) {

		String secret = GoogleOTPAuthUtil.generateSecret(64);
		String codeUrl = GoogleOTPAuthUtil.generateTotpURI(username, secret);
		  //生成二维码配置
        Map<EncodeHintType,Object> hints =  new HashMap<>();
        //设置纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
        //编码类型
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl,BarcodeFormat.QR_CODE,200,200,hints);
		
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig();
		BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
        return bufferedImage;
	}

    /**
     * 获取验证码
     * @param deviceId 前端唯一标识/手机号
     */
    @Override
    public String getCode(String deviceId) {
        return (String)redisRepository.get(buildKey(deviceId));
    }

    /**
     * 删除验证码
     * @param deviceId 前端唯一标识/手机号
     */
    @Override
    public void remove(String deviceId) {
        redisRepository.del(buildKey(deviceId));
    }

    /**
     * 验证验证码
     */
    @Override
    public void validate(String deviceId, String validCode) {
        if (StrUtil.isBlank(deviceId)) {
            throw new ValidateCodeException("请在请求参数中携带deviceId参数");
        }
        String code = this.getCode(deviceId);
        if (StrUtil.isBlank(validCode)) {
            throw new ValidateCodeException("请填写验证码");
        }
        if (code == null) {
            throw new ValidateCodeException("验证码不存在或已过期");
        }
        if (!StrUtil.equals(code, validCode.toLowerCase())) {
            throw new ValidateCodeException("验证码不正确");
        }
        this.remove(deviceId);
    }

    private String buildKey(String deviceId) {
        return SecurityConstants.DEFAULT_CODE_KEY + ":" + deviceId;
    }
    
    @Override
	@SneakyThrows
	public BufferedImage createQrCode(String deviceId, String secret) {

		String codeUrl = GoogleOTPAuthUtil.generateTotpURI(deviceId, secret);
		  //生成二维码配置
        Map<EncodeHintType,Object> hints =  new HashMap<>();
        //设置纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);
        //编码类型
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl,BarcodeFormat.QR_CODE,200,200,hints);
		
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig();
		BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
        return bufferedImage;
	}
	 /**
     * 保存谷歌动态令牌应用信息
     *
     * @param deviceId 客户端生成
     * @param imageCode 应用密钥
     */
    @Override
	public void saveQrSecret(String deviceId ,String secret) {
		  redisRepository.setExpire(String.format("%s:%s", SecurityConstants.QR_CODE_KEY,deviceId) , secret , SecurityConstants.DEFAULT_QRCODE_EXPIRE );
	}
	@Override
	public void validateDynamicToken(String deviceId, String validCode) {
		if (StrUtil.isBlank(deviceId)) {
            throw new ValidateCodeException("请在请求参数中携带deviceId参数");
        }
        String secret =  (String)redisRepository.get( String.format("%s:%s", SecurityConstants.QR_CODE_KEY,deviceId)  );
        if (StrUtil.isBlank(secret)) {
            throw new ValidateCodeException("请重新生成应用密钥");
        }
        if (secret == null) {
            throw new ValidateCodeException("应用密钥不存在或已过期");
        }
        if(!GoogleOTPAuthUtil.verify(secret, validCode)) {
        	 throw new ValidateCodeException("动态口令校验异常");
        }
        this.remove(String.format("%s:%s", SecurityConstants.QR_CODE_KEY,deviceId) );
	}

	@Override
	public ResponseEntity saveSmKey(String deviceId,String publicKey, String privateKey) {

		redisRepository.setExpire(String.format("%s%s", CommonConstant.SM_PRIVATE_KEY, deviceId), privateKey,
				SecurityConstants.DEFAULT_SMKEY_EXPIRE);

		return  ResponseEntity.succeed(publicKey);
	}

	@Override
	@SneakyThrows
	public String validateSmkey(String deviceId, String password) {
		Assert.isTrue(!StringUtil.isBlank(password), "解析密码错误，密码为空!");
		String privateKey = (String) redisRepository
				.get(String.format("%s%s", CommonConstant.SM_PRIVATE_KEY, deviceId));
		// 如果发生异常，直接原样返回
		Assert.isTrue(!StringUtil.isEmpty(privateKey), "密码验证Key丢失,解析密码失败!");
		ECPrivateKeyParameters priKey = new ECPrivateKeyParameters(new BigInteger(ByteUtils.fromHexString(privateKey)),
				SM2Util.DOMAIN_PARAMS);
		byte[] passwordDecrypt = null;
		try {
			passwordDecrypt = SM2Util.decrypt(priKey, ByteUtils.fromHexString(password));
		} catch (InvalidCipherTextException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new ValidateCodeException("解析密码错误，密码传输过程中被篡改!");
		}
		String realPass = "";
		String[] pass = new String(passwordDecrypt).split("\\|");
		Assert.isTrue(pass.length == 2, "解析密码错误，密码传输过程中被篡改!");
		realPass = pass[0];
		Assert.isTrue(!StringUtil.isBlank(realPass), "解析密码错误，密码为空!");
		// 密码传输防串改
		boolean matchFlag = SM3Util.verify(pass[0].getBytes("utf-8"), ByteUtils.fromHexString(pass[1]));
		Assert.isTrue(matchFlag, "解析密码错误，密码传输过程中被篡改!");
		this.remove(String.format("%s%s", CommonConstant.SM_PRIVATE_KEY, deviceId));
		return realPass;
	}

}
