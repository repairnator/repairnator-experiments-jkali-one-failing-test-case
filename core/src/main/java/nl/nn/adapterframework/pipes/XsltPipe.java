/*
   Copyright 2013, 2016 Nationale-Nederlanden

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package nl.nn.adapterframework.pipes;

import java.io.IOException;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.core.PipeStartException;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.parameters.ParameterResolutionContext;
import nl.nn.adapterframework.util.DomBuilderException;
import nl.nn.adapterframework.util.TransformerPool;
import nl.nn.adapterframework.util.XmlUtils;


/**
 * Perform an XSLT transformation with a specified stylesheet.
 *
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.pipes.XsltPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setMaxThreads(int) maxThreads}</td><td>maximum number of threads that may call {@link #doPipe(java.lang.Object, nl.nn.adapterframework.core.IPipeLineSession)} simultaneously</td><td>0 (unlimited)</td></tr>
 * <tr><td>{@link #setDurationThreshold(long) durationThreshold}</td><td>if durationThreshold >=0 and the duration (in milliseconds) of the message processing exceeded the value specified the message is logged informatory</td><td>-1</td></tr>
 * <tr><td>{@link #setGetInputFromSessionKey(String) getInputFromSessionKey}</td><td>when set, input is taken from this session key, instead of regular input</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setStoreResultInSessionKey(String) storeResultInSessionKey}</td><td>when set, the result is stored under this session key</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setNamespaceAware(boolean) namespaceAware}</td><td>controls namespace-awareness of transformation</td><td>application default</td></tr>
 * <tr><td>{@link #setStyleSheetName(String) styleSheetName}</td><td>stylesheet to apply to the input message</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setXpathExpression(String) xpathExpression}</td><td>XPath-expression to apply to the input message. It's possible to refer to a parameter (which e.g. contains a value from a sessionKey) by using the parameter name prefixed with $</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setNamespaceDefs(String) namespaceDefs}</td><td>namespace defintions for xpathExpression. Must be in the form of a comma or space separated list of <code>prefix=namespaceuri</code>-definitions</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setOutputType(String) outputType}</td><td>either 'text' or 'xml'. Only valid for xpathExpression</td><td>text</td></tr>
 * <tr><td>{@link #setOmitXmlDeclaration(boolean) omitXmlDeclaration}</td><td>force the transformer generated from the XPath-expression to omit the xml declaration</td><td>true</td></tr>
 * <tr><td>{@link #setSessionKey(String) sessionKey}</td><td>If specified, the result is put 
 * in the PipeLineSession under the specified key, and the result of this pipe will be 
 * the same as the input (the xml). If NOT specified, the result of the xpath expression 
 * will be the result of this pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setSkipEmptyTags(boolean) skipEmptyTags}</td><td>when set <code>true</code> empty tags in the output are removed</td><td>false</td></tr>
 * <tr><td>{@link #setIndentXml(boolean) indentXml}</td><td>when set <code>true</code>, result is pretty-printed. (only used when <code>skipEmptyTags="true"</code>)</td><td>true</td></tr>
 * <tr><td>{@link #setRemoveNamespaces(boolean) removeNamespaces}</td><td>when set <code>true</code> namespaces (and prefixes) in the input message are removed</td><td>false</td></tr>
 * <tr><td>{@link #setXslt2(boolean) xslt2}</td><td>when set <code>true</code> XSLT processor 2.0 (net.sf.saxon) will be used, otherwise XSLT processor 1.0 (org.apache.xalan)</td><td>false</td></tr>
 * </table>
 * <table border="1">
 * <tr><th>nested elements</th><th>description</th></tr>
 * <tr><td>{@link nl.nn.adapterframework.parameters.Parameter param}</td><td>any parameters defined on the pipe will be applied to the created transformer</td></tr>
 * </table>
 * </p>
 * <p><b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td><i>{@link #setForwardName(String) forwardName}</i></td><td>if specified</td></tr>
 * </table>
 * </p>
 * @author Johan Verrips
 */

public class XsltPipe extends FixedForwardPipe {

	private TransformerPool transformerPool;
	private String xpathExpression=null;
	private String namespaceDefs = null; 
	private String outputType="text";
	private String styleSheetName;
	private boolean omitXmlDeclaration=true;
	private boolean indentXml=true;
	private String sessionKey=null;
	private boolean skipEmptyTags=false;
	private boolean removeNamespaces=false;
	private boolean xslt2=false;

	private TransformerPool transformerPoolSkipEmptyTags;
	private TransformerPool transformerPoolRemoveNamespaces;

	{
		setSizeStatistics(true);
	}
	
	/**
	 * The <code>configure()</code> method instantiates a transformer for the specified
	 * XSL. If the stylesheetname cannot be accessed, a ConfigurationException is thrown.
	 * @throws ConfigurationException
	 */
	@Override
	public void configure() throws ConfigurationException {
	    super.configure();
	
		transformerPool = TransformerPool.configureTransformer0(getLogPrefix(null), classLoader, getNamespaceDefs(), getXpathExpression(), getStyleSheetName(), getOutputType(), !isOmitXmlDeclaration(), getParameterList(), isXslt2());
		if (isSkipEmptyTags()) {
			String skipEmptyTags_xslt = XmlUtils.makeSkipEmptyTagsXslt(isOmitXmlDeclaration(),isIndentXml());
			log.debug("test [" + skipEmptyTags_xslt + "]");
			try {
				transformerPoolSkipEmptyTags = TransformerPool.getInstance(skipEmptyTags_xslt);
			} catch (TransformerConfigurationException te) {
				throw new ConfigurationException(getLogPrefix(null) + "got error creating transformer from skipEmptyTags", te);
			}
		}
		if (isRemoveNamespaces()) {
			String removeNamespaces_xslt = XmlUtils.makeRemoveNamespacesXslt(isOmitXmlDeclaration(),isIndentXml());
			log.debug("test [" + removeNamespaces_xslt + "]");
			try {
				transformerPoolRemoveNamespaces = TransformerPool.getInstance(removeNamespaces_xslt);
			} catch (TransformerConfigurationException te) {
				throw new ConfigurationException(getLogPrefix(null) + "got error creating transformer from removeNamespaces", te);
			}
		}

		if (isXslt2()) {
			ParameterList parameterList = getParameterList();
			for (int i=0; i<parameterList.size(); i++) {
				Parameter parameter = parameterList.getParameter(i);
				if (StringUtils.isNotEmpty(parameter.getType()) && "node".equalsIgnoreCase(parameter.getType())) {
					throw new ConfigurationException(getLogPrefix(null) + "type \"node\" is not permitted in combination with XSLT 2.0, use type \"domdoc\"");
				}
			}
		}
	}

	@Override
	public void start() throws PipeStartException {
		super.start();
		if (transformerPoolRemoveNamespaces!=null) {
			try {
				transformerPoolRemoveNamespaces.open();
			} catch (Exception e) {
				throw new PipeStartException(getLogPrefix(null)+"cannot start TransformerPool RemoveNamespaces", e);
			}
		}
		if (transformerPool!=null) {
			try {
				transformerPool.open();
			} catch (Exception e) {
				throw new PipeStartException(getLogPrefix(null)+"cannot start TransformerPool", e);
			}
		}
		if (transformerPoolSkipEmptyTags!=null) {
			try {
				transformerPoolSkipEmptyTags.open();
			} catch (Exception e) {
				throw new PipeStartException(getLogPrefix(null)+"cannot start TransformerPool SkipEmptyTags", e);
			}
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		if (transformerPoolRemoveNamespaces!=null) {
			transformerPoolRemoveNamespaces.close();
		}
		if (transformerPool!=null) {
			transformerPool.close();
		}
		if (transformerPoolSkipEmptyTags!=null) {
			transformerPoolSkipEmptyTags.close();
		}
	}
	
	protected ParameterResolutionContext getInput(String input, IPipeLineSession session) throws PipeRunException, DomBuilderException, TransformerException, IOException {
		if (isRemoveNamespaces()) {
			log.debug(getLogPrefix(session)+ " removing namespaces from input message");
			ParameterResolutionContext prc_RemoveNamespaces = new ParameterResolutionContext(input, session, isNamespaceAware()); 
			input = transformerPoolRemoveNamespaces.transform(prc_RemoveNamespaces.getInputSource(), null); 
			log.debug(getLogPrefix(session)+ " output message after removing namespaces [" + input + "]");
		}
		return new ParameterResolutionContext(input, session, isNamespaceAware(), isXslt2());
	}

	protected String transform(TransformerPool tp, Source source, Map parametervalues) throws TransformerException, IOException {
		return tp.transform(source, parametervalues);
	}
	/**
	 * Here the actual transforming is done. Under weblogic the transformer object becomes
	 * corrupt when a not-well formed xml was handled. The transformer is then re-initialized
	 * via the configure() and start() methods.
	 */
	@Override
	public PipeRunResult doPipe(Object input, IPipeLineSession session) throws PipeRunException {
		if (input==null) {
			throw new PipeRunException(this,
				getLogPrefix(session)+"got null input");
		}
 	    if (!(input instanceof String)) {
	        throw new PipeRunException(this,
	            getLogPrefix(session)+"got an invalid type as input, expected String, got "
	                + input.getClass().getName());
	    }
		String stringResult =(String) input;

		//ParameterResolutionContext prc = new ParameterResolutionContext((String)input, session, isNamespaceAware()); 
	    try {

			ParameterResolutionContext prc = getInput(stringResult, session);
			Map parametervalues = null;
			ParameterList parameterList = getParameterList();
			if (parameterList!=null) {
				parametervalues = prc.getValueMap(parameterList);
			}
			
	        //stringResult = transformerPool.transform(prc.getInputSource(), parametervalues); 
	        stringResult = transform(transformerPool,prc.getInputSource(), parametervalues);

			if (isSkipEmptyTags()) {
				log.debug(getLogPrefix(session)+ " skipping empty tags from result [" + stringResult + "]");
				//URL xsltSource = ClassUtils.getResourceURL( this, skipEmptyTags_xslt);
				//Transformer transformer = XmlUtils.createTransformer(xsltSource);
				//stringResult = XmlUtils.transformXml(transformer, stringResult);
				ParameterResolutionContext prc_SkipEmptyTags = new ParameterResolutionContext(stringResult, session, true, true); 
				stringResult = transformerPoolSkipEmptyTags.transform(prc_SkipEmptyTags.getInputSource(), null); 
			}

			if (StringUtils.isEmpty(getSessionKey())){
				return new PipeRunResult(getForward(), stringResult);
			}
			session.put(getSessionKey(), stringResult);
			return new PipeRunResult(getForward(), input);
	    } 
	    catch (Exception e) {
	        throw new PipeRunException(this, getLogPrefix(session)+" Exception on transforming input", e);
	    } 
	}

	/**
	 * Specify the stylesheet to use
	 */
	public void setStyleSheetName(String stylesheetName){
		this.styleSheetName=stylesheetName;
	}
	public String getStyleSheetName() {
		return styleSheetName;
	}

	/**
	 * set the "omit xml declaration" on the transfomer. Defaults to true.
	 */
	public void setOmitXmlDeclaration(boolean b) {
		omitXmlDeclaration = b;
	}
	public boolean isOmitXmlDeclaration() {
		return omitXmlDeclaration;
	}


	public void setXpathExpression(String string) {
		xpathExpression = string;
	}
	public String getXpathExpression() {
		return xpathExpression;
	}

	public void setNamespaceDefs(String namespaceDefs) {
		this.namespaceDefs = namespaceDefs;
	}
	public String getNamespaceDefs() {
		return namespaceDefs;
	}

	/**
	 * The name of the key in the <code>PipeLineSession</code> to store the input in
	 * @see nl.nn.adapterframework.core.IPipeLineSession
	 */
	public void setSessionKey(String newSessionKey) {
		sessionKey = newSessionKey;
	}
	public String getSessionKey() {
		return sessionKey;
	}


	public void setOutputType(String string) {
		outputType = string;
	}
	public String getOutputType() {
		return outputType;
	}


	public void setSkipEmptyTags(boolean b) {
		skipEmptyTags = b;
	}
	public boolean isSkipEmptyTags() {
		return skipEmptyTags;
	}

	public void setIndentXml(boolean b) {
		indentXml = b;
	}
	public boolean isIndentXml() {
		return indentXml;
	}

	public void setRemoveNamespaces(boolean b) {
		removeNamespaces = b;
	}
	public boolean isRemoveNamespaces() {
		return removeNamespaces;
	}

	public boolean isXslt2() {
		return xslt2;
	}

	public void setXslt2(boolean b) {
		xslt2 = b;
	}
}
