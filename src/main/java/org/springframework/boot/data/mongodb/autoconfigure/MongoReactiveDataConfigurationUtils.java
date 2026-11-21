/*
 * Copyright (c) 2020 Jiovan Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.data.mongodb.autoconfigure;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.mongodb.autoconfigure.MongoConfigurationUtils;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import com.mongodb.reactivestreams.client.MongoClient;


/**
 * Utilities for configuring reactive mongo data
 *
 * @author David Hsing
 *
 * @see org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoReactiveDataConfigurationUtils {
    /**
     * Returns a reactive mongo database factory backed by the given client and configured from the properties
     *
     * @param client the reactive mongo client used to connect to the database
     * @param properties the mongo properties that supply the database name
     *
     * @return the configured reactive mongo database factory
     */
    @Nonnull
    public static SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(@Nonnull MongoClient client, @Nonnull MongoProperties properties) {
        return reactiveMongoDatabaseFactory(client, properties, null, null);
    }

    /**
     * Returns a reactive mongo database factory backed by the given client, properties and connection details
     *
     * @param client the reactive mongo client used to connect to the database
     * @param properties the mongo properties that supply the database name
     * @param details the mongo connection details, may be {@code null}
     * @param bundles the SSL bundles used to configure TLS connections, may be {@code null}
     *
     * @return the configured reactive mongo database factory
     */
    @Nonnull
    public static SimpleReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(@Nonnull MongoClient client, @Nonnull MongoProperties properties, @Nullable MongoConnectionDetails details, @Nullable SslBundles bundles) {
        MongoConnectionDetails alias = ObjectUtils.getIfNull(details, MongoConfigurationUtils.mongoConnectionDetails(properties, bundles));
        return new DataMongoReactiveAutoConfiguration().reactiveMongoDatabaseFactory(properties, client, alias);
    }

    /**
     * Returns a reactive mongo template backed by the given database factory and converter
     *
     * @param details the mongo connection details retained for API compatibility, ignored by Spring Boot 4
     * @param factory the reactive mongo database factory used to access the database
     * @param converter the mongo converter used to map documents to and from entities
     *
     * @return the configured reactive mongo template
     */
    @Nonnull
    public static ReactiveMongoTemplate reactiveMongoTemplate(@Nonnull MongoConnectionDetails details, @Nonnull ReactiveMongoDatabaseFactory factory, @Nonnull MongoConverter converter) {
        return new DataMongoReactiveAutoConfiguration().reactiveMongoTemplate(factory, converter);
    }

    /**
     * Returns a reactive GridFsTemplate backed by the given data mongo properties, database factory, converter and buffer factory
     *
     * @param properties the data mongo properties that supply the GridFS database and bucket names
     * @param details the mongo connection details retained for API compatibility, ignored by Spring Boot 4
     * @param databaseFactory the reactive mongo database factory used to access the database
     * @param converter the mapping mongo converter used to map GridFS documents
     * @param bufferFactory the data buffer factory used to allocate reactive data buffers
     *
     * @return the configured reactive GridFS template
     */
    @Nonnull
    public static ReactiveGridFsTemplate reactiveGridFsTemplate(@Nonnull DataMongoProperties properties, @Nonnull MongoConnectionDetails details, @Nonnull ReactiveMongoDatabaseFactory databaseFactory, @Nonnull MappingMongoConverter converter, @Nonnull DataBufferFactory bufferFactory) {
        return new DataMongoReactiveAutoConfiguration().reactiveGridFsTemplate(properties, databaseFactory, converter, bufferFactory);
    }
}
