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
package org.github._1c_syntax.bsl.languageserver.diagnostics;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticMetadata;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticParameter;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticSeverity;
import org.github._1c_syntax.bsl.languageserver.diagnostics.metadata.DiagnosticType;
import org.github._1c_syntax.bsl.parser.BSLParser;

import java.util.Map;

@DiagnosticMetadata(
  type = DiagnosticType.CODE_SMELL,
  severity = DiagnosticSeverity.MAJOR,
  minutesToFix = 30
)
public class MethodSizeDiagnostic extends AbstractVisitorDiagnostic {

  private static final int MAX_METHOD_SIZE = 200;

  @DiagnosticParameter(
    type = Integer.class,
    defaultValue = "" + MAX_METHOD_SIZE,
    description = "Максимальная длина метода в строках"
  )
  private int maxMethodSize = MAX_METHOD_SIZE;

  @Override
  public void configure(Map<String, Object> configuration) {
    if (configuration == null) {
      return;
    }
    maxMethodSize = (Integer) configuration.get("maxMethodSize");
  }

  @Override
  public ParseTree visitProcedure(BSLParser.ProcedureContext ctx) {
    int methodSize = methodSize(ctx.subCodeBlock());

    if (methodSizeExceedsLimit(methodSize)) {
      diagnosticStorage.addDiagnostic(
        ctx.procDeclaration().subName(),
        getDiagnosticMessage(ctx.procDeclaration().subName().getText(), methodSize, maxMethodSize));
    }

    return ctx;
  }

  @Override
  public ParseTree visitFunction(BSLParser.FunctionContext ctx) {
    int methodSize = methodSize(ctx.subCodeBlock());

    if (methodSizeExceedsLimit(methodSize)) {
      diagnosticStorage.addDiagnostic(
        ctx.funcDeclaration().subName(),
        getDiagnosticMessage(ctx.funcDeclaration().subName().getText(), methodSize, maxMethodSize));
    }

    return ctx;
  }

  private boolean methodSizeExceedsLimit(int methodSize) {
    return methodSize > maxMethodSize;
  }

  private static int methodSize(BSLParser.SubCodeBlockContext ctx) {
    if (ctx.getTokens().isEmpty()) {
      return 0;
    }
    Token start = ctx.getStart();
    Token stop = ctx.getStop();

    return stop.getLine() - start.getLine();
  }

}
