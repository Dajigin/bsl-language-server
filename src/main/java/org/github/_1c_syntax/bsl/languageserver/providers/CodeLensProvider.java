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
package org.github._1c_syntax.bsl.languageserver.providers;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.github._1c_syntax.bsl.languageserver.configuration.LanguageServerConfiguration;
import org.github._1c_syntax.bsl.languageserver.context.DocumentContext;
import org.github._1c_syntax.bsl.languageserver.context.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CodeLensProvider {

  private final LanguageServerConfiguration configuration;

  public CodeLensProvider(LanguageServerConfiguration configuration) {
    this.configuration = configuration;
  }

  public List<CodeLens> getCodeLens(DocumentContext documentContext) {
    return getCognitiveComplexityCodeLenses(documentContext);
  }

  private List<CodeLens> getCognitiveComplexityCodeLenses(DocumentContext documentContext) {
    if (!configuration.isShowCognitiveComplexityCodeLens()) {
      return Collections.emptyList();
    }

    List<CodeLens> codeLenses = new ArrayList<>();

    Map<MethodSymbol, Integer> methodsComplexity = documentContext.getCognitiveComplexityData().getMethodsComplexity();

    methodsComplexity.forEach((MethodSymbol methodSymbol, Integer complexity) -> {
      String title = String.format("Cognitive complexity is %d", complexity);
      Command command = new Command(title, "");
      CodeLens codeLens = new CodeLens(
        methodSymbol.getSubNameRange(),
        command,
        null
      );

      codeLenses.add(codeLens);
    });

    return codeLenses;
  }
}
