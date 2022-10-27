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

package gov.nist.secauto.metaschema.schemagen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.UsedDefinitionModelWalker;
import gov.nist.secauto.metaschema.model.common.configuration.IConfiguration;
import gov.nist.secauto.metaschema.model.common.util.ObjectUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaGenerator
    extends AbstractSchemaGenerator {
  @NonNull
  private final JsonFactory jsonFactory;

  public JsonSchemaGenerator() {
    this(new JsonFactory());
  }

  public JsonSchemaGenerator(@NonNull JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

  public JsonFactory getJsonFactory() {
    return jsonFactory;
  }

  @Override
  public void generateFromMetaschema(@NonNull IMetaschema metaschema, @NonNull Writer out,
      @NonNull IConfiguration<SchemaGenerationFeature> configuration) throws IOException {
    try (JsonGenerator jsonGenerator = getJsonFactory().createGenerator(out)) {
      jsonGenerator.setCodec(new ObjectMapper());
      jsonGenerator.useDefaultPrettyPrinter();

      generateSchemaMetadata(metaschema, jsonGenerator, configuration);

      jsonGenerator.flush();
    }
  }

  protected void generateSchemaMetadata(@NonNull IMetaschema metaschema, @NonNull JsonGenerator jsonGenerator,
      @NonNull IConfiguration<SchemaGenerationFeature> configuration)
      throws IOException {
    jsonGenerator.writeStartObject();

    jsonGenerator.writeStringField("$schema", "http://json-schema.org/draft-07/schema#");
    jsonGenerator.writeStringField("$id",
        String.format("%s/%s-%s-schema.json",
            metaschema.getXmlNamespace(),
            metaschema.getShortName(),
            metaschema.getVersion()));
    jsonGenerator.writeStringField("$comment", metaschema.getName().toMarkdown());
    jsonGenerator.writeStringField("type", "object");

    Collection<? extends IDefinition> definitions
        = UsedDefinitionModelWalker.collectUsedDefinitionsFromMetaschema(metaschema);

    IInlineStrategy inlineStrategy = newInlineStrategy(configuration, definitions);

    GenerationState state = new GenerationState(jsonGenerator, inlineStrategy);

    generateDefinitions(definitions, state);

    Set<IAssemblyDefinition> rootAssemblies = new LinkedHashSet<>();

    for (IDefinition definition : definitions) {
      if (definition instanceof IAssemblyDefinition) {
        IAssemblyDefinition assemblyDefinition = (IAssemblyDefinition) definition;
        if (assemblyDefinition.isRoot()) {
          rootAssemblies.add(assemblyDefinition);
        }
      }
    }

    if (!rootAssemblies.isEmpty()) {
      generateRootProperties(rootAssemblies, state);
    }
    jsonGenerator.writeEndObject();
  }

  protected void generateDefinitions(
      @NonNull Collection<? extends IDefinition> definitions,
      @NonNull GenerationState state) throws IOException {
    if (!definitions.isEmpty()) {
      JsonGenerator writer = state.getWriter(); // NOPMD not closable here

      ObjectNode definitionsObject = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());

      for (IDefinition definition : definitions) {
        assert definition != null;
        if (!state.isInline(definition)) {
          JsonDefinitionGenerator.generateDefinition(definition, definitionsObject, state);
        }
      }

      // write datatypes
      state.getDatatypeManager().generateDatatypes(definitionsObject);

      if (!definitionsObject.isEmpty()) {
        writer.writeFieldName("definitions");
        writer.writeTree(definitionsObject);
      }
    }
  }

  protected void generateRootProperties(
      @NonNull Set<IAssemblyDefinition> rootAssemblies,
      @NonNull GenerationState state) throws IOException {
    JsonGenerator writer = state.getWriter(); // NOPMD not closable here
    // generate root properties
    writer.writeFieldName("properties");
    writer.writeStartObject();

    writer.writeFieldName("$schema");
    writer.writeStartObject();
    writer.writeStringField("type", "string");
    writer.writeStringField("format", "uri-reference");
    writer.writeEndObject();

    for (IAssemblyDefinition root : rootAssemblies) {
      writer.writeFieldName(root.getRootJsonName());
      writer.writeStartObject();
      writer.writeStringField("$ref", state.getDatatypeManager().getJsonDefinitionRefForDefinition(root, state));
      writer.writeEndObject();
    }
    writer.writeEndObject();

    // generate root requires
    if (rootAssemblies.size() == 1) {
      @SuppressWarnings("null")
      @NonNull
      IAssemblyDefinition root = rootAssemblies.iterator().next();
      generateRootRequired(root, writer);
    } else {
      writer.writeFieldName("oneOf");
      writer.writeStartArray();

      for (IAssemblyDefinition root : rootAssemblies) {
        assert root != null;
        writer.writeStartObject();
        generateRootRequired(root, writer);
        writer.writeEndObject();
      }

      writer.writeEndArray();
    }

    writer.writeBooleanField("additionalProperties", false);
  }

  protected void generateRootRequired(@NonNull IAssemblyDefinition root, @NonNull JsonGenerator jsonGenerator)
      throws IOException {
    jsonGenerator.writeFieldName("required");
    jsonGenerator.writeStartArray();
    jsonGenerator.writeString(root.getRootJsonName());
    jsonGenerator.writeEndArray();

  }

  public static class GenerationState
      extends AbstractGenerationState<JsonGenerator, JsonDatatypeManager> {

    public GenerationState(@NonNull JsonGenerator writer, @NonNull IInlineStrategy inlineStrategy) {
      super(writer, new JsonDatatypeManager(), inlineStrategy);
    }
  }
}
