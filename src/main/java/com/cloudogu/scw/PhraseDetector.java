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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PhraseDetector {

  private static final Logger LOG = LoggerFactory.getLogger(PhraseDetector.class);

  private Map<String, String> phrases;

  public PhraseDetector() {
    try {
      InputStream is = getClass().getClassLoader().getResourceAsStream("com/cloudogu/scw/phraseList.json");
      phrases = new ObjectMapper().readValue(is, Map.class);
    } catch (IOException e) {
      LOG.error("Could not find phrases", e);
    }
  }

  PhraseDetector(Map<String, String> phrases) {
    this.phrases = phrases;
  }

  public Set<String> detect(String text) {
    Set<String> matchingPhrases = new HashSet<>();

    for (Map.Entry<String, String> phrase : phrases.entrySet()) {
      Pattern pattern = Pattern.compile("(?i).*" + phrase.getKey() + ".*");
      if (pattern.matcher(text).find()) {
        matchingPhrases.add(phrase.getValue());
      }
    }

    return matchingPhrases;
  }
}
