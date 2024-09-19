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

import sonia.scm.net.ahc.AdvancedHttpClient;
import sonia.scm.net.ahc.AdvancedHttpResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
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
      throw new WebApplicationException(e);
    }
  }
}
