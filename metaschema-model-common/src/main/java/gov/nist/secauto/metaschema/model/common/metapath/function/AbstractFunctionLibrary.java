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

package gov.nist.secauto.metaschema.model.common.metapath.function;

import gov.nist.secauto.metaschema.model.common.metapath.ast.IExpression;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class AbstractFunctionLibrary implements IFunctionLibrary {

  @NotNull
  private final HashMap<@NotNull String, NamedFunctionSet> library = new HashMap<>();

  /**
   * Get the map of function name to function signatures.
   * 
   * @return the mapping
   */
  @NotNull
  protected HashMap<@NotNull String, NamedFunctionSet> getLibrary() {
    return library;
  }

  /**
   * Register the provided function signature.
   * 
   * @param function
   *          the function signature to register
   * @throws IllegalArgumentException
   *           if the provided function has the same arity as a previously registered function with
   *           the same name
   */
  public synchronized void registerFunction(@NotNull IFunction function) throws IllegalArgumentException {
    String name = function.getName();

    NamedFunctionSet functions = getLibrary().get(name);
    if (functions == null) {
      functions = new NamedFunctionSet();
      library.put(name, functions);
    }
    IFunction duplicate = functions.addFunction(function);
    if (duplicate != null) {
      throw new IllegalArgumentException(String.format("Duplicate functions with same arity: %s shadows %s",
          duplicate.toSignature(), function.toSignature()));
    }
  }

  @Override
  public Stream<@NotNull IFunction> getFunctionsAsStream() {
    return getLibrary().values().stream().flatMap(set -> {
      return set.getFunctionsAsStream();
    });
  }

  @Override
  public synchronized boolean hasFunction(@NotNull String name, @NotNull List<@NotNull IExpression> args) {
    return getFunction(name, args) != null;
  }

  @Override
  public synchronized IFunction getFunction(@NotNull String name, @NotNull List<@NotNull IExpression> args) {
    NamedFunctionSet functions = getLibrary().get(name);
    IFunction retval;
    if (functions == null) {
      retval = null;
    } else {
      retval = functions.getFunctionWithArity(args.size());
    }
    return retval;
  }

  private static class NamedFunctionSet {
    private final Map<Integer, IFunction> arityToFunctionMap;

    public NamedFunctionSet() {
      this.arityToFunctionMap = new HashMap<>();
    }

    @SuppressWarnings("null")
    @NotNull
    public Stream<@NotNull IFunction> getFunctionsAsStream() {
      return arityToFunctionMap.values().stream();
    }

    public IFunction getFunctionWithArity(int arity) {
      return arityToFunctionMap.get(arity);
    }

    public IFunction addFunction(@NotNull IFunction function) {
      return arityToFunctionMap.put(function.arity(), function);
    }
  }
}
