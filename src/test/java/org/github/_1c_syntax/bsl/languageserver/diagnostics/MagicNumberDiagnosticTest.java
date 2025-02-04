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

import org.eclipse.lsp4j.Diagnostic;
import org.github._1c_syntax.bsl.languageserver.providers.DiagnosticProvider;
import org.github._1c_syntax.bsl.languageserver.utils.RangeHelper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MagicNumberDiagnosticTest extends AbstractDiagnosticTest<MagicNumberDiagnostic> {

  MagicNumberDiagnosticTest() { super(MagicNumberDiagnostic.class); }

  @Test
  void runTest() {
    // when
    List<Diagnostic> diagnostics = getDiagnostics();

    // then
    assertThat(diagnostics).hasSize(7);
    assertThat(diagnostics)
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(3, 18, 3, 20)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(3, 23, 3, 25)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(7, 31, 7, 33)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(11, 20, 11, 21)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(20, 21, 20, 23)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(23, 24, 23, 26)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(27, 34, 27, 35)));
  }

  @Test
  void testConfigure() {
    // conf
    Map<String, Object> configuration = DiagnosticProvider.getDefaultDiagnosticConfiguration(getDiagnosticInstance());
    configuration.put("authorizedNumbers", "-1,0,1,60,7");
    getDiagnosticInstance().configure(configuration);

    // when
    List<Diagnostic> diagnostics = getDiagnostics();

    // then
    assertThat(diagnostics).hasSize(4);
    assertThat(diagnostics)
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(7, 31, 7, 33)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(11, 20, 11, 21)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(20, 21, 20, 23)))
      .anyMatch(diagnostic -> diagnostic.getRange().equals(RangeHelper.newRange(23, 24, 23, 26)));
  }
}
