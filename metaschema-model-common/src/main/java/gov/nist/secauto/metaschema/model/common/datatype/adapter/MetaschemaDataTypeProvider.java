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

package gov.nist.secauto.metaschema.model.common.datatype.adapter;

import com.google.auto.service.AutoService;

import gov.nist.secauto.metaschema.model.common.datatype.AbstractDataTypeProvider;
import gov.nist.secauto.metaschema.model.common.datatype.IDataTypeProvider;

import org.jetbrains.annotations.NotNull;

@AutoService(IDataTypeProvider.class)
public class MetaschemaDataTypeProvider
    extends AbstractDataTypeProvider {
  @NotNull
  public static final Base64Adapter BASE64 = new Base64Adapter();
  @NotNull
  public static final BooleanAdapter BOOLEAN = new BooleanAdapter();
  @NotNull
  public static final DateAdapter DATE = new DateAdapter();
  @NotNull
  public static final DateWithTZAdapter DATE_WITH_TZ = new DateWithTZAdapter();
  @NotNull
  public static final DateTimeAdapter DATE_TIME = new DateTimeAdapter();
  @NotNull
  public static final DateTimeWithTZAdapter DATE_TIME_WITH_TZ = new DateTimeWithTZAdapter();
  @NotNull
  public static final IPv4AddressAdapter IP_V4_ADDRESS = new IPv4AddressAdapter();
  @NotNull
  public static final IPv6AddressAdapter IP_V6_ADDRESS = new IPv6AddressAdapter();
  @NotNull
  public static final UriAdapter URI = new UriAdapter();
  @NotNull
  public static final UriReferenceAdapter URI_REFERENCE = new UriReferenceAdapter();
  @NotNull
  public static final UuidAdapter UUID = new UuidAdapter();

  @NotNull
  public static final DayTimeAdapter DAY_TIME_DURATION = new DayTimeAdapter();
  @NotNull
  public static final YearMonthAdapter YEAR_MONTH_DURATION = new YearMonthAdapter();

  @NotNull
  public static final DecimalAdapter DECIMAL = new DecimalAdapter();
  @NotNull
  public static final IntegerAdapter INTEGER = new IntegerAdapter();
  @NotNull
  public static final NonNegativeIntegerAdapter NON_NEGATIVE_INTEGER = new NonNegativeIntegerAdapter();
  @NotNull
  public static final PositiveIntegerAdapter POSITIVE_INTEGER = new PositiveIntegerAdapter();

  @NotNull
  public static final EmailAddressAdapter EMAIL_ADDRESS = new EmailAddressAdapter();
  @NotNull
  public static final HostnameAdapter HOSTNAME = new HostnameAdapter();
  @NotNull
  public static final NcNameAdapter NCNAME = new NcNameAdapter();
  @NotNull
  public static final StringAdapter STRING = new StringAdapter();
  @NotNull
  public static final TokenAdapter TOKEN = new TokenAdapter();

  @NotNull
  public static final MarkupLineAdapter MARKUP_LINE = new MarkupLineAdapter();
  @NotNull
  public static final MarkupMultilineAdapter MARKUP_MULTILINE = new MarkupMultilineAdapter();

  @NotNull
  public static final StringAdapter DEFAULT_DATA_TYPE = STRING;

  public MetaschemaDataTypeProvider() {
    registerDatatype(BASE64);
    registerDatatype(BOOLEAN);
    registerDatatype(DATE);
    registerDatatype(DATE_WITH_TZ);
    registerDatatype(DATE_TIME);
    registerDatatype(DATE_TIME_WITH_TZ);
    registerDatatype(DAY_TIME_DURATION);
    registerDatatype(DECIMAL);
    registerDatatype(EMAIL_ADDRESS);
    registerDatatype(HOSTNAME);
    registerDatatype(INTEGER);
    registerDatatype(IP_V4_ADDRESS);
    registerDatatype(IP_V6_ADDRESS);
    registerDatatype(MARKUP_LINE);
    registerDatatype(MARKUP_MULTILINE);
    registerDatatype(NON_NEGATIVE_INTEGER);
    registerDatatype(POSITIVE_INTEGER);
    registerDatatype(STRING);
    registerDatatype(TOKEN);
    registerDatatype(URI);
    registerDatatype(URI_REFERENCE);
    registerDatatype(UUID);
    registerDatatype(YEAR_MONTH_DURATION);
    // aliases for legacy type names
    registerDatatypeByName("base64Binary", BASE64);
    registerDatatypeByName("dateTime", DATE_TIME);
    registerDatatypeByName("dateTime-with-timezone", DATE_TIME_WITH_TZ);
    registerDatatypeByName("email", EMAIL_ADDRESS);
    registerDatatypeByName("nonNegativeInteger", NON_NEGATIVE_INTEGER);
    registerDatatypeByName("positiveInteger", POSITIVE_INTEGER);

  }
}
