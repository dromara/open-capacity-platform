package com.open.capacity.gateway.filter;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.codec.CodecProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.open.capacity.common.constant.CommonConstant;
import com.open.capacity.common.dto.ResponseEntity;
import com.open.capacity.common.exception.BusinessException;
import com.open.capacity.common.properties.SecurityProperties;
import com.open.capacity.common.utils.WebfluxResponseUtil;
import com.open.capacity.gateway.chain.SecurityFilterChain;
import com.open.capacity.gateway.context.SecurityContext;
import com.open.capacity.gateway.handler.DefaultSecurityHandler;

import cn.hutool.http.HttpStatus;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 安全防护: 1.端点命令 2.黑名单 3.限流 4.xss攻击
 */
@Slf4j
@Component
public class SecurityChainFilter implements GlobalFilter, Ordered {

	@Resource
	private SecurityFilterChain securityFilterChain;
	private static final String MSG = "安全攻击拦截";
	private static final String ERROR_MSG = "您的请求参数中含有非法字符!";

	@Autowired
	private ObjectMapper objectMapper;
	private List<Pattern> hdivRules = new ArrayList<>();
	private DefaultSecurityHandler hander = new DefaultSecurityHandler();
	private List<HttpMessageReader<?>> messageReaders;
	@Autowired
	private SecurityProperties securityProperties;
	/**
	 * 最低优先级（最大值）：0 大于 0 无效
	 */
	public static final int ORDERED = Ordered.HIGHEST_PRECEDENCE + 10000;
	@Setter
	private int order = ORDERED;

	@Override
	public int getOrder() {
		return order;
	}

	public SecurityChainFilter(ObjectMapper objectMapper, CodecConfigurer codecConfigurer,
			CodecProperties codecProperties) {
		hander.readDefaultValidations();
		List<Map<DefaultSecurityHandler.ValidationParam, String>> validations = hander.getValidations();
		validations.forEach(val -> {
			String regex = val.get(DefaultSecurityHandler.ValidationParam.REGEX);
			hdivRules.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
		});
		this.objectMapper = objectMapper;
		this.messageReaders = fetchMessageReaders(codecConfigurer, codecProperties);

	}

	private List<HttpMessageReader<?>> fetchMessageReaders(CodecConfigurer codecConfigurer,
			CodecProperties codecProperties) {
		PropertyMapper propertyMapper = PropertyMapper.get();
		CodecConfigurer.DefaultCodecs defaultCodecs = codecConfigurer.defaultCodecs();
		propertyMapper.from(codecProperties.getMaxInMemorySize()).whenNonNull().asInt(DataSize::toBytes)
				.to(defaultCodecs::maxInMemorySize);
		return codecConfigurer.getReaders();
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		// 封装请求对象
		SecurityContext securityContext = new SecurityContext();
		securityContext.setExchange(exchange);
		try {
			securityFilterChain.execute(securityContext);
		} catch (Exception e) {
			if (log.isTraceEnabled()) {
				log.trace(" security chains exception :{}", e.getMessage());
			}
		}
		// 获取处理结果
		if (securityContext.isResult()) {
			return WebfluxResponseUtil.responseWrite(exchange, securityContext.getCode(), securityContext.getEntity());
		} else {
			// 基于xss的安全防护需要额外处理
			if (securityProperties.getXss().getEnable()) {
				long contentLength = exchange.getRequest().getHeaders().getContentLength();
				MediaType contentType = exchange.getRequest().getHeaders().getContentType();
				Set<String> headers = exchange.getRequest().getHeaders().values().stream()
						.flatMap(list -> list.stream()).collect(Collectors.toSet());
				try {
					// 校验请求头
					validateParamSet(headers);
					// 校验请求体
					if (contentLength > 0 && (MediaType.APPLICATION_JSON.equals(contentType))) {
						return DataBufferUtils.join(exchange.getRequest().getBody())
								.flatMap(validateJson(exchange, chain));
					}

				} catch (Exception e) {
					return WebfluxResponseUtil.responseWrite(exchange, HttpStatus.HTTP_BAD_REQUEST,
							ResponseEntity.failed(ERROR_MSG));
				}
			}
			return chain.filter(exchange);
		}

	}

	/**
	 * 请求体校验
	 * 
	 * @param exchange
	 * @param chain
	 * @return
	 */
	private Function<DataBuffer, Mono<? extends Void>> validateJson(ServerWebExchange exchange,
			GatewayFilterChain chain) {
		Set<String> params = Sets.newHashSet();
		return dataBuffer -> {
			byte[] bytes = new byte[dataBuffer.readableByteCount()];
			dataBuffer.read(bytes);
			try {
				String bodyString = new String(bytes, CommonConstant.UTF8);
				String patternString = URLDecoder.decode(bodyString.replaceAll("%(?![0-9a-fA-F]{2})", "%25"),
						CommonConstant.UTF8);
				validateParamString(patternString);
				exchange.getAttributes().put("POST_BODY", bodyString);
			} catch (BusinessException e) {
				return WebfluxResponseUtil.responseWrite(exchange, HttpStatus.HTTP_BAD_REQUEST,
						ResponseEntity.failed(ERROR_MSG));
			} catch (Exception e) {
			} finally {
				DataBufferUtils.release(dataBuffer);
			}
			Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
				DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
				return Mono.just(buffer);
			});
			ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
				@Override
				public Flux<DataBuffer> getBody() {
					return cachedFlux;
				}
			};
			return chain.filter(exchange.mutate().request(mutatedRequest).build());
		};

	}

	/**
	 * hdiv安全校验
	 * 
	 * @param paramStr
	 * @param rule
	 * @return
	 */
	private boolean validateParam(String paramStr, Pattern rule) {
		Matcher matcher = rule.matcher(paramStr);
		return matcher.matches();
	}

	/**
	 * 基于请求头的安全防护
	 * 
	 * @param params
	 */
	private void validateParamSet(Set<String> params) {
		Iterator<String> paramIterator = params.iterator();
		while (paramIterator.hasNext()) {
			String param = paramIterator.next();
			Iterator<Pattern> ruleIterator = hdivRules.iterator();
			while (ruleIterator.hasNext()) {
				Pattern rule = ruleIterator.next();
				if (validateParam(param, rule)) {
					throw new BusinessException(MSG);
				}
			}
		}
	}

	/**
	 * 基于requestbodyString的安全防护
	 * 
	 * @param param
	 */
	private void validateParamString(String param) {
		Iterator<Pattern> ruleIterator = hdivRules.iterator();
		while (ruleIterator.hasNext()) {
			Pattern rule = ruleIterator.next();
			if (validateParam(param, rule)) {
				throw new BusinessException(MSG);
			}
		}
	}

}
