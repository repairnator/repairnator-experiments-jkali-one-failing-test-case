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
package com.helger.peppol.smpserver.rest;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.url.URLHelper;
import com.helger.peppol.identifier.generic.doctype.IDocumentTypeIdentifier;
import com.helger.peppol.identifier.generic.participant.IParticipantIdentifier;
import com.helger.peppol.smpserver.SMPServerConfiguration;
import com.helger.peppol.smpserver.restapi.ISMPServerAPIDataProvider;
import com.helger.servlet.StaticServerInfo;

/**
 * {@link ISMPServerAPIDataProvider} implementation based on {@link UriInfo}
 * data.
 *
 * @author Philip Helger
 */
public class SMPServerAPIDataProvider implements ISMPServerAPIDataProvider
{
  private final UriInfo m_aUriInfo;
  private final boolean m_bUseStaticServerInfo;

  public SMPServerAPIDataProvider (@Nonnull final UriInfo aUriInfo)
  {
    this (aUriInfo, true);
  }

  public SMPServerAPIDataProvider (@Nonnull final UriInfo aUriInfo, final boolean bUseStaticServerInfo)
  {
    m_aUriInfo = ValueEnforcer.notNull (aUriInfo, "UriInfo");
    m_bUseStaticServerInfo = bUseStaticServerInfo;
  }

  @Nonnull
  protected final UriInfo getUriInfo ()
  {
    return m_aUriInfo;
  }

  protected final boolean isUseStaticServerInfo ()
  {
    return m_bUseStaticServerInfo;
  }

  @Nonnull
  public URI getCurrentURI ()
  {
    if (m_bUseStaticServerInfo && StaticServerInfo.isSet ())
    {
      // Do not decode params - '#' lets URI parser fail!
      return URLHelper.getAsURI (StaticServerInfo.getInstance ().getFullContextPath () +
                                 "/" +
                                 m_aUriInfo.getPath (false));
    }
    if (false)
    {
      // First version in ROOT, second in context "/test"

      // http://127.0.0.1:90/
      // http://127.0.0.1:90/test/
      m_aUriInfo.getBaseUri ();
      // iso6523-actorid-upis:0088:5798000000112
      m_aUriInfo.getPath ();
      // http://127.0.0.1:90/iso6523-actorid-upis%3A0088%3A5798000000112
      // http://127.0.0.1:90/test/iso6523-actorid-upis%3A0088%3A5798000000112
      m_aUriInfo.getAbsolutePath ();
    }
    return m_aUriInfo.getAbsolutePath ();
  }

  /**
   * @return An UriBuilder that contains the full server name, port and context
   *         path!
   */
  @Nonnull
  protected UriBuilder getBaseUriBuilder ()
  {
    if (m_bUseStaticServerInfo && StaticServerInfo.isSet ())
    {
      return UriBuilder.fromUri (StaticServerInfo.getInstance ().getFullContextPath () + "/");
    }
    return m_aUriInfo.getBaseUriBuilder ();
  }

  @Nonnull
  public String getServiceGroupHref (@Nonnull final IParticipantIdentifier aServiceGroupID)
  {
    UriBuilder aBuilder = getBaseUriBuilder ();
    if (SMPServerConfiguration.isForceRoot ())
    {
      // Ensure that no context is emitted by using "replacePath" first!
      aBuilder = aBuilder.replacePath ("");
    }
    return aBuilder.path (ServiceGroupInterface.class)
                   .buildFromEncoded (aServiceGroupID.getURIPercentEncoded ())
                   .toString ();
  }

  @Nonnull
  public String getServiceMetadataReferenceHref (@Nonnull final IParticipantIdentifier aServiceGroupID,
                                                 @Nonnull final IDocumentTypeIdentifier aDocTypeID)
  {
    UriBuilder aBuilder = getBaseUriBuilder ();
    if (SMPServerConfiguration.isForceRoot ())
    {
      // Ensure that no context is emitted by using "replacePath" first!
      aBuilder = aBuilder.replacePath ("");
    }
    return aBuilder.path (ServiceMetadataInterface.class)
                   .buildFromEncoded (aServiceGroupID.getURIPercentEncoded (), aDocTypeID.getURIPercentEncoded ())
                   .toString ();
  }
}
