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
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Extension
@Enrich(PullRequest.class)
public class PullRequestEmbeddedEnricher implements HalEnricher {

  private final ScwResultStore store;
  private final ScwResultMapper mapper;

  @Inject
  public PullRequestEmbeddedEnricher(ScwResultStore store, ScwResultMapper mapper) {
    this.store = store;
    this.mapper = mapper;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    PullRequest pullRequest = context.oneRequireByType(PullRequest.class);
    ScwResults scwResults = store.get(repository, pullRequest.getId());
    List<ScwResultDto> dtos = new ArrayList<>();
    for (ScwResult result : scwResults.getResults()) {
      dtos.add(mapper.map(result));
    }
    ScwResultsDto resultsDto = new ScwResultsDto();
    resultsDto.setResults(dtos);
    appender.appendEmbedded("scwResults", resultsDto);
  }
}
