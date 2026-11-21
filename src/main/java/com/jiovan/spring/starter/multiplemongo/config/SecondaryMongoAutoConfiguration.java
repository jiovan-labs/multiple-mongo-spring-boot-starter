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
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoProperties;
import org.springframework.boot.data.mongodb.autoconfigure.MongoDataConfigurationUtils;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoClientFactory;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.mongodb.autoconfigure.MongoConfigurationUtils;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.MongoClientSettings;
import com.mongodb.TransactionOptions;
import com.mongodb.client.MongoClient;
import com.jiovan.plexus.spring.condition.annotation.ConditionalOnAnyProperties;
import com.jiovan.spring.starter.multiplemongo.property.ExtendedMongoProperties;


/**
 * Secondary configuration for classic mongo
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBooleanProperty(prefix = "jiovan.multiple-mongo", name = "enabled", matchIfMissing = true)
@ConditionalOnAnyProperties(value = {
    @ConditionalOnProperty(prefix = SecondaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri"),
    @ConditionalOnProperty(prefix = SecondaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "host")
})
@ConditionalOnClass(value = MongoClient.class)
@AutoConfigureAfter(value = PrimaryMongoAutoConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, DataMongoAutoConfiguration.class, MongoReactiveAutoConfiguration.class, DataMongoReactiveAutoConfiguration.class})
public class SecondaryMongoAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "jiovan.multiple-mongo.secondary";    // $NON-NLS-1$
    public static final String MONGO_PROPERTIES = "secondaryMongoProperties";    // $NON-NLS-1$
    public static final String DATA_MONGO_PROPERTIES = "secondaryDataMongoProperties";    // $NON-NLS-1$
    public static final String CONNECTION_DETAILS = "secondaryMongoConnectionDetails";    // $NON-NLS-1$
    public static final String SSL_BUNDLES = "secondaryMongoSslBundles";    // $NON-NLS-1$
    public static final String SETTINGS_BUILDER_CUSTOMIZER = "secondaryMongoClientSettingsBuilderCustomizer";    // $NON-NLS-1$
    public static final String CLIENT_FACTORY = "secondaryMongoClientFactory";    // $NON-NLS-1$
    public static final String CLIENT_SETTINGS = "secondaryMongoClientSettings";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "secondaryMongoClient";    // $NON-NLS-1$
    public static final String DATABASE_FACTORY = "secondaryMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String TRANSACTION_MANAGER = "secondaryMongoTransactionManager";    // $NON-NLS-1$
    public static final String TRANSACTION_OPTIONS = "secondaryMongoTransactionOptions";    // $NON-NLS-1$
    public static final String CUSTOM_CONVERSIONS = "secondaryMongoCustomConversions";    // $NON-NLS-1$
    public static final String MANAGED_TYPES = "secondaryMongoManagedTypes";    // $NON-NLS-1$
    public static final String MAPPING_CONTEXT = "secondaryMongoMappingContext";    // $NON-NLS-1$
    public static final String MAPPING_CONVERTER = "secondaryMongoMappingConverter";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "secondaryMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "secondaryMongoGridFsTemplate";    // $NON-NLS-1$

    /**
     * Returns the secondary mongo properties bean bound from the {@code jiovan.multiple-mongo.secondary} configuration prefix
     *
     * @return the secondary mongo properties bean
     */
    @Bean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = MONGO_PROPERTIES)
    @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
    public MongoProperties mongoProperties() {
        return new ExtendedMongoProperties();
    }

    /**
     * Returns the secondary data mongo properties bean bound from the {@code jiovan.multiple-mongo.secondary} configuration prefix
     *
     * @return the secondary data mongo properties bean
     */
    @Bean(name = DATA_MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = DATA_MONGO_PROPERTIES)
    @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
    public DataMongoProperties dataMongoProperties() {
        return new DataMongoProperties();
    }

    /**
     * Returns the secondary mongo connection details bean built from the qualified mongo properties and SSL bundles
     *
     * @param properties the qualified secondary mongo properties that supply the connection URI and credentials
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the secondary mongo connection details bean
     */
    @Bean(name = CONNECTION_DETAILS)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = CONNECTION_DETAILS)
    public MongoConnectionDetails mongoConnectionDetails(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties, @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoConfigurationUtils.mongoConnectionDetails(properties, bundles);
    }

    /**
     * Returns the secondary mongo client settings builder customizer bean built from the mongo properties and connection details
     *
     * @param properties the qualified secondary mongo properties that supply the client settings
     * @param details the qualified secondary mongo connection details
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the secondary mongo client settings builder customizer bean
     */
    @Bean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoConfigurationUtils.mongoClientSettingsCustomizer(properties, details, bundles);
    }

    /**
     * Returns the secondary mongo client factory bean configured with the qualified settings builder customizer
     *
     * @param customizer the qualified secondary mongo client settings builder customizer
     *
     * @return the secondary mongo client factory bean
     */
    @Bean(name = CLIENT_FACTORY)
    @ConditionalOnBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_FACTORY)
    public MongoClientFactory mongoClientFactory(@Qualifier(value = SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return new MongoClientFactory(Collections.singletonList(customizer));
    }

    /**
     * Returns the secondary mongo client settings bean built by applying the qualified customizer
     *
     * @param customizer the qualified secondary mongo client settings builder customizer
     *
     * @return the secondary mongo client settings bean
     */
    @Bean(name = CLIENT_SETTINGS)
    @ConditionalOnBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_SETTINGS)
    public MongoClientSettings mongoClientSettings(@Qualifier(value = SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return MongoConfigurationUtils.mongoClientSettings(customizer);
    }

    /**
     * Returns the secondary mongo client bean created by the qualified client factory from the given settings
     *
     * @param factory the qualified secondary mongo client factory
     * @param settings the qualified secondary mongo client settings
     *
     * @return the secondary mongo client bean
     */
    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {CLIENT_FACTORY, CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = CLIENT_FACTORY) @Nonnull MongoClientFactory factory,
        @Qualifier(value = CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    /**
     * Returns the secondary mongo database factory bean backed by the qualified mongo client and configured from the mongo properties
     *
     * @param client the qualified secondary mongo client
     * @param properties the qualified secondary mongo properties that supply the database name
     * @param details the qualified secondary mongo connection details
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the secondary mongo database factory bean
     */
    @Bean(name = DATABASE_FACTORY)
    @ConditionalOnBean(name = {MONGO_CLIENT, MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = DATABASE_FACTORY)
    public MongoDatabaseFactorySupport<?> mongoDatabaseFactory(@Qualifier(value = MONGO_CLIENT) @Nonnull MongoClient client,
        @Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoDataConfigurationUtils.mongoDatabaseFactory(client, properties, details, bundles);
    }

    /**
     * Returns the secondary mongo transaction manager bean backed by the qualified database factory and optional transaction options
     *
     * @param factory the qualified secondary mongo database factory
     * @param options the qualified transaction options, may be {@code null}
     *
     * @return the secondary mongo transaction manager bean
     */
    @Bean(name = TRANSACTION_MANAGER)
    @ConditionalOnBean(name = DATABASE_FACTORY)
    @ConditionalOnMissingBean(name = TRANSACTION_MANAGER)
    public MongoTransactionManager mongoTransactionManager(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Autowired(required = false) @Qualifier(value = TRANSACTION_OPTIONS) @Nullable TransactionOptions options) {
        return new MongoTransactionManager(factory, options);
    }

    /**
     * Returns the secondary mongo custom conversions bean used to register converters with the mapping context
     *
     * @param properties the secondary data mongo properties that supply the BigDecimal representation
     *
     * @return the secondary mongo custom conversions bean
     */
    @Bean(name = CUSTOM_CONVERSIONS)
    @ConditionalOnMissingBean(name = CUSTOM_CONVERSIONS)
    public MongoCustomConversions mongoCustomConversions(@Qualifier(value = DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties) {
        return MongoDataConfigurationUtils.mongoCustomConversions(properties);
    }

    /**
     * Returns the secondary mongo managed types bean discovered from the given application context
     *
     * @param context the application context used to discover managed types
     *
     * @return the secondary mongo managed types bean
     *
     * @throws ClassNotFoundException if a discovered managed type cannot be loaded
     */
    @Bean(name = MANAGED_TYPES)
    @ConditionalOnMissingBean(name = MANAGED_TYPES)
    public MongoManagedTypes mongoManagedTypes(@Nonnull ApplicationContext context) throws ClassNotFoundException {
        return MongoDataConfigurationUtils.mongoManagedTypes(context);
    }

    /**
     * Returns the secondary mongo mapping context bean configured with the qualified properties, conversions and managed types
     *
     * @param properties the qualified secondary data mongo properties that control auto index creation and field naming strategy
     * @param conversions the qualified secondary mongo custom conversions
     * @param types the qualified secondary mongo managed types
     *
     * @return the secondary mongo mapping context bean
     */
    @Bean(name = MAPPING_CONTEXT)
    @ConditionalOnBean(name = {CUSTOM_CONVERSIONS, DATA_MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MAPPING_CONTEXT)
    public MongoMappingContext mongoMappingContext(@Qualifier(value = DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties,
        @Qualifier(value = CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions,
        @Qualifier(value = MANAGED_TYPES) @Nonnull MongoManagedTypes types) {
        return MongoDataConfigurationUtils.mongoMappingContext(properties, conversions, types);
    }

    /**
     * Returns the secondary mapping mongo converter bean backed by the qualified database factory and mapping context
     *
     * @param properties the qualified secondary data mongo properties retained for configuration consistency
     * @param factory the qualified secondary mongo database factory
     * @param context the qualified secondary mongo mapping context
     * @param conversions the qualified secondary mongo custom conversions
     *
     * @return the secondary mapping mongo converter bean
     */
    @Bean(name = MAPPING_CONVERTER)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MAPPING_CONTEXT, CUSTOM_CONVERSIONS, DATA_MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MAPPING_CONVERTER)
    public MappingMongoConverter mongoMappingConverter(@Qualifier(value = DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MAPPING_CONTEXT) @Nonnull MongoMappingContext context,
        @Qualifier(value = CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions) {
        return MongoDataConfigurationUtils.mappingMongoConverter(properties, factory, context, conversions);
    }

    /**
     * Returns the secondary mongo template bean backed by the qualified database factory and converter
     *
     * @param factory the qualified secondary mongo database factory
     * @param converter the qualified secondary mapping mongo converter
     *
     * @return the secondary mongo template bean
     */
    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public MongoTemplate mongoTemplate(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MAPPING_CONVERTER) @Nonnull MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    /**
     * Returns the secondary GridFsTemplate bean configured from the qualified mongo properties, database factory and mongo template
     *
     * @param properties the qualified secondary data mongo properties that supply the GridFS database and bucket names
     * @param factory the qualified secondary mongo database factory
     * @param template the qualified secondary mongo template
     * @param details the qualified secondary mongo connection details
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the secondary GridFS template bean
     */
    @Bean(name = GRID_FS_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MONGO_TEMPLATE, DATA_MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = GRID_FS_TEMPLATE)
    public GridFsTemplate mongoGridFsTemplate(@Qualifier(value = DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties,
        @Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MONGO_TEMPLATE) @Nonnull MongoTemplate template,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoDataConfigurationUtils.gridFsTemplate(properties, factory, template, details, bundles);
    }
}
