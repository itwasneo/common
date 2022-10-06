package com.itwasneo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itwasneo.common.repository.RedisRepository;
import io.activej.config.Config;
import io.activej.inject.Injector;
import io.activej.inject.Key;
import io.activej.inject.module.ModuleBuilder;
import redis.clients.jedis.JedisPooled;

public class CommonProvider {

	private static final Injector injector = Injector.of(ModuleBuilder.create()
			// Config
			.bind(Config.class).to(() -> Config.ofClassPathProperties("application.properties"))

			// JedisPool
			.bind(JedisPooled.class).to(c -> {
				c = c.getChild("redis");
				return new JedisPooled(c.get("host"), Integer.parseInt(c.get("port")));
			}, Config.class)

			// ObjectMapper
			.bind(ObjectMapper.class).to(() -> new ObjectMapper().registerModule(new JavaTimeModule()))

			// Redis Repository
			.bind(RedisRepository.class).to(RedisRepository::new, JedisPooled.class, ObjectMapper.class)

			// END
			.build());

	/**
	 * Gets RedisRepository instance.
	 *
	 * @return RedisRepository
	 */
	public static <T> RedisRepository<T> getRedisRepository() {
		Key<RedisRepository<T>> key = Key.ofType(RedisRepository.class);
		return injector.getInstance(key);
	}

	private CommonProvider() {}
}