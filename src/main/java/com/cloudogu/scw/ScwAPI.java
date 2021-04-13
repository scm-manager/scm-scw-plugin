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

import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpResponse;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScwAPI {

  private static final String SCW_BASE_URL = "https://integration-api.securecodewarrior.com/api/v1/trial";

  private final AdvancedHttpClient httpClient;

  @Inject
  public ScwAPI(AdvancedHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<ScwResultDto> fetchForKeywords(Set<String> keywords) {
    List<ScwResultDto> results = new ArrayList<>();
    for (String keyword : keywords) {
      results.add(fetchForKeyword(keyword));
    }
    return results;
  }

  private ScwResultDto fetchForKeyword(String keyword) {
    try {
      AdvancedHttpResponse response = httpClient
        .get(SCW_BASE_URL)
        .queryString("Id", "cloudogu")
        .queryString("MappingList", "phrase")
        .queryString("MappingKey", keyword)
        .spanKind("Secure Code Warrior")
        .request();
      return response.contentFromJson(ScwResultDto.class);
    } catch (IOException e) {
      // Do something
      throw new WebApplicationException();
    }
  }
}
