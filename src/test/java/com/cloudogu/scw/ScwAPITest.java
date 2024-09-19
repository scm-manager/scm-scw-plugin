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

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpRequest;
import sonia.scm.net.ahc.AdvancedHttpResponse;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScwAPITest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private AdvancedHttpClient client;

  @Mock(answer = Answers.RETURNS_SELF)
  private AdvancedHttpRequest request;

  @InjectMocks
  private ScwAPI api;

  @Test
  void shouldFetchForKeywords() throws IOException {
    when(client.get("https://integration-api.securecodewarrior.com/api/v1/trial")).thenReturn(request);
    when(request.request()).thenReturn(mock(AdvancedHttpResponse.class));

    api.fetchForKeywords(ImmutableSet.of("xss", "sqli"));

    verify(client, times(2)).get("https://integration-api.securecodewarrior.com/api/v1/trial");
    verify(request, times(2)).spanKind("Secure Code Warrior");
    verify(request, times(2)).queryString("Id", "cloudogu");
    verify(request, times(2)).queryString("MappingList", "phrase");
    verify(request).queryString("MappingKey", "xss");
    verify(request).queryString("MappingKey", "sqli");
  }

}
