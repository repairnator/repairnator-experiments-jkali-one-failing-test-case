/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.api.config.strategy;

import com.dangdang.ddframe.rdb.sharding.routing.strategy.ShardingAlgorithmFactory;
import com.dangdang.ddframe.rdb.sharding.routing.strategy.ShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.routing.strategy.hint.HintShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.routing.strategy.hint.HintShardingStrategy;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

/**
 * Hint sharding strategy configuration.
 * 
 * @author zhangliang
 */
@Getter
@Setter
public class HintShardingStrategyConfig implements ShardingStrategyConfig {
    
    private String algorithmClassName;
    
    @Override
    public ShardingStrategy build() {
        Preconditions.checkNotNull(algorithmClassName, "Algorithm class cannot be null.");
        return new HintShardingStrategy(ShardingAlgorithmFactory.newInstance(algorithmClassName, HintShardingAlgorithm.class));
    }
}
