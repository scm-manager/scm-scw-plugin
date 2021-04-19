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
