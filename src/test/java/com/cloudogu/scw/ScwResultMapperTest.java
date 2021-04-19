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
