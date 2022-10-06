package com.itwasneo.common.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.util.Optional;

public class RedisRepository<T> {

	private static final Logger logger = LoggerFactory.getLogger(RedisRepository.class);
	private static final String KEY_DELIMITER = ":";
	private final JedisPooled redisPool;
	private final ObjectMapper objectMapper;

	public RedisRepository(JedisPooled redisPool, ObjectMapper objectMapper) {
		this.redisPool = redisPool;
		this.objectMapper = objectMapper;
	}

	public Optional<T> findByKeys(Class<T> type, String... keys) {
		try {
			String redisResult = redisPool.get(createRedisKey(keys));
			if (redisResult == null || redisResult.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(objectMapper.readValue(redisResult, type));
		} catch (JsonProcessingException jpe) {
			logger.error("", jpe);
			return Optional.empty();
		}
	}

	public String save(T obj, String... keys) {
		try {
			return redisPool.set(createRedisKey(keys), objectMapper.writeValueAsString(obj));
		} catch (JsonProcessingException jpe) {
			logger.error("", jpe);
			return "Couldn't Save";
		}
	}

	private String createRedisKey(String... keys) {
		return String.join(KEY_DELIMITER, keys);
	}

}
