/*
 * This file is a part of BSL Language Server.
 *
 * Copyright © 2018-2019
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */
package org.github._1c_syntax.bsl.languageserver.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.github._1c_syntax.bsl.languageserver.configuration.diagnostics.DiagnosticConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@Data
@RequiredArgsConstructor
@JsonDeserialize(using = LanguageServerConfiguration.LanguageServerConfigurationDeserializer.class)
public class LanguageServerConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(LanguageServerConfiguration.class.getSimpleName());

  private final DiagnosticLanguage diagnosticLanguage;
  private final Map<String, Either<Boolean, DiagnosticConfiguration>> diagnostics;

  public LanguageServerConfiguration() {
    diagnostics = new HashMap<>();
    diagnosticLanguage = DiagnosticLanguage.EN;
  }

  static class LanguageServerConfigurationDeserializer extends JsonDeserializer<LanguageServerConfiguration> {

    @Override
    public LanguageServerConfiguration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      JsonNode node = jp.getCodec().readTree(jp);

      String diagnosticLanguage = getDiagnosticLanguage(node);

      Map<String, Either<Boolean, DiagnosticConfiguration>> diagnosticsMap = getDiagnostics(node);

      return new LanguageServerConfiguration(
        DiagnosticLanguage.valueOf(diagnosticLanguage.toUpperCase(Locale.ENGLISH)),
        diagnosticsMap
      );
    }

    private Map<String, Either<Boolean, DiagnosticConfiguration>> getDiagnostics(JsonNode node) {
      JsonNode diagnostics = node.get("diagnostics");

      if (diagnostics == null) {
        return Collections.emptyMap();
      }

      ObjectMapper mapper = new ObjectMapper();
      Map<String, Either<Boolean, DiagnosticConfiguration>> diagnosticsMap = new HashMap<>();

      Iterator<Map.Entry<String, JsonNode>> diagnosticsNodes = diagnostics.fields();
      diagnosticsNodes.forEachRemaining(entry -> {
        JsonNode diagnosticConfig = entry.getValue();
        if (diagnosticConfig.isBoolean()) {
          diagnosticsMap.put(entry.getKey(), Either.forLeft(diagnosticConfig.asBoolean()));
        } else {
          Class<? extends DiagnosticConfiguration> diagnosticClass;
          try {
            diagnosticClass = Class.forName("org.github._1c_syntax.bsl.languageserver.configuration.diagnostics." + entry.getKey() + "DiagnosticConfiguration").asSubclass(DiagnosticConfiguration.class);
          } catch (ClassNotFoundException e) {
            LOGGER.error("Can't find corresponding diagnostic configuration class", e);
            return;
          }
          DiagnosticConfiguration diagnosticConfiguration;
          try {
            diagnosticConfiguration = mapper.treeToValue(diagnosticConfig, diagnosticClass);
          } catch (JsonProcessingException e) {
            LOGGER.error("Can't deserialize diagnostic configuration", e);
            return;
          }
          diagnosticsMap.put(entry.getKey(), Either.forRight(diagnosticConfiguration));
        }
      });


      return diagnosticsMap;
    }

    private String getDiagnosticLanguage(JsonNode node) {
      String diagnosticLanguage;
      if (node.get("diagnosticLanguage") == null) {
        diagnosticLanguage = "en";
      } else {
        diagnosticLanguage = node.get("diagnosticLanguage").asText();
      }
      return diagnosticLanguage;
    }
  }
}
