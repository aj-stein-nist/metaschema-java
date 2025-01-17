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

package gov.nist.secauto.metaschema.model.common.metapath.function.library;

import gov.nist.secauto.metaschema.model.common.metapath.DynamicContext;
import gov.nist.secauto.metaschema.model.common.metapath.evaluate.ISequence;
import gov.nist.secauto.metaschema.model.common.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.model.common.metapath.function.IArgument;
import gov.nist.secauto.metaschema.model.common.metapath.function.IFunction;
import gov.nist.secauto.metaschema.model.common.metapath.item.IAnyUriItem;
import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.List;

/**
 * Since a node doesn't have a base URI in Metaschema, this is an alias for the document-uri
 * function.
 */
public final class FnBaseUri {

  @NotNull
  static final IFunction SIGNATURE_NO_ARG = IFunction.builder()
      .name("base-uri")
      .deterministic()
      .contextDependent()
      .focusDependent()
      .returnType(IAnyUriItem.class)
      .returnOne()
      .functionHandler(FnBaseUri::executeNoArg)
      .build();

  @NotNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name("base-uri")
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.newBuilder()
          .name("arg1")
          .type(INodeItem.class)
          .zeroOrOne()
          .build())
      .returnType(IAnyUriItem.class)
      .returnOne()
      .functionHandler(FnBaseUri::executeOneArg)
      .build();

  private FnBaseUri() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NotNull
  private static ISequence<IAnyUriItem> executeNoArg(@NotNull IFunction function,
      @NotNull List<@NotNull ISequence<?>> arguments,
      @NotNull DynamicContext dynamicContext,
      INodeItem focus) {

    INodeItem item = focus;

    ISequence<IAnyUriItem> retval;
    if (item == null) {
      retval = ISequence.empty();
    } else {
      IAnyUriItem uri = fnBaseUri(item);
      retval = ISequence.of(uri);
    }
    return retval;
  }

  @SuppressWarnings("unused")
  @NotNull
  private static ISequence<IAnyUriItem> executeOneArg(@NotNull IFunction function,
      @NotNull List<@NotNull ISequence<?>> arguments,
      @NotNull DynamicContext dynamicContext,
      INodeItem focus) {

    ISequence<? extends INodeItem> arg = FunctionUtils.asType(arguments.get(0));

    INodeItem item = FunctionUtils.getFirstItem(arg, true);

    ISequence<IAnyUriItem> retval;
    if (item == null) {
      retval = ISequence.empty();
    } else {
      IAnyUriItem uri = fnBaseUri(item);
      retval = ISequence.of(uri);
    }
    return retval;
  }

  /**
   * Get the base URI for the provided {@code nodeItem}.
   * <p>
   * Based on the XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-base-uri">fn:base-uri</a> function.
   * 
   * @param nodeItem
   *          the node to get the base URI from
   * @return the base URI, or {@code null} if the node is either null or doesn't have a base URI
   */
  public static IAnyUriItem fnBaseUri(INodeItem nodeItem) {
    IAnyUriItem retval;
    if (nodeItem == null) {
      retval = null; // NOPMD - intentional
    } else {
      URI baseUri = nodeItem.getBaseUri();
      retval = baseUri == null ? null : IAnyUriItem.valueOf(baseUri);
    }
    return retval;
  }
}
