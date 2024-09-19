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
import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import jakarta.inject.Inject;
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
