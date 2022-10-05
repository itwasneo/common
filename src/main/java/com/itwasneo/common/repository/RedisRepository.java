package com.itwasneo.common.repository;

import redis.clients.jedis.JedisPooled;

import java.util.Optional;

public class RedisRepository {

	private static final String KEY_DELIMITER = ":";

	private final JedisPooled redisPool;

	public RedisRepository(JedisPooled redisPool) {
		this.redisPool = redisPool;
	}

	public Optional<String> findByKeys(String... keys) {
		return Optional.ofNullable(redisPool.get(createRedisKey(keys)));
	}

	public String save(String[] keys, Object obj) {
		return redisPool.jsonSet(createRedisKey(keys), obj);
	}

	private String createRedisKey(String... keys) {
		return String.join(KEY_DELIMITER, keys);
	}

}
