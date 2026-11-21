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
import org.springframework.context.annotation.Primary;
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
 * Primary configuration for classic mongo
 *
 * @author David Hsing
 *
 * @see org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration
 * @see org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
 * @see org.springframework.data.mongodb.core.convert.MappingMongoConverter
 * @see org.springframework.data.mongodb.config.BeanNames
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBooleanProperty(prefix = "jiovan.multiple-mongo", name = "enabled", matchIfMissing = true)
@ConditionalOnAnyProperties(value = {
    @ConditionalOnProperty(prefix = PrimaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "uri"),
    @ConditionalOnProperty(prefix = PrimaryMongoAutoConfiguration.PROPERTIES_PREFIX, name = "host")
})
@ConditionalOnClass(value = MongoClient.class)
@AutoConfigureAfter(value = MongoReactivePreConfiguration.class)
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, DataMongoAutoConfiguration.class, MongoReactiveAutoConfiguration.class, DataMongoReactiveAutoConfiguration.class})
public class PrimaryMongoAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "jiovan.multiple-mongo.primary";    // $NON-NLS-1$
    public static final String MONGO_PROPERTIES = "primaryMongoProperties";    // $NON-NLS-1$
    public static final String DATA_MONGO_PROPERTIES = "primaryDataMongoProperties";    // $NON-NLS-1$
    public static final String CONNECTION_DETAILS = "primaryMongoConnectionDetails";    // $NON-NLS-1$
    public static final String SSL_BUNDLES = "primaryMongoSslBundles";    // $NON-NLS-1$
    public static final String SETTINGS_BUILDER_CUSTOMIZER = "primaryMongoClientSettingsBuilderCustomizer";    // $NON-NLS-1$
    public static final String CLIENT_FACTORY = "primaryMongoClientFactory";    // $NON-NLS-1$
    public static final String CLIENT_SETTINGS = "primaryMongoClientSettings";    // $NON-NLS-1$
    public static final String MONGO_CLIENT = "primaryMongoClient";    // $NON-NLS-1$
    public static final String DATABASE_FACTORY = "primaryMongoDatabaseFactory";    // $NON-NLS-1$
    public static final String TRANSACTION_MANAGER = "primaryMongoTransactionManager";    // $NON-NLS-1$
    public static final String TRANSACTION_OPTIONS = "primaryMongoTransactionOptions";    // $NON-NLS-1$
    public static final String CUSTOM_CONVERSIONS = "primaryMongoCustomConversions";    // $NON-NLS-1$
    public static final String MANAGED_TYPES = "primaryMongoManagedTypes";    // $NON-NLS-1$
    public static final String MAPPING_CONTEXT = "primaryMongoMappingContext";    // $NON-NLS-1$
    public static final String MAPPING_CONVERTER = "primaryMongoMappingConverter";    // $NON-NLS-1$
    public static final String MONGO_TEMPLATE = "primaryMongoTemplate";    // $NON-NLS-1$
    public static final String GRID_FS_TEMPLATE = "primaryMongoGridFsTemplate";    // $NON-NLS-1$

    /**
     * Returns the primary mongo properties bean bound from the {@code jiovan.multiple-mongo.primary} configuration prefix
     *
     * @return the primary mongo properties bean
     */
    @Primary
    @Bean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = MONGO_PROPERTIES)
    @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
    public MongoProperties mongoProperties() {
        return new ExtendedMongoProperties();
    }

    /**
     * Returns the primary data mongo properties bean bound from the {@code jiovan.multiple-mongo.primary} configuration prefix
     *
     * @return the primary data mongo properties bean
     */
    @Primary
    @Bean(name = DATA_MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = DATA_MONGO_PROPERTIES)
    @ConfigurationProperties(prefix = PROPERTIES_PREFIX)
    public DataMongoProperties dataMongoProperties() {
        return new DataMongoProperties();
    }

    /**
     * Returns the primary mongo connection details bean built from the qualified mongo properties and SSL bundles
     *
     * @param properties the qualified primary mongo properties that supply the connection URI and credentials
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the primary mongo connection details bean
     */
    @Primary
    @Bean(name = CONNECTION_DETAILS)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = CONNECTION_DETAILS)
    public MongoConnectionDetails mongoConnectionDetails(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties, @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoConfigurationUtils.mongoConnectionDetails(properties, bundles);
    }

    /**
     * Returns the primary mongo client settings builder customizer bean built from the mongo properties and connection details
     *
     * @param properties the qualified primary mongo properties that supply the client settings
     * @param details the qualified primary mongo connection details
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the primary mongo client settings builder customizer bean
     */
    @Primary
    @Bean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnBean(name = MONGO_PROPERTIES)
    @ConditionalOnMissingBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(@Qualifier(value = MONGO_PROPERTIES) @Nonnull MongoProperties properties,
        @Qualifier(value = CONNECTION_DETAILS) @Nonnull MongoConnectionDetails details,
        @Autowired(required = false) @Qualifier(value = SSL_BUNDLES) @Nonnull SslBundles bundles) {
        return MongoConfigurationUtils.mongoClientSettingsCustomizer(properties, details, bundles);
    }

    /**
     * Returns the primary mongo client factory bean configured with the qualified settings builder customizer
     *
     * @param customizer the qualified primary mongo client settings builder customizer
     *
     * @return the primary mongo client factory bean
     */
    @Primary
    @Bean(name = CLIENT_FACTORY)
    @ConditionalOnBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_FACTORY)
    public MongoClientFactory mongoClientFactory(@Qualifier(value = SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return new MongoClientFactory(Collections.singletonList(customizer));
    }

    /**
     * Returns the primary mongo client settings bean built by applying the qualified customizer
     *
     * @param customizer the qualified primary mongo client settings builder customizer
     *
     * @return the primary mongo client settings bean
     */
    @Primary
    @Bean(name = CLIENT_SETTINGS)
    @ConditionalOnBean(name = SETTINGS_BUILDER_CUSTOMIZER)
    @ConditionalOnMissingBean(name = CLIENT_SETTINGS)
    public MongoClientSettings mongoClientSettings(@Qualifier(value = SETTINGS_BUILDER_CUSTOMIZER) @Nonnull MongoClientSettingsBuilderCustomizer customizer) {
        return MongoConfigurationUtils.mongoClientSettings(customizer);
    }

    /**
     * Returns the primary mongo client bean created by the qualified client factory from the given settings
     *
     * @param factory the qualified primary mongo client factory
     * @param settings the qualified primary mongo client settings
     *
     * @return the primary mongo client bean
     */
    @Primary
    @Bean(name = MONGO_CLIENT, destroyMethod = "close")
    @ConditionalOnBean(name = {CLIENT_FACTORY, CLIENT_SETTINGS})
    @ConditionalOnMissingBean(name = MONGO_CLIENT)
    public MongoClient mongoClient(@Qualifier(value = CLIENT_FACTORY) @Nonnull MongoClientFactory factory,
        @Qualifier(value = CLIENT_SETTINGS) @Nonnull MongoClientSettings settings) {
        return factory.createMongoClient(settings);
    }

    /**
     * Returns the primary mongo database factory bean backed by the qualified mongo client and configured from the mongo properties
     *
     * @param client the qualified primary mongo client
     * @param properties the qualified primary mongo properties that supply the database name
     * @param details the qualified primary mongo connection details
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the primary mongo database factory bean
     */
    @Primary
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
     * Returns the primary mongo transaction manager bean backed by the qualified database factory and optional transaction options
     *
     * @param factory the qualified primary mongo database factory
     * @param options the qualified transaction options, may be {@code null}
     *
     * @return the primary mongo transaction manager bean
     */
    @Primary
    @Bean(name = TRANSACTION_MANAGER)
    @ConditionalOnBean(name = DATABASE_FACTORY)
    @ConditionalOnMissingBean(name = TRANSACTION_MANAGER)
    public MongoTransactionManager mongoTransactionManager(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Autowired(required = false) @Qualifier(value = TRANSACTION_OPTIONS) @Nullable TransactionOptions options) {
        return new MongoTransactionManager(factory, options);
    }

    /**
     * Returns the primary mongo custom conversions bean used to register converters with the mapping context
     *
     * @param properties the primary data mongo properties that supply the BigDecimal representation
     *
     * @return the primary mongo custom conversions bean
     */
    @Primary
    @Bean(name = CUSTOM_CONVERSIONS)
    @ConditionalOnMissingBean(name = CUSTOM_CONVERSIONS)
    public MongoCustomConversions mongoCustomConversions(@Qualifier(value = DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties) {
        return MongoDataConfigurationUtils.mongoCustomConversions(properties);
    }

    /**
     * Returns the primary mongo managed types bean discovered from the given application context
     *
     * @param context the application context used to discover managed types
     *
     * @return the primary mongo managed types bean
     *
     * @throws ClassNotFoundException if a discovered managed type cannot be loaded
     */
    @Primary
    @Bean(name = MANAGED_TYPES)
    @ConditionalOnMissingBean(name = MANAGED_TYPES)
    public MongoManagedTypes mongoManagedTypes(@Nonnull ApplicationContext context) throws ClassNotFoundException {
        return MongoDataConfigurationUtils.mongoManagedTypes(context);
    }

    /**
     * Returns the primary mongo mapping context bean configured with the qualified properties, conversions and managed types
     *
     * @param properties the qualified primary data mongo properties that control auto index creation and field naming strategy
     * @param conversions the qualified primary mongo custom conversions
     * @param types the qualified primary mongo managed types
     *
     * @return the primary mongo mapping context bean
     */
    @Primary
    @Bean(name = MAPPING_CONTEXT)
    @ConditionalOnBean(name = {CUSTOM_CONVERSIONS, DATA_MONGO_PROPERTIES})
    @ConditionalOnMissingBean(name = MAPPING_CONTEXT)
    public MongoMappingContext mongoMappingContext(@Qualifier(value = DATA_MONGO_PROPERTIES) @Nonnull DataMongoProperties properties,
        @Qualifier(value = CUSTOM_CONVERSIONS) @Nonnull MongoCustomConversions conversions,
        @Qualifier(value = MANAGED_TYPES) @Nonnull MongoManagedTypes types) {
        return MongoDataConfigurationUtils.mongoMappingContext(properties, conversions, types);
    }

    /**
     * Returns the primary mapping mongo converter bean backed by the qualified database factory and mapping context
     *
     * @param properties the qualified primary data mongo properties retained for configuration consistency
     * @param factory the qualified primary mongo database factory
     * @param context the qualified primary mongo mapping context
     * @param conversions the qualified primary mongo custom conversions
     *
     * @return the primary mapping mongo converter bean
     */
    @Primary
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
     * Returns the primary mongo template bean backed by the qualified database factory and converter
     *
     * @param factory the qualified primary mongo database factory
     * @param converter the qualified primary mapping mongo converter
     *
     * @return the primary mongo template bean
     */
    @Primary
    @Bean(name = MONGO_TEMPLATE)
    @ConditionalOnBean(name = {DATABASE_FACTORY, MAPPING_CONVERTER})
    @ConditionalOnMissingBean(name = MONGO_TEMPLATE)
    public MongoTemplate mongoTemplate(@Qualifier(value = DATABASE_FACTORY) @Nonnull MongoDatabaseFactory factory,
        @Qualifier(value = MAPPING_CONVERTER) @Nonnull MongoConverter converter) {
        return new MongoTemplate(factory, converter);
    }

    /**
     * Returns the primary GridFsTemplate bean configured from the qualified mongo properties, database factory and mongo template
     *
     * @param properties the qualified primary data mongo properties that supply the GridFS database and bucket names
     * @param factory the qualified primary mongo database factory
     * @param template the qualified primary mongo template
     * @param details the qualified primary mongo connection details
     * @param bundles the SSL bundles used to configure TLS connections
     *
     * @return the primary GridFS template bean
     */
    @Primary
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
