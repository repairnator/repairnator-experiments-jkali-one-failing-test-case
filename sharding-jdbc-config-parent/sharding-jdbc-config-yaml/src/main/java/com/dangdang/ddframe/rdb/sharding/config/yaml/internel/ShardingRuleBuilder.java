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

package com.dangdang.ddframe.rdb.sharding.config.yaml.internel;

import com.dangdang.ddframe.rdb.sharding.api.config.ShardingRuleConfig;
import com.dangdang.ddframe.rdb.sharding.rule.ShardingRule;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Sharding rule builder from yaml.
 *
 * @author zhangliang
 */
@RequiredArgsConstructor
public final class ShardingRuleBuilder {
    
    private final Map<String, DataSource> dataSourceMap;
    
    private final YamlConfig yamlConfig;
    
    /**
     * Build sharding rule from yaml.
     * 
     * @return sharding rule from yaml
     */
    public ShardingRule build() {
        ShardingRuleConfig result = new ShardingRuleConfig();
        result.setDefaultDataSourceName(yamlConfig.getDefaultDataSourceName());
        for (Entry<String, YamlTableRuleConfig> entry : yamlConfig.getTables().entrySet()) {
            YamlTableRuleConfig tableRuleConfig = entry.getValue();
            tableRuleConfig.setLogicTable(entry.getKey());
            result.getTableRuleConfigs().add(tableRuleConfig.getTableRuleConfig());
        }
        result.getBindingTableGroups().addAll(yamlConfig.getBindingTables());
        if (null != yamlConfig.getDefaultDatabaseStrategy()) {
            result.setDefaultDatabaseShardingStrategyConfig(yamlConfig.getDefaultDatabaseStrategy().getShardingStrategy());
        }
        if (null != yamlConfig.getDefaultTableStrategy()) {
            result.setDefaultTableShardingStrategyConfig(yamlConfig.getDefaultTableStrategy().getShardingStrategy());
        }
        result.setDefaultKeyGeneratorClass(yamlConfig.getDefaultKeyGeneratorClass());
        return result.build(dataSourceMap.isEmpty() ? yamlConfig.getDataSources() : dataSourceMap);
    }
}
