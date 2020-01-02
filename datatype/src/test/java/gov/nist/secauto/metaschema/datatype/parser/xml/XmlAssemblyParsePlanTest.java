package gov.nist.secauto.metaschema.datatype.parser.xml;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.codehaus.stax2.XMLEventReader2;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.auto.Auto;
import org.jmock.auto.Mock;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import gov.nist.secauto.metaschema.datatype.parser.BindingException;
import gov.nist.secauto.metaschema.datatype.parser.xml.XmlFieldParsePlanTest.Value;

class XmlAssemblyParsePlanTest {
	private static final String NS = "namespace";
	private static final QName OBJECT_QNAME = new QName(NS, "object");
	private static final QName CHILD1_QNAME = new QName(NS, "child1");
	private static final QName P_QNAME = new QName(NS, "p");
	private static final String ATTRIBUTE_A_LOCAL_NAME = "a";
	private static final String ATTRIBUTE_B_LOCAL_NAME = "b";

	private static final String OBJECT_START_ELEMENT = OBJECT_QNAME.getLocalPart() + "-start-element";
	private static final String CHILD1_START_ELEMENT = CHILD1_QNAME.getLocalPart() + "-start-element";
	private static final String CHILD1_END_ELEMENT = CHILD1_QNAME.getLocalPart() + "-end-element";
	private static final String P_START_ELEMENT = P_QNAME.getLocalPart() + "-start-element";
	private static final String CHARACTERS = "characters";
	private static final String P_END_ELEMENT = P_QNAME.getLocalPart() + "-end-element";
	private static final String OBJECT_END_ELEMENT = OBJECT_QNAME.getLocalPart() + "-end-element";

	@Mock
	private StartElement OBJECT_START_ELEMENT_EVENT;
	@Mock
	private EndElement OBJECT_END_ELEMENT_EVENT;
	@Mock
	private StartElement CHILD1_START_ELEMENT_EVENT;
	@Mock
	private EndElement CHILD1_END_ELEMENT_EVENT;
	@Mock
	private StartElement P_START_ELEMENT_EVENT;
	@Mock
	private EndElement P_END_ELEMENT_EVENT;
	@Mock
	private Characters CHARACTERS_EVENT;

	@RegisterExtension
	JUnit5Mockery context = new JUnit5Mockery();
	@Auto
	private States readerState;

	@Mock
	private XmlParser xmlParser;
	@Mock
	private XMLEventReader2 reader;

	@Mock
	FieldValueXmlPropertyParser fieldValueXmlPropertyParser;

	@Test
	void testModelWithAttributes() throws BindingException, XMLStreamException {

		Attribute attributeA = context.mock(Attribute.class, "attributeA");
		Attribute attributeB = context.mock(Attribute.class, "attributeB");
		List<Attribute> attributes = new LinkedList<>();
		attributes.add(attributeA);
		attributes.add(attributeB);

		XmlAttributePropertyParser attributeParserA = context.mock(XmlAttributePropertyParser.class,
				"XmlAttributePropertyParserA");
		XmlAttributePropertyParser attributeParserB = context.mock(XmlAttributePropertyParser.class,
				"XmlAttributePropertyParserB");

		XmlObjectPropertyParser child1Parser = context.mock(XmlObjectPropertyParser.class,
				"XmlObjectPropertyParser-child1");

		XmlObjectPropertyParser markupMultilineParser = context.mock(XmlObjectPropertyParser.class,
				"XmlObjectPropertyParser-MarkupMultiline");

		readerState.startsAs(OBJECT_START_ELEMENT);
		Sequence parseStream = context.sequence("parseStream");

		context.checking(new Expectations() {
			{
				MockXmlSupport.mockElementXMLEvent(this, OBJECT_START_ELEMENT_EVENT, OBJECT_END_ELEMENT_EVENT,
						OBJECT_QNAME);
				MockXmlSupport.mockElementXMLEvent(this, CHILD1_START_ELEMENT_EVENT, CHILD1_END_ELEMENT_EVENT,
						CHILD1_QNAME);
				MockXmlSupport.mockElementXMLEvent(this, P_START_ELEMENT_EVENT, P_END_ELEMENT_EVENT,
						P_QNAME);

				MockXmlSupport.mockAttributeXMLEvent(this, attributeA, new QName(ATTRIBUTE_A_LOCAL_NAME), CHARACTERS);
				MockXmlSupport.mockAttributeXMLEvent(this, attributeB, new QName(ATTRIBUTE_B_LOCAL_NAME), CHARACTERS);
				MockXmlSupport.mockCharactersXMLEvent(this, CHARACTERS_EVENT, CHARACTERS);

				oneOf(OBJECT_START_ELEMENT_EVENT).getAttributes();
				will(returnValue(attributes.iterator()));

				// setup reader peeking behavior based on states
				allowing(reader).peek();
				will(returnValue(OBJECT_START_ELEMENT_EVENT));
				when(readerState.is(OBJECT_START_ELEMENT));

				allowing(reader).peek();
				will(returnValue(CHILD1_START_ELEMENT_EVENT));
				when(readerState.is(CHILD1_START_ELEMENT));

				allowing(reader).peek();
				will(returnValue(CHILD1_END_ELEMENT_EVENT));
				when(readerState.is(CHILD1_END_ELEMENT));

				allowing(reader).peek();
				will(returnValue(P_START_ELEMENT_EVENT));
				when(readerState.is(P_START_ELEMENT));

				allowing(reader).peek();
				will(returnValue(P_END_ELEMENT_EVENT));
				when(readerState.is(P_END_ELEMENT));

				allowing(reader).peek();
				will(returnValue(CHARACTERS_EVENT));
				when(readerState.is(CHARACTERS));

				allowing(reader).peek();
				will(returnValue(OBJECT_END_ELEMENT_EVENT));
				when(readerState.is(OBJECT_END_ELEMENT));

				// setup parsers
				allowing(child1Parser).canConsume(with(equal(CHILD1_QNAME)));
				will(returnValue(true));
				allowing(child1Parser).isChildWrappedInXml();
				will(returnValue(true));
				
				allowing(markupMultilineParser).canConsume(with(equal(P_QNAME)));
				will(returnValue(true));
				allowing(markupMultilineParser).isChildWrappedInXml();
				will(returnValue(false));

				/*
				 * the parsing sequence
				 */
				/*
				 * <object a="characters" b="characters">
				 *   <child1>characters</child1>
				 *   <p>characters</p>
				 * </object>
				 */
//				// parsing object START_ELEMENT
				oneOf(reader).nextEvent();
				will(returnValue(OBJECT_START_ELEMENT_EVENT));
				then(readerState.is(CHILD1_START_ELEMENT));
				inSequence(parseStream);

				// parse the attributes
				oneOf(attributeParserA).parse(with(any(Value.class)), with(same(attributeA)));
				inSequence(parseStream);
				oneOf(attributeParserB).parse(with(any(Value.class)), with(same(attributeB)));
				inSequence(parseStream);

				// Parse child1
				// transition to end element state to mimick parsing
				oneOf(child1Parser).parse(with(any(Value.class)), with(same(reader)));
				then(readerState.is(P_START_ELEMENT));
				inSequence(parseStream);
//
//				// move past child1 end element
//				oneOf(reader).nextEvent();
//				will(returnValue(CHILD1_END_ELEMENT_EVENT));
//				then(readerState.is(P_START_ELEMENT));
//				inSequence(parseStream);

				// Parse the MarkupMultiline
				// transition to end element state to mimick parsing
				oneOf(markupMultilineParser).parse(with(any(Value.class)), with(same(reader)));
				then(readerState.is(OBJECT_END_ELEMENT));
				inSequence(parseStream);

				// parse the field value
//				oneOf(fieldValueXmlPropertyParser).parse(with(any(Value.class)), with(same(reader)));
//				inSequence(parseStream);
//				// this simulates parsing
//				then(readerState.is(OBJECT_END_ELEMENT));

				// end of document
				oneOf(reader).nextEvent();
				inSequence(parseStream);
			}
		});

		Map<QName, XmlAttributePropertyParser> attributeParsers = new LinkedHashMap<>();
		attributeParsers.put(attributeA.getName(), attributeParserA);
		attributeParsers.put(attributeB.getName(), attributeParserB);

		List<XmlObjectPropertyParser> modelParsers = new LinkedList<>();
		modelParsers.add(child1Parser);
		modelParsers.add(markupMultilineParser);

		AssemblyXmlParsePlan<Value> parsePlan = new AssemblyXmlParsePlan<Value>(xmlParser, Value.class,
				attributeParsers, modelParsers);

		parsePlan.parse(reader);

		context.assertIsSatisfied();
	}

	public static class Value {
		public Value() {

		}
	}
}
