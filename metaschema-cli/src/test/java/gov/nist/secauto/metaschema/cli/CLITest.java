/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.nist.secauto.metaschema.cli.commands.QueryCommand;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.altindag.log.LogCaptor;

/**
 * Unit test for simple CLI.
 */
public class CLITest {
  void evaluateResult(@NonNull ExitStatus status, @NonNull ExitCode expectedCode) {
    status.generateMessage(true);
    assertAll(() -> assertEquals(expectedCode, status.getExitCode(), "exit code mismatch"),
        () -> assertNull(status.getThrowable(), "expected null Throwable"));
  }

  void evaluateResult(@NonNull ExitStatus status, @NonNull ExitCode expectedCode,
      @NonNull Class<? extends Throwable> thrownClass) {
    status.generateMessage(true);
    Throwable thrown = status.getThrowable();
    assert thrown != null;
    assertAll(() -> assertEquals(expectedCode, status.getExitCode(), "exit code mismatch"),
        () -> assertEquals(thrownClass, thrown.getClass(), "expected Throwable mismatch"));
  }

  private static Stream<Arguments> providesValues() {
    ExitCode noExpectedExceptionClass = null;
    List<Arguments> values = new ArrayList<>();
    values.add(Arguments.of(new String[] {}, ExitCode.INVALID_COMMAND, noExpectedExceptionClass));
    values.add(Arguments.of(new String[] { "-h" }, ExitCode.OK, noExpectedExceptionClass));
    values.add(Arguments.of(new String[] { "generate-schema", "--help" }, ExitCode.INVALID_COMMAND,
        noExpectedExceptionClass));
    values.add(Arguments.of(new String[] { "validate", "--help" }, ExitCode.OK, noExpectedExceptionClass));
    values.add(Arguments.of(new String[] { "validate-content", "--help" }, ExitCode.INVALID_COMMAND,
        noExpectedExceptionClass));
    values.add(Arguments.of(
        new String[] { "validate",
            "../databind/src/test/resources/metaschema/fields_with_flags/metaschema.xml" },
        ExitCode.OK, noExpectedExceptionClass));
    values.add(Arguments.of(new String[] { "generate-schema", "--overwrite", "--as", "JSON",
        "../databind/src/test/resources/metaschema/fields_with_flags/metaschema.xml",
        "target/schema-test.json" }, ExitCode.OK, noExpectedExceptionClass));
    return values.stream();
  }

  @ParameterizedTest
  @MethodSource("providesValues")
  void testAllCommands(@NonNull String[] args, @NonNull ExitCode expectedExitCode,
      Class<? extends Throwable> expectedThrownClass) {
    if (expectedThrownClass == null) {
      evaluateResult(CLI.runCli(args), expectedExitCode);
    } else {
      evaluateResult(CLI.runCli(args), expectedExitCode, expectedThrownClass);
    }
  }

  @Test
  void testQueryCommand() {
    LogCaptor logCaptor = LogCaptor.forClass(QueryCommand.class);
    String[] args
        = new String[] { "query", "-m", "../databind/src/test/resources/metaschema/fields_with_flags/metaschema.xml",
            "-i",
            "../databind/src/test/resources/metaschema/fields_with_flags/example.json", "3 + 4 + 5" };
    CLI.runCli(args);
    assertThat(logCaptor.getInfoLogs()).containsExactly("[12]");
  };
}
