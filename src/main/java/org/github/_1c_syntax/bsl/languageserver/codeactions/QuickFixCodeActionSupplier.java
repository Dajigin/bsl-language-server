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
package org.github._1c_syntax.bsl.languageserver.codeactions;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.github._1c_syntax.bsl.languageserver.context.DocumentContext;
import org.github._1c_syntax.bsl.languageserver.diagnostics.BSLDiagnostic;
import org.github._1c_syntax.bsl.languageserver.diagnostics.QuickFixProvider;
import org.github._1c_syntax.bsl.languageserver.providers.DiagnosticProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class QuickFixCodeActionSupplier extends AbstractQuickFixSupplier {

  public QuickFixCodeActionSupplier(DiagnosticProvider diagnosticProvider) {
    super(diagnosticProvider);
  }

  @Override
  protected Stream<CodeAction> processDiagnosticStream(
    Stream<Diagnostic> diagnosticStream,
    CodeActionParams params,
    DocumentContext documentContext
  ) {
    return diagnosticStream
      .flatMap(diagnostic -> getCodeActionsByDiagnostic(diagnostic, params, documentContext).stream());
  }

  private List<CodeAction> getCodeActionsByDiagnostic(
    Diagnostic diagnostic,
    CodeActionParams params,
    DocumentContext documentContext
  ) {

    Class<? extends BSLDiagnostic> bslDiagnosticClass = DiagnosticProvider.getBSLDiagnosticClass(diagnostic);
    if (bslDiagnosticClass == null || !QuickFixProvider.class.isAssignableFrom(bslDiagnosticClass)) {
      return Collections.emptyList();
    }

    BSLDiagnostic diagnosticInstance = diagnosticProvider.getDiagnosticInstance(bslDiagnosticClass);
    return ((QuickFixProvider) diagnosticInstance).getQuickFixes(
      Collections.singletonList(diagnostic),
      params,
      documentContext
    );

  }
}
