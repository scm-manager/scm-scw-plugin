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

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhraseDetectorTest {

  @Test
  void shouldDetectPhraseInText() {
    ImmutableMap<String, String> phrases = ImmutableMap.of("xss", "xss attack", "sqli", "sql injection");
    PhraseDetector phraseDetector = new PhraseDetector(phrases);

    assertThat(phraseDetector.detect("this is some xss attack").size()).isEqualTo(1);
    assertThat(phraseDetector.detect("lets try out sqli!").size()).isEqualTo(1);
    assertThat(phraseDetector.detect("Why not both? SQLI and XSS!").size()).isEqualTo(2);

    // Should not detect
    assertThat(phraseDetector.detect("random text").size()).isEqualTo(0);
    assertThat(phraseDetector.detect("sqil").size()).isEqualTo(0);
    assertThat(phraseDetector.detect("XXL + SQLY").size()).isEqualTo(0);
  }

  @Test
  void shouldDetectPhraseInMultiLineText() {
    ImmutableMap<String, String> phrases = ImmutableMap.of("xss", "xss attack", "sqli", "sql injection");
    PhraseDetector phraseDetector = new PhraseDetector(phrases);

    assertThat(phraseDetector.detect("lets\n\n try \n\n xss").size()).isEqualTo(1);
  }

  @Test
  void shouldDetectMoreComplexRegEx() {
    ImmutableMap<String, String> phrases = ImmutableMap.of(
      "(weak|improper|missing|broken) input validation", "input infos",
      "app(lication)? background(ing)? screenshot", "spy"
    );

    PhraseDetector phraseDetector = new PhraseDetector(phrases);

    assertThat(phraseDetector.detect("weak input validation").size()).isEqualTo(1);
    assertThat(phraseDetector.detect("app backgrounding screenshot").size()).isEqualTo(1);
    assertThat(phraseDetector.detect("Missing Input Validation and application background screenshot").size()).isEqualTo(2);
    assertThat(phraseDetector.detect("Missing Int Vation and application backund screenshot").size()).isEqualTo(0);
  }
}
