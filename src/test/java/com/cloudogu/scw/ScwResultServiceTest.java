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

import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScwResultServiceTest {

  private static final Repository REPOSITORY = RepositoryTestData.create42Puzzle();

  @Mock
  private ScwResultMapper mapper;
  @Mock
  private ScwResultStore store;
  @Mock
  private ScwAPI api;

  @InjectMocks
  private ScwResultService service;

  @BeforeEach
  void initService() {
    ImmutableMap<String, String> phrases = ImmutableMap.of("sqli", "sql injection", "xss", "cross site scripting");
    service.setPhraseDetector(new PhraseDetector(phrases));
  }

  @Test
  void shouldCheckPullRequestForResultsInTitle() {
    String title = "sqli protection";
    ScwResult result = createMocks();

    PullRequest pullRequest = createPullRequest(title, "");
    service.checkPullRequestForResults(REPOSITORY, pullRequest);

    verify(store, times(1)).put(eq(REPOSITORY), eq("1"), argThat(results -> {
      assertThat(results.getResults().get(0)).isEqualTo(result);
      return true;
    }));
  }

  @Test
  void shouldCheckPullRequestForResultsInDescription() {
    String description = "Should we also test for sqli?";
    ScwResult result = createMocks();

    PullRequest pullRequest = createPullRequest("", description);
    service.checkPullRequestForResults(REPOSITORY, pullRequest);

    verify(store, times(1)).put(eq(REPOSITORY), eq("1"), argThat(results -> {
      assertThat(results.getResults().get(0)).isEqualTo(result);
      return true;
    }));
  }

  @Test
  void shouldCheckPullRequestCommentForResults() {
    String text = "Should we also test for sqli?";
    ScwResult result = createMocks();

    Set<ScwResult> response = service.checkTextForResults(text);

    assertThat(response.iterator().next()).isEqualTo(result);
  }

  private ScwResult createMocks() {
    when(api.fetchForKeywords(ImmutableSet.of("sql injection")))
      .thenReturn(ImmutableList.of(new ScwResultDto()));
    ScwResult result = new ScwResult();
    when(mapper.map(any(ScwResultDto.class))).thenReturn(result);
    return result;
  }

  private PullRequest createPullRequest(String title, String description) {
    PullRequest pullRequest = new PullRequest();
    pullRequest.setId("1");
    pullRequest.setTitle(title);
    pullRequest.setDescription(description);
    return pullRequest;
  }

}
