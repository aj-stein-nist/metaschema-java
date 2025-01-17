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

package gov.nist.secauto.metaschema.model.xmlbeans.handler;

import gov.nist.secauto.metaschema.model.common.datatype.DataTypeService;
import gov.nist.secauto.metaschema.model.common.datatype.IJavaTypeAdapter;

import org.apache.xmlbeans.SimpleValue;

public final class DatatypesHandler {
  private DatatypesHandler() {
    // disable construction
  }

  /**
   * Given an XMLBeans value, return the {@link IJavaTypeAdapter} instance with the given name, as
   * determined by {@link IJavaTypeAdapter#getName()}.
   * 
   * @param value
   *          the name of the data type
   * @return the data type instance
   */
  public static IJavaTypeAdapter<?> decodeFieldDatatypesType(SimpleValue value) {
    return decode(value);
  }

  /**
   * Given a data type instance, set the name of the data type, as determined by
   * {@link IJavaTypeAdapter#getName()}, in the provided target XMLBeans value.
   * 
   * @param datatype
   *          the data type instance
   * @param target
   *          XMLBeans value to apply the name to
   */
  public static void encodeFieldDatatypesType(IJavaTypeAdapter<?> datatype, SimpleValue target) {
    encode(datatype, target);
  }

  /**
   * Given an XMLBeans value, return the {@link IJavaTypeAdapter} instance with the given name, as
   * determined by {@link IJavaTypeAdapter#getName()}.
   * 
   * @param value
   *          the name of the data type
   * @return the data type instance
   */
  public static IJavaTypeAdapter<?> decodeSimpleDatatypesType(SimpleValue value) {
    return decode(value);
  }

  /**
   * Given a data type instance, set the name of the data type, as determined by
   * {@link IJavaTypeAdapter#getName()}, in the provided target XMLBeans value.
   * 
   * @param datatype
   *          the data type instance
   * @param target
   *          XMLBeans value to apply the name to
   */
  public static void encodeSimpleDatatypesType(IJavaTypeAdapter<?> datatype, SimpleValue target) {
    encode(datatype, target);
  }

  private static IJavaTypeAdapter<?> decode(SimpleValue target) {
    String name = target.getStringValue();
    IJavaTypeAdapter<?> retval = DataTypeService.getInstance().getJavaTypeAdapterByName(name);
    if (retval == null) {
      throw new IllegalStateException("Unable to find data type: " + name);
    }
    return retval;
  }

  private static void encode(IJavaTypeAdapter<?> datatype, SimpleValue target) {
    if (datatype != null) {
      target.setStringValue(datatype.getName());
    }
  }

}
