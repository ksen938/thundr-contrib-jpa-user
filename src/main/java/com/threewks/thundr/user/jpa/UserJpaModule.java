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
package com.threewks.thundr.user.jpa;

import com.atomicleopard.thundr.flyway.FlywayModule;
import com.atomicleopard.thundr.jdbc.HikariModule;
import com.atomicleopard.thundr.jdbc.MySqlModule;
import com.threewks.thundr.bind.BinderRegistry;
import com.threewks.thundr.injection.BaseModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.jpa.Jpa;
import com.threewks.thundr.jpa.hibernate.HibernateConfig;
import com.threewks.thundr.jpa.hibernate.HibernateModule;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.user.ThundrUserService;
import com.threewks.thundr.user.UserModule;
import com.threewks.thundr.user.UserRepository;
import com.threewks.thundr.user.UserTokenRepository;
import com.threewks.thundr.user.bind.UserBinder;
import com.threewks.thundr.user.jpa.authentication.PasswordAuthentication;
import org.hibernate.cfg.Environment;

import javax.sql.DataSource;


public class UserJpaModule extends BaseModule {
	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		super.requires(dependencyRegistry);
		dependencyRegistry.addDependency(MySqlModule.class);
		dependencyRegistry.addDependency(HikariModule.class);
		dependencyRegistry.addDependency(FlywayModule.class);
		dependencyRegistry.addDependency(HibernateModule.class);
		dependencyRegistry.addDependency(UserModule.class);
	}

	@Override
	public void initialise(UpdatableInjectionContext injectionContext) {
		super.initialise(injectionContext);
		initialiseHibernate(injectionContext);
	}

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		super.configure(injectionContext);
		Jpa jpa = injectionContext.get(Jpa.class);
		UserRepositoryImpl<User> userRepository = new UserRepositoryImpl<>(User.class, jpa);
		injectionContext.inject(userRepository).as(UserRepository.class);
		injectionContext.inject(userRepository).as(UserRepositoryImpl.class);
		injectionContext.inject(UserTokenRepositoryImpl.class).as(UserTokenRepository.class);
		injectionContext.inject(UserServiceImpl.class).as(UserService.class);
		injectionContext.inject(UserServiceImpl.class).as(ThundrUserService.class);
	}

	private void initialiseHibernate(UpdatableInjectionContext injectionContext) {
		DataSource dataSource = injectionContext.get(DataSource.class);
		HibernateConfig hibernateConfig = new HibernateConfig(dataSource);
		hibernateConfig.withProperty(Environment.SHOW_SQL, "true");
		hibernateConfig
				.withEntity(User.class)
				.withEntity(PasswordAuthentication.class)
				.withEntity(UserToken.class);
		injectionContext.inject(hibernateConfig).as(HibernateConfig.class);
	}

	@Override
	public void start(UpdatableInjectionContext injectionContext) {

		UserService userService = injectionContext.get(UserService.class);
		BinderRegistry binderRegistry = injectionContext.get(BinderRegistry.class);
		UserBinder<User> userActionMethodBinder = new UserBinder<>(User.class, userService);
		binderRegistry.registerBinder(userActionMethodBinder);
	}
}
