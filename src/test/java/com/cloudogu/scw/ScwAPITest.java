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
