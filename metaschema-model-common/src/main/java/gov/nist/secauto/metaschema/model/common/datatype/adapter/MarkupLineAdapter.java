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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import gov.nist.secauto.metaschema.model.common.datatype.markup.AbstractMarkupString;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupXmlEventWriter;
import gov.nist.secauto.metaschema.model.common.datatype.markup.MarkupXmlStreamWriter;
import gov.nist.secauto.metaschema.model.common.metapath.item.IMarkupItem;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEventFactory2;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class MarkupLineAdapter
    extends AbstractMarkupAdapter<MarkupLine> {

  @SuppressWarnings("null")
  public MarkupLineAdapter() {
    super(MarkupLine.class);
  }

  @Override
  public String getName() {
    return "markup-line";
  }

  @Override
  public MarkupLine parse(String value) {
    return MarkupLine.fromMarkdown(value);
  }

  @SuppressWarnings("null")
  @Override
  public MarkupLine parse(XMLEventReader2 eventReader) throws IOException {
    try {
      return getMarkupParser().parseMarkupline(eventReader);
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public MarkupLine parse(JsonParser parser) throws IOException {
    @SuppressWarnings("null")
    MarkupLine retval = parse(parser.getValueAsString());
    // skip past value
    parser.nextToken();
    return retval;
  }

  // TODO: verify that read/write methods cannot be generalized in the base class
  @Override
  public void writeXml(Object value, StartElement parent, XMLEventFactory2 eventFactory, XMLEventWriter eventWriter)
      throws XMLStreamException {
    MarkupXmlEventWriter writingVisitor
        = new MarkupXmlEventWriter(parent.getName().getNamespaceURI(), false, eventFactory);
    writingVisitor.visitChildren(((AbstractMarkupString<?>) value).getDocument(), eventWriter);
  }

  @Override
  public void writeXmlCharacters(Object value, QName parentName, XMLStreamWriter2 writer) throws XMLStreamException {
    MarkupXmlStreamWriter writingVisitor = new MarkupXmlStreamWriter(parentName.getNamespaceURI(), false);
    writingVisitor.visitChildren(((AbstractMarkupString<?>) value).getDocument(), writer);
  }

  @Override
  public void writeJsonValue(Object value, JsonGenerator generator) throws IOException {

    MarkupLine ml;
    try {
      ml = (MarkupLine) value;
    } catch (ClassCastException ex) {
      throw new IOException(ex);
    }

    String jsonString;
    if (generator instanceof YAMLGenerator) {
      jsonString = ml.toMarkdownYaml().trim();
    } else {
      jsonString = ml.toMarkdown().trim();
    }
    generator.writeString(jsonString);
  }

  @Override
  public String getDefaultJsonValueKey() {
    return "RICHTEXT";
  }

  @SuppressWarnings("null")
  @Override
  public @NotNull Class<IMarkupItem> getItemClass() {
    return IMarkupItem.class;
  }

  @Override
  public IMarkupItem newItem(Object value) {
    MarkupLine item = toValue(value);
    return IMarkupItem.valueOf(item);
  }
}
