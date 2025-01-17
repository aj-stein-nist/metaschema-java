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

package gov.nist.secauto.metaschema.model.common.metapath.format;

import gov.nist.secauto.metaschema.model.common.metapath.item.INodeItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A named segment of a path that can be formatted.
 */
public interface IPathSegment {
  /**
   * Get the path for this node item using the provided formatter.
   * 
   * @param formatter
   *          the path formatter to use to produce the path
   * 
   * @return the formatted path
   */
  @NotNull
  default String toPath(@NotNull IPathFormatter formatter) {
    return formatter.format(this);
  }

  /**
   * Apply formatting for the path segment. This is a visitor pattern that will be called to format
   * each segment in a larger path.
   * 
   * @param formatter
   *          the path formatter
   * @return a textual representation of the path segment
   */
  @NotNull
  String format(@NotNull IPathFormatter formatter);

  /**
   * Get a list of path segments, starting at the root and descending.
   * 
   * @return a list of path segments in descending order
   */
  @SuppressWarnings("null")
  @NotNull
  default List<@NotNull IPathSegment> getPath() {
    return getPathStream().collect(Collectors.toUnmodifiableList());
  }

  /**
   * Get a stream of path segments, starting at the root and descending.
   * 
   * @return a stream of path segments in descending order
   */
  @NotNull
  Stream<@NotNull ? extends IPathSegment> getPathStream();

  /**
   * Get the value associated with the path segment.
   * 
   * @return the value or {@code} if no value is associated with this path segment
   */
  INodeItem getNodeItem();
}
