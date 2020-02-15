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

package com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource;

import com.dangdang.ddframe.rdb.sharding.rule.MasterSlaveRule;
import com.dangdang.ddframe.rdb.sharding.constant.SQLType;
import com.dangdang.ddframe.rdb.sharding.hint.HintManagerHolder;
import com.dangdang.ddframe.rdb.sharding.jdbc.adapter.AbstractDataSourceAdapter;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.connection.MasterSlaveConnection;
import com.google.common.base.Preconditions;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Database that support master-slave.
 *
 * @author zhangliang
 */
@Getter
public class MasterSlaveDataSource extends AbstractDataSourceAdapter {
    
    private static final ThreadLocal<Boolean> DML_FLAG = new ThreadLocal<Boolean>() {
        
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    
    private final MasterSlaveRule masterSlaveRule;
    
    public MasterSlaveDataSource(final MasterSlaveRule masterSlaveRule) throws SQLException {
        super(getAllDataSources(masterSlaveRule.getMasterDataSource(), masterSlaveRule.getSlaveDataSourceMap().values()));
        this.masterSlaveRule = masterSlaveRule;
    }
    
    private static Collection<DataSource> getAllDataSources(final DataSource masterDataSource, final Collection<DataSource> slaveDataSources) {
        Collection<DataSource> result = new LinkedList<>(slaveDataSources);
        result.add(masterDataSource);
        return result;
    }
    
    /**
     * Get map of all actual data source name and all actual data sources.
     *
     * @return map of all actual data source name and all actual data sources
     */
    public Map<String, DataSource> getAllDataSources() {
        Map<String, DataSource> result = new HashMap<>(masterSlaveRule.getSlaveDataSourceMap().size() + 1, 1);
        result.put(masterSlaveRule.getMasterDataSourceName(), masterSlaveRule.getMasterDataSource());
        result.putAll(masterSlaveRule.getSlaveDataSourceMap());
        return result;
    }
    
    /**
     * reset DML flag.
     */
    public static void resetDMLFlag() {
        DML_FLAG.remove();
    }
    
    /**
     * Get data source from master-slave data source.
     *
     * @param sqlType SQL type
     * @return data source from master-slave data source
     */
    public NamedDataSource getDataSource(final SQLType sqlType) {
        if (isMasterRoute(sqlType)) {
            DML_FLAG.set(true);
            return new NamedDataSource(masterSlaveRule.getMasterDataSourceName(), masterSlaveRule.getMasterDataSource());
        }
        String selectedSourceName = masterSlaveRule.getStrategy().getDataSource(masterSlaveRule.getName(), 
                masterSlaveRule.getMasterDataSourceName(), new ArrayList<>(masterSlaveRule.getSlaveDataSourceMap().keySet()));
        DataSource selectedSource = selectedSourceName.equals(masterSlaveRule.getMasterDataSourceName())
                ? masterSlaveRule.getMasterDataSource() : masterSlaveRule.getSlaveDataSourceMap().get(selectedSourceName);
        Preconditions.checkNotNull(selectedSource, "");
        return new NamedDataSource(selectedSourceName, selectedSource);
    }
    
    private boolean isMasterRoute(final SQLType sqlType) {
        return SQLType.DQL != sqlType || DML_FLAG.get() || HintManagerHolder.isMasterRouteOnly();
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return new MasterSlaveConnection(this);
    }
}
