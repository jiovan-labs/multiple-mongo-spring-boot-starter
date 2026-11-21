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

package com.jiovan.spring.starter.multiplemongo.config;


import java.util.Collections;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoProperties;
import org.springframework.boot.data.mongodb.autoconfigure.MongoReactiveDataConfigurationUtils;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import reactor.core.publisher.Flux;


/**
 * Tertiary configuration for reactive mongo
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = TertiaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri")
@ConditionalOnClass(value = {MongoClient.class, Flux.class})
@AutoConfigureAfter(value = TertiaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, DataMongoAutoConfiguration.class, MongoReactiveAutoConfiguration.class, DataMongoReactiveAutoConfiguration.class})
public class TertiaryMongoReactiveConfiguration {
    public static final String CLIENT_FACTORY = "tertiaryReactiveMongoClientFactory";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "tertiaryReactiveMongoClient";    // $NON-NLS-1$
    public static final String DATABASE_FACTORY = "tertiaryReactiveMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "tertiaryReactiveMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "tertiaryReactiveMongoGridFsTemplate";    // $NON-NLS-1$

    /**
     * Returns the tertiary reactive mongo client factory bean configured with the qualified settings builder customizer
     *
     * @param customizer the qualified tertiary mongo client settings builder customizer
     *
     * @return the tertiary reactive mongo client factory bean
     */
    @Bean(name = CLIENT_FACTORY)
    @ConditionalOnBean(name = TertiaryMongoAutoConfiguration.SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_FACTORY)
    public ReactiveMongoClientFactory mongoClientFactory(@Qualifier(value = TertiaryMongoAutoConfiguration.SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return new ReactiveMongoClientFactory(Collections.singletonList(customizer));
    }

    /**
     * Returns the tertiary reactive mongo client bean created by the client factory from the settings
     *
     * @param factory the qualified tertiary reactive mongo client factory
     * @param settings the qualified tertiary mongo client settings
     *
     * @return the tertiary reactive mongo client bean
     */
    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {CLIENT_FACTORY, TertiaryMongoAutoConfiguration.CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = CLIENT_FACTORY) @Nonnull ReactiveMongoClientFactory factory,
        @Qualifier(value = TertiaryMongoAutoConfiguration.CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    /**
     * Returns the tertiary reactive mongo database factory bean backed by the qualified mongo client and properties
     *
     * @param mongoClient the qualified tertiary reactive mongo client
     * @param properties the qualified tertiary mongo properties that supply the database name
     *
     * @return the tertiary reactive mongo database factory bean
     */
    @Bean(name = DATABASE_FACTORY)
    @ConditionalOnBean(name = {MONGO_CLIENT, TertiaryMongoAutoConfiguration.MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = DATABASE_FACTORY)
    public SimpleReactiveMongoDatabaseFactory mongoDatabaseFactory(@Qualifier(value = MONGO_CLIENT) @Nonnull MongoClient mongoClient,
        @Qualifier(value = TertiaryMongoAutoConfiguration.MONGO_PROPERTIES) @Nonnull MongoProperties properties) {
        return MongoReactiveDataConfigurationUtils.reactiveMongoDatabaseFactory(mongoClient, properties);
    }

    /**
     * Returns the tertiary reactive mongo template bean backed by the reactive database factory and converter
     *
     * @param details the qualified tertiary mongo connection details
     * @param factory the qualified tertiary reactive mongo database factory
     * @param converter the qualified tertiary mapping mongo converter
     *
     * @return the tertiary reactive mongo template bean
     */
    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, TertiaryMongoAutoConfiguration.MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public ReactiveMongoTemplate mongoTemplate(@Qualifier(value = TertiaryMongoAutoConfiguration.CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull ReactiveMongoDatabaseFactory factory,
        @Qualifier(value = TertiaryMongoAutoConfiguration.MAPPING_CONVERTER) @Nonnull MappingMongoConverter converter) {
        return MongoReactiveDataConfigurationUtils.reactiveMongoTemplate(details, factory, converter);
    }

    /**
     * Returns the tertiary reactive GridFsTemplate bean backed by the reactive database factory, converter and buffer factory
     *
     * @param properties the data mongo properties that supply the GridFS database and bucket names
     * @param details the qualified tertiary mongo connection details
     * @param databaseFactory the qualified tertiary reactive mongo database factory
     * @param converter the qualified tertiary mapping mongo converter
     * @param bufferFactory the qualified data buffer factory
     *
     * @return the tertiary reactive GridFS template bean
     */
    @Bean(name = GRID_FS_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, TertiaryMongoAutoConfiguration.MAPPING_CONVERTER, MongoReactivePreConfiguration.DATA_BUFFER_FACTORY, TertiaryMongoAutoConfiguration.MONGO_PROPERTIES, TertiaryMongoAutoConfiguration.DATA_MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = GRID_FS_TEMPLATE)
    public ReactiveGridFsTemplate mongoGridFsTemplate(@Qualifier(value = TertiaryMongoAutoConfiguration.DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties,
        @Qualifier(value = TertiaryMongoAutoConfiguration.CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull ReactiveMongoDatabaseFactory databaseFactory,
        @Qualifier(value = TertiaryMongoAutoConfiguration.MAPPING_CONVERTER) @Nonnull MappingMongoConverter converter,
        @Nonnull @Qualifier(value = MongoReactivePreConfiguration.DATA_BUFFER_FACTORY) DataBufferFactory bufferFactory) {
        return MongoReactiveDataConfigurationUtils.reactiveGridFsTemplate(properties, details, databaseFactory, converter, bufferFactory);
    }
}
