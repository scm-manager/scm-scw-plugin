/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    ScwResult scwResult = store.get(REPOSITORY, "1");

    assertThat(scwResult).isNotNull();
  }

  @Test
  void shouldStoreResult() {
    ScwResult result = new ScwResult("link", "title", "desc", ImmutableList.of("video"));
    store.put(REPOSITORY, "1", result);

    ScwResult storedResult = store.get(REPOSITORY, "1");
    assertThat(storedResult.getUrl()).isEqualTo("link");
    assertThat(storedResult.getName()).isEqualTo("title");
    assertThat(storedResult.getDescription()).isEqualTo("desc");
    assertThat(storedResult.getVideos().get(0)).isEqualTo("video");
  }
}
