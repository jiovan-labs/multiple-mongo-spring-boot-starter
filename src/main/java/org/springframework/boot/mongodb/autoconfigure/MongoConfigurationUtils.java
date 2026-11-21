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

package org.springframework.boot.mongodb.autoconfigure;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.ssl.SslBundles;
import com.mongodb.MongoClientSettings;


/**
 * Utilities for configuring classic mongo
 *
 * @author David Hsing
 *
 * @see org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class MongoConfigurationUtils {
    /**
     * Returns mongo connection details built from the given properties and SSL bundles
     *
     * @param properties the mongo properties that supply the connection URI and credentials
     * @param bundles the SSL bundles used to configure TLS connections, may be {@code null}
     *
     * @return the mongo connection details
     */
    @Nonnull
    public static MongoConnectionDetails mongoConnectionDetails(@Nonnull MongoProperties properties, @Nullable SslBundles bundles) {
        return new PropertiesMongoConnectionDetails(properties, bundles);
    }

    /**
     * Returns mongo client settings built by applying the given customizer
     *
     * @param customizer the customizer applied to the settings builder, may be {@code null}
     *
     * @return the built mongo client settings
     */
    @Nonnull
    public static MongoClientSettings mongoClientSettings(@Nullable MongoClientSettingsBuilderCustomizer customizer) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        if (customizer != null) {
            customizer.customize(builder);
        }
        return builder.build();
    }

    /**
     * Returns a mongo client settings customizer configured from the given properties and connection details
     *
     * @param properties the mongo properties that supply the client settings
     * @param details the mongo connection details, may be {@code null}
     * @param bundles the SSL bundles used to configure TLS connections, may be {@code null}
     *
     * @return the mongo client settings builder customizer
     */
    @Nonnull
    public static MongoClientSettingsBuilderCustomizer mongoClientSettingsCustomizer(@Nonnull MongoProperties properties, @Nullable MongoConnectionDetails details, @Nullable SslBundles bundles) {
        MongoConnectionDetails alias = ObjectUtils.getIfNull(details, mongoConnectionDetails(properties, bundles));
        return new MongoAutoConfiguration.MongoClientSettingsConfiguration().standardMongoSettingsCustomizer(properties, alias);
    }
}
