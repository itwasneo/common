package com.itwasneo.common.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import io.activej.inject.annotation.Inject;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MongoRepository<T> {

	private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);

	private final MongoCollection<Document> collection;

	private final ObjectMapper objectMapper;

	@Inject
	public MongoRepository(MongoCollection<Document> collection, ObjectMapper objectMapper) {
		this.collection = collection;
		this.objectMapper = objectMapper;
	}

	public Optional<T> findByObjectId(String id, Class<T> t) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		return Optional.ofNullable(collection.find(filter, t).first());
	}

	public Optional<BsonValue> save(T t) {
		try {
			InsertOneResult res = collection
					.insertOne(
							Document
									.parse(objectMapper.writeValueAsString(t)));
			return Optional.ofNullable(res.getInsertedId());
		} catch (JsonProcessingException jpe){
			logger.error("Error converting object:", jpe);
			return Optional.empty();
		}
	}
}
