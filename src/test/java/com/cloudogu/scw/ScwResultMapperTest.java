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

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScwResultMapperTest {

  private final ScwResultMapper mapper = new ScwResultMapperImpl();

  @Test
  void shouldMapToDto() {
    ScwResult scwResult = new ScwResult("url", "name", "desc", ImmutableList.of("video1", "video2"));
    ScwResultDto dto = mapper.map(scwResult);

    assertThat(dto.getUrl()).isEqualTo("url");
    assertThat(dto.getName()).isEqualTo("name");
    assertThat(dto.getDescription()).isEqualTo("desc");
    assertThat(dto.getVideos()).hasSize(2);
    assertThat(dto.getVideos()).contains("video1", "video2");
  }

  @Test
  void shouldMapFromDto() {
    ScwResultDto dto = new ScwResultDto();
    dto.setUrl("url");
    dto.setName("name");
    dto.setDescription("desc");
    dto.setVideos(ImmutableList.of("video1", "video2"));

    ScwResult result = mapper.map(dto);

    assertThat(result.getUrl()).isEqualTo("url");
    assertThat(result.getName()).isEqualTo("name");
    assertThat(result.getDescription()).isEqualTo("desc");
    assertThat(result.getVideos()).hasSize(2);
    assertThat(result.getVideos()).contains("video1", "video2");
  }

}
