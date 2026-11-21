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


import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import reactor.core.publisher.Flux;


/**
 * Pre-Configuration for reactive mongo
 *
 * @author David Hsing
 *
 * @see org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = {MongoClient.class, Flux.class})
@AutoConfigureBefore(value = {MongoAutoConfiguration.class, DataMongoAutoConfiguration.class, MongoReactiveAutoConfiguration.class, DataMongoReactiveAutoConfiguration.class})
public class MongoReactivePreConfiguration {
    public static final String DATA_BUFFER_FACTORY = "mongoDataBufferFactory";    // $NON-NLS-1$

    /**
     * Returns the default data buffer factory bean used to allocate buffers for reactive mongo templates
     *
     * @return the default data buffer factory bean
     */
    @Primary
    @Bean(name = DATA_BUFFER_FACTORY)
    @ConditionalOnMissingBean(name = DATA_BUFFER_FACTORY)
    public DefaultDataBufferFactory dataBufferFactory() {
        return new DefaultDataBufferFactory();
    }
}
