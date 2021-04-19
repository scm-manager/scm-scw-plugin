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
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestEmbeddedEnricherTest {

  private static final Repository REPOSITORY = RepositoryTestData.create42Puzzle();

  @Mock
  private HalAppender appender;
  @Mock
  private ScwResultStore store;

  private PullRequestEmbeddedEnricher enricher;

  @BeforeEach
  void initEnricher() {
    enricher = new PullRequestEmbeddedEnricher(store, new ScwResultMapperImpl());
  }

  @Test
  void shouldAppendStoredResult() {
    PullRequest pullRequest = new PullRequest("1", "source", "target");
    ScwResult result = new ScwResult("url", "name", "desc", ImmutableList.of("video"));
    ScwResult result2 = new ScwResult("url2", "name2", "desc2", ImmutableList.of("video2", "video3"));
    ImmutableList<ScwResult> results = ImmutableList.of(result, result2);
    when(store.get(REPOSITORY, pullRequest.getId())).thenReturn(new ScwResults(results));
    HalEnricherContext context = HalEnricherContext.of(REPOSITORY, pullRequest);

    enricher.enrich(context, appender);

    verify(appender).appendEmbedded(eq("scwResults"), (ScwResultsDto) argThat(dto -> {
      List<ScwResultDto> dtoResults = ((ScwResultsDto) dto).getResults();
      assertThat(dtoResults).hasSize(2);

      ScwResultDto firstResultDto = dtoResults.get(0);
      assertThat(firstResultDto.getUrl()).isEqualTo(result.getUrl());
      assertThat(firstResultDto.getDescription()).isEqualTo(result.getDescription());
      assertThat(firstResultDto.getName()).isEqualTo(result.getName());
      assertThat(firstResultDto.getVideos()).isEqualTo(result.getVideos());

      ScwResultDto secondResultDto = dtoResults.get(1);
      assertThat(secondResultDto.getUrl()).isEqualTo(result2.getUrl());
      assertThat(secondResultDto.getDescription()).isEqualTo(result2.getDescription());
      assertThat(secondResultDto.getName()).isEqualTo(result2.getName());
      assertThat(secondResultDto.getVideos()).isEqualTo(result2.getVideos());
      return true;
    }));
  }
}
