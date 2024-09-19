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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
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
    if (!Strings.isNullOrEmpty(text)) {
      for (Map.Entry<String, String> phrase : phrases.entrySet()) {
        Pattern pattern = Pattern.compile("(?i)" + phrase.getKey());
        if (pattern.matcher(text).find()) {
          matchingPhrases.add(phrase.getValue());
        }
      }
    }

    return matchingPhrases;
  }
}
