/*
 * Copyright 2016 Santanu Sinha <santanu.sinha@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.dropwizard.sharding;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.sharding.admin.BlacklistShardTask;
import io.dropwizard.sharding.admin.UnblacklistShardTask;
import io.dropwizard.sharding.caching.LookupCache;
import io.dropwizard.sharding.caching.RelationalCache;
import io.dropwizard.sharding.config.ShardedHibernateFactory;
import io.dropwizard.sharding.dao.*;
import io.dropwizard.sharding.sharding.BucketIdExtractor;
import io.dropwizard.sharding.sharding.InMemoryLocalShardBlacklistingStore;
import io.dropwizard.sharding.sharding.ShardBlacklistingStore;
import io.dropwizard.sharding.sharding.ShardManager;
import io.dropwizard.sharding.sharding.impl.ConsistentHashBucketIdExtractor;
import io.dropwizard.sharding.utils.ShardCalculator;
import lombok.Getter;
import lombok.val;
import org.hibernate.SessionFactory;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A dropwizard bundle that provides sharding over normal RDBMS.
 */
public abstract class DBShardingBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private static final String DEFAULT_NAMESPACE = "default";
    private static final String SHARD_ENV = "db.shards";
    private static final String DEFAULT_SHARDS = "2";

    private List<HibernateBundle<T>> shardBundles = Lists.newArrayList();
    @Getter
    private List<SessionFactory> sessionFactories;
    @Getter
    private ShardManager shardManager;
    @Getter
    private String dbNamespace;

    public DBShardingBundle(String dbNamespace, Class<?> entity, Class<?>... entities) {
        this.dbNamespace = dbNamespace;

        val inEntities = ImmutableList.<Class<?>>builder().add(entity).add(entities).build();
        init(inEntities);
    }

    public DBShardingBundle(String dbNamespace, String classPathPrefix) {
        this.dbNamespace = dbNamespace;

        Set<Class<?>> entities = new Reflections(classPathPrefix).getTypesAnnotatedWith(Entity.class);
        Preconditions.checkArgument(!entities.isEmpty(), String.format("No entity class found at %s", classPathPrefix));
        val inEntities = ImmutableList.<Class<?>>builder().addAll(entities).build();
        init(inEntities);
    }

    public DBShardingBundle(Class<?> entity, Class<?>... entities) {
        this(DEFAULT_NAMESPACE, entity, entities);
    }

    public DBShardingBundle(String classPathPrefix) {
        this(DEFAULT_NAMESPACE, classPathPrefix);
    }

    private void init(final ImmutableList<Class<?>> inEntities) {
        String numShardsEnv = System.getProperty(String.join(".", dbNamespace, DEFAULT_NAMESPACE),
                System.getProperty(SHARD_ENV, DEFAULT_SHARDS));

        int numShards = Integer.parseInt(numShardsEnv);
        shardManager = new ShardManager(numShards, getBlacklistingStore());
        for (int i = 0; i < numShards; i++) {
            final int finalI = i;
            shardBundles.add(new HibernateBundle<T>(inEntities, new SessionFactoryFactory()) {
                @Override
                protected String name() {
                    return String.format("connectionpool-%s-%d", dbNamespace, finalI);
                }

                @Override
                public PooledDataSourceFactory getDataSourceFactory(T t) {
                    return getConfig(t).getShards().get(finalI);
                }
            });
        }
    }

    @Override
    public void run(T configuration, Environment environment) {
        sessionFactories = shardBundles.stream().map(HibernateBundle::getSessionFactory).collect(Collectors.toList());
        environment.admin().addTask(new BlacklistShardTask(shardManager));
        environment.admin().addTask(new UnblacklistShardTask(shardManager));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Bootstrap<?> bootstrap) {
        //shardBundles.forEach(shardBundle -> bootstrap.addBundle((ConfiguredBundle)shardBundle));
        shardBundles.forEach(hibernateBundle -> bootstrap.addBundle((ConfiguredBundle) hibernateBundle));
    }

    @VisibleForTesting
    public void runBundles(T configuration, Environment environment) {
        shardBundles.forEach(hibernateBundle -> {
            try {
                hibernateBundle.run(configuration, environment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @VisibleForTesting
    public void initBundles(Bootstrap bootstrap) {
        shardBundles.forEach(hibernameBundle -> initialize(bootstrap));
    }

    /**
     * Child classes can override this to provide custom implementation.
     * @return
     */
    public ShardBlacklistingStore getBlacklistingStore() {
        return new InMemoryLocalShardBlacklistingStore();
    }

    protected abstract ShardedHibernateFactory getConfig(T config);

    public static <EntityType, T extends Configuration>
    LookupDao<EntityType> createParentObjectDao(DBShardingBundle<T> bundle, Class<EntityType> clazz) {
        return new LookupDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, new ConsistentHashBucketIdExtractor<>()));
    }

    public static <EntityType, T extends Configuration>
    CacheableLookupDao<EntityType> createParentObjectDao(DBShardingBundle<T> bundle, Class<EntityType> clazz, LookupCache<EntityType> cacheManager) {
        return new CacheableLookupDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, new ConsistentHashBucketIdExtractor<>()), cacheManager);
    }

    public static <EntityType, T extends Configuration>
    LookupDao<EntityType> createParentObjectDao(DBShardingBundle<T> bundle,
                                                Class<EntityType> clazz,
                                                BucketIdExtractor<String> bucketIdExtractor) {
        return new LookupDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, bucketIdExtractor));
    }

    public static <EntityType, T extends Configuration>
    CacheableLookupDao<EntityType> createParentObjectDao(DBShardingBundle<T> bundle, Class<EntityType> clazz, BucketIdExtractor<String> bucketIdExtractor, LookupCache<EntityType> cacheManager) {
        return new CacheableLookupDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, bucketIdExtractor), cacheManager);
    }


    public static <EntityType, T extends Configuration>
    RelationalDao<EntityType> createRelatedObjectDao(DBShardingBundle<T> bundle, Class<EntityType> clazz) {
        return new RelationalDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, new ConsistentHashBucketIdExtractor<>()));
    }


    public static <EntityType, T extends Configuration>
    CacheableRelationalDao<EntityType> createRelatedObjectDao(DBShardingBundle<T> bundle, Class<EntityType> clazz, RelationalCache<EntityType> cacheManager) {
        return new CacheableRelationalDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, new ConsistentHashBucketIdExtractor<>()), cacheManager);
    }


    public static <EntityType, T extends Configuration>
    RelationalDao<EntityType> createRelatedObjectDao(DBShardingBundle<T> bundle,
                                                     Class<EntityType> clazz,
                                                     BucketIdExtractor<String> bucketIdExtractor) {
        return new RelationalDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, bucketIdExtractor));
    }

    public static <EntityType, T extends Configuration>
    CacheableRelationalDao<EntityType> createRelatedObjectDao(DBShardingBundle<T> bundle, Class<EntityType> clazz, BucketIdExtractor<String> bucketIdExtractor, RelationalCache<EntityType> cacheManager) {
        return new CacheableRelationalDao<>(bundle.sessionFactories, clazz, new ShardCalculator<>(bundle.shardManager, bucketIdExtractor), cacheManager);
    }


    public static <EntityType, DaoType extends AbstractDAO<EntityType>, T extends Configuration>
    WrapperDao<EntityType, DaoType> createWrapperDao(DBShardingBundle<T> bundle, Class<DaoType> daoTypeClass) {
        return new WrapperDao<>(bundle.sessionFactories, daoTypeClass, new ShardCalculator<>(bundle.shardManager, new ConsistentHashBucketIdExtractor<>()));
    }

    public static <EntityType, DaoType extends AbstractDAO<EntityType>, T extends Configuration>
    WrapperDao<EntityType, DaoType> createWrapperDao(DBShardingBundle<T> bundle,
                                                     Class<DaoType> daoTypeClass,
                                                     BucketIdExtractor<String> bucketIdExtractor) {
        return new WrapperDao<>(bundle.sessionFactories, daoTypeClass, new ShardCalculator<>(bundle.shardManager, bucketIdExtractor));
    }

    public static <EntityType, DaoType extends AbstractDAO<EntityType>, T extends Configuration>
    WrapperDao<EntityType, DaoType> createWrapperDao(DBShardingBundle<T> bundle, Class<DaoType> daoTypeClass,
                                                     Class[] extraConstructorParamClasses, Class[] extraConstructorParamObjects) {
        return new WrapperDao<>(bundle.sessionFactories, daoTypeClass,
                                extraConstructorParamClasses, extraConstructorParamObjects,
                                new ShardCalculator<>(bundle.shardManager, new ConsistentHashBucketIdExtractor<>()));
    }
}
