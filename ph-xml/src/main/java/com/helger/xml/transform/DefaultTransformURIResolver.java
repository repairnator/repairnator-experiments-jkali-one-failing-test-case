/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.xml.transform;

import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceresolver.DefaultResourceResolver;
import com.helger.xml.ls.SimpleLSResourceResolver;

/**
 * Implementation of the {@link javax.xml.transform.URIResolver} interface using
 * {@link SimpleLSResourceResolver} to resolve resources.
 *
 * @author Philip Helger
 */
public class DefaultTransformURIResolver extends AbstractTransformURIResolver
{
  public DefaultTransformURIResolver ()
  {
    super ();
  }

  public DefaultTransformURIResolver (@Nullable final URIResolver aWrappedURIResolver)
  {
    super (aWrappedURIResolver);
  }

  @Override
  @Nullable
  protected Source internalResolve (final String sHref, final String sBase) throws TransformerException
  {
    try
    {
      final IReadableResource aRes = DefaultResourceResolver.getResolvedResource (sHref, sBase);
      if (aRes != null && aRes.exists ())
        return TransformSourceFactory.create (aRes);
    }
    catch (final Exception ex)
    {
      throw new TransformerException (sHref + "//" + sBase, ex);
    }

    // Nothing to resolve
    return null;
  }
}
