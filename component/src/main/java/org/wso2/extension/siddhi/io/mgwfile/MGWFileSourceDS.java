/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
*/
package org.wso2.extension.siddhi.io.mgwfile;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.jndi.JNDIContextManager;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.exception.DataSourceException;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.NamingException;

@Component(
        name = "org.wso2.analaytics.apim.MGWFileSourceDS",
        immediate = true
)
public class MGWFileSourceDS {

    private static final Log log = LogFactory.getLog(MGWFileSourceDS.class);


    @Reference(
            name = "org.wso2.carbon.datasource.DataSourceService",
            service = DataSourceService.class,
            cardinality = ReferenceCardinality.AT_LEAST_ONE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unregisterDataSourceService"
    )
    protected void onDataSourceServiceReady(DataSourceService service) {
        Connection connection = null;
        try {
            HikariDataSource dsObject = (HikariDataSource) service.getDataSource("WSO2AM_STATS_DB");
            FileEventAdapterDBUtil.setDataSource(dsObject);
            //From connection do the required CRUD operation
        } catch (DataSourceException e) {
            log.error("error occurred while fetching the data source.", e);
        }  finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("error occurred while closing the connection.", e);
                }
            }
        }
    }

    protected void unregisterDataSourceService(DataSourceService dataSourceService) {
        log.info("Unregistering data sources sample");
    }

}
