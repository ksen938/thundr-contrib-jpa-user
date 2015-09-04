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

import com.atomicleopard.expressive.ETransformer;
import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.jpa.Action;
import com.threewks.thundr.jpa.Jpa;
import com.threewks.thundr.jpa.Propagation;
import com.threewks.thundr.jpa.ResultAction;
import com.threewks.thundr.jpa.repository.LongRepository;
import com.threewks.thundr.logger.Logger;
import com.threewks.thundr.user.UserTokenRepository;

import javax.persistence.EntityManager;
import java.util.List;

public class UserTokenRepositoryImpl<U extends User> implements UserTokenRepository<U> {

	private static final int LIMIT_ONE = 1;
	private static final int LIMIT_ONE_THOUSAND = 1000;
	private static final ETransformer<UserToken, Long> TO_USER_TOKEN_ID = new ETransformer<UserToken, Long>() {
		@Override
		public Long from(UserToken token) {
			return token.getId();
		}
	};

	private final Jpa jpa;
	private final LongRepository<UserToken> userTokenRepository;

	public UserTokenRepositoryImpl(Jpa jpa) {
		this.jpa = jpa;
		userTokenRepository = new LongRepository<>(UserToken.class, jpa);
	}

	@Override
	public String createToken(final U user) {
		final UserToken token = new UserToken(user);
		return jpa.run(Propagation.Required, new ResultAction<String>() {
			@Override
			public String run(EntityManager em) {
				userTokenRepository.create(token);
				return token.getToken();
			}
		});
	}

	@Override
	public void expireToken(U user, final String token) {
		jpa.run(Propagation.Required, new Action() {
			@Override
			public void run(EntityManager em) {
				List<UserToken> tokens = userTokenRepository.find(UserToken.Fields.Token, token, LIMIT_ONE);
				UserToken userToken = tokens.size() > 0 ? tokens.get(0) : null;
				if (userToken != null) {
					userTokenRepository.delete(userToken);
				}
			}
		});
	}

	@Override
	public void expireTokens(final U user) {
		jpa.run(Propagation.Required, new Action() {
			@Override
			public void run(EntityManager em) {
				List<UserToken> tokens = userTokenRepository.find(UserToken.Fields.User, user.getId(), LIMIT_ONE_THOUSAND);
				List<Long> tokenIds = Expressive.Transformers.transformAllUsing(TO_USER_TOKEN_ID).from(tokens);
				userTokenRepository.delete(tokenIds.toArray(new Long[tokenIds.size()]));
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public U getUserForToken(String token) {
		List<UserToken> tokens = userTokenRepository.find(UserToken.Fields.Token, token, LIMIT_ONE);
		UserToken userToken = tokens.size() > 0 ? tokens.get(0) : null;
		if (userToken == null) {
			Logger.warn("Recieved invalid user token: %s");
			return null;
		}
		return (U) userToken.getUser();
	}
}
