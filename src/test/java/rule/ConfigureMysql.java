/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rule;

import com.atomicleopard.thundr.jdbc.MySqlModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import org.junit.rules.ExternalResource;

/**
 * Created by kaushiksen on 18/08/2015.
 */
public class ConfigureMysql extends ExternalResource {
    public static final String MYSQL_JDBC_URL = "jdbc:mysql://localhost:3306/app?user=root&password=";
    protected UpdatableInjectionContext injectionContext;
    protected MySqlModule mySqlModule = new MySqlModule();

    public ConfigureMysql(UpdatableInjectionContext injectionContext) {
        this.injectionContext = injectionContext;
    }

    @Override
    protected void before() throws Throwable {
        String jdbcUrl = MYSQL_JDBC_URL;
        injectionContext.inject(jdbcUrl).as(String.class);
        mySqlModule.initialise(injectionContext);
    }


    @Override
    protected void after() {
        mySqlModule.stop(injectionContext);
    }
}
