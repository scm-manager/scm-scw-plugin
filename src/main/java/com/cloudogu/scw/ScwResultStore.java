/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scw;

import sonia.scm.repository.Repository;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import jakarta.inject.Inject;

public class ScwResultStore {

  private static final String STORE_NAME = "secure-code-warrior";

  private final DataStoreFactory dataStoreFactory;

  @Inject
  public ScwResultStore(DataStoreFactory dataStoreFactory) {
    this.dataStoreFactory = dataStoreFactory;
  }

  public ScwResults get(Repository repository, String id) {
    return createStore(repository).getOptional(id).orElse(new ScwResults());
  }

  public void put(Repository repository, String id, ScwResults results) {
    createStore(repository).put(id, results);
  }

  private DataStore<ScwResults> createStore(Repository repository) {
    return dataStoreFactory.withType(ScwResults.class).withName(STORE_NAME).forRepository(repository).build();
  }
}
