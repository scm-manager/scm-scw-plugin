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

import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.google.common.annotations.VisibleForTesting;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
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
    for (ScwResultDto result : results) {
      store.put(repository, pullRequest.getId(), mapper.map(result));
    }
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
