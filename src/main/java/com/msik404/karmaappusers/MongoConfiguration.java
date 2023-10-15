package com.msik404.karmaappusers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.msik404.karmaappusers.user.UserDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.lang.NonNull;

@Configuration
public class MongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(@NonNull final MongoClient client) {
        return new SimpleMongoClientDatabaseFactory(client, databaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate(@NonNull final MongoDatabaseFactory factory) {
        final MongoTemplate mongoTemplate = new MongoTemplate(factory);

        final MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate
                .getConverter().getMappingContext();

        final IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        IndexOperations indexOps = mongoTemplate.indexOps(UserDocument.class);
        resolver.resolveIndexFor(UserDocument.class).forEach(indexOps::ensureIndex);

        return mongoTemplate;
    }

}
