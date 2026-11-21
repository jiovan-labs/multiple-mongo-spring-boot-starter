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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.mongodb.autoconfigure.MongoConfigurationUtils;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.client.MongoClient;
import com.jiovan.plexus.spring.toolkit.support.SingletonObjectProvider;


/**
 * Utilities for configuring classic mongo data
 *
 * @author David Hsing
 *
 * @see DataMongoConfiguration
 * @see MongoDatabaseFactoryConfiguration
 * @see MongoDatabaseFactoryDependentConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoDataConfigurationUtils {
    /**
     * Returns the mongo custom conversions configured with the given data mongo properties
     *
     * @param properties the data mongo properties that supply the BigDecimal representation
     *
     * @return the mongo custom conversions used to register converters with the mapping context
     */
    @Nonnull
    public static MongoCustomConversions mongoCustomConversions(@Nonnull DataMongoProperties properties) {
        return new DataMongoConfiguration(properties).mongoCustomConversions();
    }

    /**
     * Returns the mongo managed types discovered from the given application context
     *
     * @param context the application context used to discover managed types
     *
     * @return the discovered mongo managed types
     *
     * @throws ClassNotFoundException if a discovered managed type cannot be loaded
     */
    @Nonnull
    public static MongoManagedTypes mongoManagedTypes(@Nonnull ApplicationContext context) throws ClassNotFoundException {
        return DataMongoConfiguration.mongoManagedTypes(context);
    }

    /**
     * Returns a mongo mapping context configured with the given data mongo properties, conversions and managed types
     *
     * @param properties the data mongo properties that control auto index creation and field naming strategy
     * @param conversions the custom conversions registered with the mapping context
     * @param types the managed types used to initialize the mapping context
     *
     * @return the configured mongo mapping context
     */
    @Nonnull
    public static MongoMappingContext mongoMappingContext(@Nonnull DataMongoProperties properties, @Nonnull MongoCustomConversions conversions, @Nonnull MongoManagedTypes types) {
        return new DataMongoConfiguration(properties).mongoMappingContext(conversions, types);
    }

    /**
     * Returns a mongo database factory backed by the given client and configured from the properties and connection details
     *
     * @param client the mongo client used to connect to the database
     * @param properties the mongo properties that supply the database name
     * @param details the mongo connection details, may be {@code null}
     * @param bundles the SSL bundles used to configure TLS connections, may be {@code null}
     *
     * @return the configured mongo database factory
     */
    @Nonnull
    public static MongoDatabaseFactorySupport<?> mongoDatabaseFactory(@Nonnull MongoClient client, @Nonnull MongoProperties properties, @Nullable MongoConnectionDetails details, @Nullable SslBundles bundles) {
        MongoConnectionDetails alias = ObjectUtils.getIfNull(details, MongoConfigurationUtils.mongoConnectionDetails(properties, bundles));
        return new MongoDatabaseFactoryConfiguration().mongoDatabaseFactory(client, properties, alias);
    }

    /**
     * Returns a mapping mongo converter backed by the given data mongo properties, database factory and mapping context
     *
     * @param properties the data mongo properties retained for configuration consistency
     * @param factory the mongo database factory used to access the database
     * @param context the mongo mapping context that supplies the entity mappings
     * @param conversions the custom conversions registered with the converter
     *
     * @return the configured mapping mongo converter
     */
    @Nonnull
    public static MappingMongoConverter mappingMongoConverter(@Nonnull DataMongoProperties properties, @Nonnull MongoDatabaseFactory factory, @Nonnull MongoMappingContext context, @Nonnull MongoCustomConversions conversions) {
        ObjectProvider<MongoDatabaseFactory> provider = SingletonObjectProvider.of(factory);
        return new DataMongoConfiguration(properties).mappingMongoConverter(provider, context, conversions);
    }

    /**
     * Returns a GridFsTemplate configured from the given data mongo properties, database factory and mongo template
     *
     * @param properties the data mongo properties that supply the GridFS database and bucket names
     * @param factory the mongo database factory used to access the database
     * @param template the mongo template used to configure the GridFS template
     * @param details the mongo connection details retained for API compatibility, ignored by Spring Boot 4
     * @param bundles the SSL bundles retained for API compatibility, ignored by Spring Boot 4
     *
     * @return the configured GridFS template
     */
    @Nonnull
    public static GridFsTemplate gridFsTemplate(@Nonnull DataMongoProperties properties, @Nonnull MongoDatabaseFactory factory, @Nonnull MongoTemplate template, @Nullable MongoConnectionDetails details, @Nullable SslBundles bundles) {
        return new MongoDatabaseFactoryDependentConfiguration().gridFsTemplate(properties, factory, template);
    }
}
