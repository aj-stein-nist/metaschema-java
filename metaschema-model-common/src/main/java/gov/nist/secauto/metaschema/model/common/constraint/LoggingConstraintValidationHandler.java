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

package gov.nist.secauto.metaschema.model.common.constraint;

import gov.nist.secauto.metaschema.model.common.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.model.common.metapath.DynamicContext;
import gov.nist.secauto.metaschema.model.common.metapath.MetapathException;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.ISequence;
import gov.nist.secauto.metaschema.model.common.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoggingConstraintValidationHandler
    extends AbstractConstraintValidationHandler {
  private static final Logger LOGGER = LogManager.getLogger(DefaultConstraintValidator.class);
  @NotNull
  private IPathFormatter pathFormatter = IPathFormatter.METAPATH_PATH_FORMATER;

  @Override
  @NotNull
  public IPathFormatter getPathFormatter() {
    return pathFormatter;
  }

  @SuppressWarnings("null")
  public void setPathFormatter(@NotNull IPathFormatter pathFormatter) {
    this.pathFormatter = Objects.requireNonNull(pathFormatter, "pathFormatter");
  }

  protected LogBuilder getLogBuilder(@NotNull IConstraint constraint) {
    Level level = constraint.getLevel();

    LogBuilder retval;
    switch (level) {
    case CRITICAL:
      retval = LOGGER.atFatal();
      break;
    case ERROR:
      retval = LOGGER.atError();
      break;
    case WARNING:
      retval = LOGGER.atWarn();
      break;
    case INFORMATIONAL:
      retval = LOGGER.atInfo();
      break;
    default:
      throw new UnsupportedOperationException(String.format("unsupported level '%s'", level));
    }
    return retval;
  }

  @Override
  protected String toPath(@NotNull INodeItem nodeItem) {
    return nodeItem.toPath(getPathFormatter());
  }

  protected boolean isLogged(@NotNull IConstraint constraint) {
    Level level = constraint.getLevel();

    boolean retval;
    switch (level) {
    case CRITICAL:
      retval = LOGGER.isFatalEnabled();
      break;
    case ERROR:
      retval = LOGGER.isErrorEnabled();
      break;
    case WARNING:
      retval = LOGGER.isWarnEnabled();
      break;
    case INFORMATIONAL:
      retval = LOGGER.isInfoEnabled();
      break;
    default:
      throw new UnsupportedOperationException(String.format("unsupported level '%s'", level));
    }
    return retval;
  }

  protected void logConstraint(
      @NotNull IConstraint constraint,
      @NotNull INodeItem node,
      @NotNull CharSequence message) {
    getLogBuilder(constraint).log("{}: ({}) {}", constraint.getLevel().name(), toPath(node), message);
  }

  protected void logConstraint(
      @NotNull IConstraint constraint,
      @NotNull INodeItem node,
      @NotNull CharSequence message,
      @NotNull Throwable cause) {
    getLogBuilder(constraint).withThrowable(cause).log("{}: ({}) {}", constraint.getLevel().name(), toPath(node),
        message);
  }

  @Override
  public void handleCardinalityMinimumViolation(
      @NotNull ICardinalityConstraint constraint,
      @NotNull INodeItem node,
      @NotNull ISequence<? extends INodeItem> targets) {
    if (isLogged(constraint)) {
      logConstraint(constraint, node, newCardinalityMinimumViolationMessage(constraint, node, targets));
    }
  }

  @Override
  public void handleCardinalityMaximumViolation(
      @NotNull ICardinalityConstraint constraint,
      @NotNull INodeItem node,
      @NotNull ISequence<? extends INodeItem> targets) {
    if (isLogged(constraint)) {
      logConstraint(constraint, node, newCardinalityMaximumViolationMessage(constraint, node, targets));
    }
  }

  @Override
  public void handleIndexDuplicateKeyViolation(
      @NotNull IIndexConstraint constraint,
      @NotNull INodeItem node,
      @NotNull INodeItem oldItem,
      @NotNull INodeItem target) {
    if (isLogged(constraint)) {
      logConstraint(constraint, target, newIndexDuplicateKeyViolationMessage(constraint, node, oldItem, target));
    }
  }

  @Override
  public void handleUniqueKeyViolation(
      @NotNull IUniqueConstraint constraint,
      @NotNull INodeItem node,
      @NotNull INodeItem oldItem,
      @NotNull INodeItem target) {
    if (isLogged(constraint)) {
      logConstraint(constraint, target, newUniqueKeyViolationMessage(constraint, node, oldItem, target));
    }
  }

  @SuppressWarnings("null")
  @Override
  public void handleKeyMatchError(
      @NotNull IKeyConstraint constraint,
      @NotNull INodeItem node,
      @NotNull INodeItem target,
      @NotNull MetapathException cause) {
    if (isLogged(constraint)) {
      logConstraint(constraint, target, cause.getLocalizedMessage(), cause);
    }
  }

  @Override
  public void handleMatchPatternViolation(
      @NotNull IMatchesConstraint constraint,
      @NotNull INodeItem node,
      @NotNull INodeItem target,
      @NotNull String value) {
    if (isLogged(constraint)) {
      logConstraint(constraint, target, newMatchPatternViolationMessage(constraint, node, target, value));
    }
  }

  @Override
  public void handleMatchDatatypeViolation(
      @NotNull IMatchesConstraint constraint,
      @NotNull INodeItem node,
      @NotNull INodeItem target,
      @NotNull String value,
      @NotNull IllegalArgumentException cause) {
    if (isLogged(constraint)) {
      logConstraint(constraint, target, newMatchDatatypeViolationMessage(constraint, node, target, value), cause);
    }
  }

  @Override
  public void handleExpectViolation(
      @NotNull IExpectConstraint constraint,
      @NotNull INodeItem node,
      @NotNull INodeItem target,
      @NotNull DynamicContext dynamicContext) {
    if (isLogged(constraint)) {
      logConstraint(constraint, target, newExpectViolationMessage(constraint, node, target, dynamicContext));
    }
  }
}
