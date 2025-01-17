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

package gov.nist.secauto.metaschema.model.common.datatype.markup;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;

import gov.nist.secauto.metaschema.model.common.datatype.IDatatype;
import gov.nist.secauto.metaschema.model.common.datatype.markup.flexmark.AstCollectingVisitor;
import gov.nist.secauto.metaschema.model.common.datatype.markup.flexmark.FlexmarkFactory;
import gov.nist.secauto.metaschema.model.common.datatype.markup.flexmark.InsertVisitor;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.MergedNsContext;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

public abstract class AbstractMarkupString<TYPE extends AbstractMarkupString<TYPE>>
    implements IMarkupText, IDatatype<TYPE> {
  private static final String DEFAULT_HTML_NS = "http://www.w3.org/1999/xhtml";
  private static final String DEFAULT_HTML_PREFIX = "";
  @NotNull
  private final Document document;

  //
  // public AbstractMarkupString() {
  // this(null);
  // }

  public AbstractMarkupString(@NotNull Document document) {
    this.document = document;
  }

  @Override
  public Document getDocument() {
    return document;
  }

  @Override
  public void toHtmlAsStream(@NotNull XMLStreamWriter2 xmlStreamWriter, String namespace)
      throws XMLStreamException {
    MarkupXmlStreamWriter writingVisitor
        = new MarkupXmlStreamWriter(namespace, this instanceof MarkupMultiline);
    writingVisitor.visitChildren(getDocument(), xmlStreamWriter);
    xmlStreamWriter.flush();
  }

  @Override
  public void toHtmlAsStream(OutputStream os, String namespace, String prefix) throws XMLStreamException {
    XMLOutputFactory2 factory = (XMLOutputFactory2) WstxOutputFactory.newInstance();
    factory.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_STRUCTURE, false);
    XMLStreamWriter2 xmlStreamWriter = (XMLStreamWriter2) factory.createXMLStreamWriter(os);

    String effectiveNamespace = namespace == null ? DEFAULT_HTML_NS : namespace;
    String effectivePrefix = prefix == null ? DEFAULT_HTML_PREFIX : prefix;
    NamespaceContext nsContext = MergedNsContext.construct(xmlStreamWriter.getNamespaceContext(),
        List.of(NamespaceEventImpl.constructNamespace(null, effectivePrefix, effectiveNamespace)));
    xmlStreamWriter.setNamespaceContext(nsContext);

    toHtmlAsStream(xmlStreamWriter, effectiveNamespace);
  }

  @Override
  public String toHtml() {
    return toHTML(FlexmarkFactory.instance().getHtmlRenderer());
  }

  @NotNull
  protected String toHTML(HtmlRenderer renderer) {
    return renderer.render(getDocument());
  }

  @Override
  public String toMarkdown() {
    return toMarkdown(FlexmarkFactory.instance().getFormatter());
  }

  @NotNull
  @Override
  public String toMarkdown(Formatter formatter) {
    return formatter.render(getDocument());
  }

  @Override
  public String toMarkdownYaml() {
    return toMarkdownYaml(FlexmarkFactory.instance().getFormatter());
  }

  @NotNull
  protected String toMarkdownYaml(Formatter formatter) {
    String markdown = formatter.render(getDocument());
    // markdown = markdown.replaceAll("\\n", "\n");
    // markdown = markdown.replaceAll("\\r", "\r");
    return markdown;
  }

  @SuppressWarnings("null")
  @Override
  public Stream<Node> getNodesAsStream() {
    return Stream.concat(Stream.of(getDocument()),
        StreamSupport.stream(getDocument().getDescendants().spliterator(), false));
  }

  @Override
  public List<gov.nist.secauto.metaschema.model.common.datatype.markup.flexmark.InsertAnchorNode> getInserts(
      @NotNull Predicate<gov.nist.secauto.metaschema.model.common.datatype.markup.flexmark.InsertAnchorNode> filter) {
    InsertVisitor visitor = new InsertVisitor(filter);
    visitor.visitChildren(getDocument());
    return visitor.getInserts();
  }

  @Override
  public String toString() {
    AstCollectingVisitor visitor = new AstCollectingVisitor();
    visitor.collect(getDocument());
    return visitor.getAst();
  }
}
