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

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.store.InMemoryDataStore;
import sonia.scm.store.InMemoryDataStoreFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ScwResultStoreTest {

  private static final Repository REPOSITORY = RepositoryTestData.create42Puzzle();

  private ScwResultStore store;

  @BeforeEach
  void init() {
    store = new ScwResultStore(new InMemoryDataStoreFactory(new InMemoryDataStore<ScwResult>()));
  }

  @Test
  void shouldGetNotNullResultIfEmptyStore() {
    ScwResults scwResults = store.get(REPOSITORY, "1");

    assertThat(scwResults).isNotNull();
    assertThat(scwResults.getResults()).hasSize(0);
  }

  @Test
  void shouldStoreResult() {
    ScwResult result = new ScwResult("link", "title", "desc", ImmutableList.of("video", "video2"));
    store.put(REPOSITORY, "1", new ScwResults(ImmutableList.of(result)));

    ScwResult storedResult = store.get(REPOSITORY, "1").getResults().get(0);
    assertThat(storedResult.getUrl()).isEqualTo("link");
    assertThat(storedResult.getName()).isEqualTo("title");
    assertThat(storedResult.getDescription()).isEqualTo("desc");
    assertThat(storedResult.getVideos()).contains("video", "video2");
  }
}
