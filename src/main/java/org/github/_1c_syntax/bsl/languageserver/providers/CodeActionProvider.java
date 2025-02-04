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

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.github._1c_syntax.bsl.languageserver.codeactions.CodeActionSupplier;
import org.github._1c_syntax.bsl.languageserver.codeactions.FixAllCodeActionSupplier;
import org.github._1c_syntax.bsl.languageserver.codeactions.QuickFixCodeActionSupplier;
import org.github._1c_syntax.bsl.languageserver.context.DocumentContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CodeActionProvider {

  private final DiagnosticProvider diagnosticProvider;

  public CodeActionProvider(DiagnosticProvider diagnosticProvider) {
    this.diagnosticProvider = diagnosticProvider;
  }

  public static List<CodeAction> createCodeActions(
    List<TextEdit> textEdits,
    String title,
    String uri,
    List<Diagnostic> diagnostics
  ) {

    if (diagnostics.isEmpty()) {
      return Collections.emptyList();
    }

    WorkspaceEdit edit = new WorkspaceEdit();

    Map<String, List<TextEdit>> changes = new HashMap<>();
    changes.put(uri, textEdits);
    edit.setChanges(changes);

    if (diagnostics.size() > 1) {
      title = "Fix all: " + title;
    }

    CodeAction codeAction = new CodeAction(title);
    codeAction.setDiagnostics(diagnostics);
    codeAction.setEdit(edit);
    codeAction.setKind(CodeActionKind.QuickFix);

    return Collections.singletonList(codeAction);

  }

  public List<Either<Command, CodeAction>> getCodeActions(
    CodeActionParams params,
    DocumentContext documentContext
  ) {

    List<CodeAction> codeActions = new ArrayList<>();

    CodeActionSupplier fixAllCodeActionSupplier = new FixAllCodeActionSupplier(diagnosticProvider);
    CodeActionSupplier quickFixCodeActionSupplier = new QuickFixCodeActionSupplier(diagnosticProvider);

    codeActions.addAll(quickFixCodeActionSupplier.getCodeActions(params, documentContext));
    codeActions.addAll(fixAllCodeActionSupplier.getCodeActions(params, documentContext));

    return convertCodeActionListToEitherList(codeActions);
  }

  private static List<Either<Command, CodeAction>> convertCodeActionListToEitherList(List<CodeAction> actions) {
    return actions.stream().map(
      (Function<CodeAction, Either<Command, CodeAction>>) Either::forRight).collect(Collectors.toList());
  }

}
