package com.itwasneo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itwasneo.common.repository.MongoRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.activej.config.Config;
import io.activej.inject.Injector;
import io.activej.inject.Key;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.ModuleBuilder;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.activej.types.Types.parameterizedType;

public class MongoProvider {

	private static final Logger logger = LoggerFactory.getLogger(MongoProvider.class);
	private record RepositoryConfiguration(String databaseName, String collectionName, MongoClient c) {

		@Provides
		MongoDatabase mongoDatabase() {
			return c.getDatabase(databaseName);
		}

		@Provides
		MongoCollection<Document> mongoCollection(MongoDatabase db) {
			return db.getCollection(collectionName);
		}

	}

	private static final Injector clientInjector = Injector.of(ModuleBuilder.create()
			// Config
			.bind(Config.class).to(() -> Config.ofClassPathProperties("application.properties"))

			// MongoClient
			.bind(MongoClient.class).to(c -> {
				c = c.getChild("mongo");
				String uri = String.format("mongodb://%s:%s@%s:%s/%s",
						c.get("user"),
						c.get("password"),
						c.get("host"),
						c.get("port"),
						c.get("db")
				);
				return MongoClients.create(uri);
			}, Config.class)
			.build());

	/**
	 * Gets MongoRepository instance.
	 *
	 * @return RedisRepository
	 */
	public static <T> MongoRepository<T> getMongoRepository(String databaseName, String collectionName) {
		MongoClient c = clientInjector.getInstance(MongoClient.class);
		Injector injector = Injector.of(ModuleBuilder.create()
				.scan(new RepositoryConfiguration(databaseName, collectionName, c))
				.bind(ObjectMapper.class).to(() -> new ObjectMapper().registerModule(new JavaTimeModule()))
				.build());
		Key<MongoCollection<Document>> key = Key.ofType(parameterizedType(MongoCollection.class, Document.class));
		return new MongoRepository<>(injector.getInstance(key), injector.getInstance(ObjectMapper.class));
	}

	private MongoProvider() {}
}