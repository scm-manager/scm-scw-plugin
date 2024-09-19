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
import com.google.common.annotations.VisibleForTesting;
import sonia.scm.repository.Repository;

import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScwResultService {

  private final ScwResultStore store;
  private final ScwResultMapper mapper;
  private final ScwAPI scwAPI;
  private PhraseDetector phraseDetector = new PhraseDetector();

  @Inject
  public ScwResultService(ScwResultStore store, ScwResultMapper mapper, ScwAPI scwAPI) {
    this.store = store;
    this.mapper = mapper;
    this.scwAPI = scwAPI;
  }

  @VisibleForTesting
  public void setPhraseDetector(PhraseDetector phraseDetector) {
    this.phraseDetector = phraseDetector;
  }

  public void checkPullRequestForResults(Repository repository, PullRequest pullRequest) {
    Set<String> matchingKeywords = findMatchesInPullRequest(pullRequest);

    List<ScwResultDto> results = scwAPI.fetchForKeywords(matchingKeywords);
    ScwResults scwResults = new ScwResults();
    for (ScwResultDto result : results) {
      scwResults.add(mapper.map(result));
    }
    store.put(repository, pullRequest.getId(), scwResults);
  }

  private Set<String> findMatchesInPullRequest(PullRequest pullRequest) {
    Set<String> matchingKeywords = new HashSet<>();
    matchingKeywords.addAll(phraseDetector.detect(pullRequest.getTitle()));
    matchingKeywords.addAll(phraseDetector.detect(pullRequest.getDescription()));
    return matchingKeywords;
  }

  public Set<ScwResult> checkTextForResults(String text) {
    Set<String> matchingKeywords = phraseDetector.detect(text);

    Set<ScwResult> results = new HashSet<>();
    List<ScwResultDto> dtos = scwAPI.fetchForKeywords(matchingKeywords);
    for (ScwResultDto dto : dtos) {
      results.add(mapper.map(dto));
    }
    return results;
  }
}
