package com.itwasneo.common.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.activej.config.Config;
import io.activej.inject.Injector;
import io.activej.inject.module.ModuleBuilder;
import redis.clients.jedis.JedisPooled;

public class CommonProvider {

	private static final Injector injector = Injector.of(ModuleBuilder.create()
			.bind(Config.class).to(() -> Config.ofClassPathProperties("application.properties"))
			.bind(JedisPooled.class).to(c -> {
				c = c.getChild("redis");
				return new JedisPooled(c.get("host"), Integer.parseInt(c.get("port")));
			}, Config.class)
			.bind(RedisRepository.class).to(RedisRepository::new, JedisPooled.class)
			.bind(ObjectMapper.class).to(() -> new ObjectMapper().registerModule(new JavaTimeModule()))
			.build());

	/**
	 *	Gets RedisRepository instance.
	 * @return RedisRepository
	 */
	public static RedisRepository getRedisRepository() {
		return injector.getInstance(RedisRepository.class);
	}

	private CommonProvider() {}
}
