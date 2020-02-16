/*
   Copyright 2013 Nationale-Nederlanden

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
package nl.nn.adapterframework.jms;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.nn.adapterframework.util.LogUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Singleton that has the different jmsRealms.   <br/>
 * Typical use: JmsRealmFactory.getInstance().&lt;method to execute&gt;
 * <br/>
 * @author Johan Verrips IOS
 * @see JmsRealm
 */
public class JmsRealmFactory {
	private Logger log;


    private static JmsRealmFactory self = null;
    private Hashtable jmsRealms = new Hashtable();

    /**
     * Private constructor to prevent breaking of the singleton pattern
     */
    private JmsRealmFactory() {
        super();
        log = LogUtil.getLogger(this);

    }
    /**
     * Get a hold of the singleton JmsRealmFactory
     */
    public static synchronized JmsRealmFactory getInstance() {
        if (self == null) {
            self = new JmsRealmFactory();
        }
        return (self);

    }
    /**
     * Get a realm by name
     * @return JmsRealm the requested realm or null if none was found under that name
     */
    public JmsRealm getJmsRealm(String jmsRealmName) {
        JmsRealm jmsRealm = (JmsRealm) jmsRealms.get(jmsRealmName);
        if (jmsRealm==null) {
            log.error("no JmsRealm found under name ["+jmsRealmName+"], factory contents ["+toString()+"]");
        }
        return jmsRealm;
    }
    /**
     * Get the realmnames as an Iterator, alphabetically sorted
     * @return Iterator with the realm names, alphabetically sorted
     */
    public Iterator getRegisteredRealmNames() {
        SortedSet sortedKeys = new TreeSet(jmsRealms.keySet());
        return sortedKeys.iterator();
    }
    /**
     * Get the names as a list
     * @return List with the realm names
     */
    public List<String> getRegisteredRealmNamesAsList() {
        Iterator it = getRegisteredRealmNames();
        List result = new ArrayList();
        while (it.hasNext()) {
            result.add((String) it.next());
        }
        return result;
    }
    /**
     * register a Realm
     * @param jmsRealm
     */
    public void registerJmsRealm(JmsRealm jmsRealm) {
        jmsRealms.put(jmsRealm.getRealmName(), jmsRealm);
        log.debug("JmsRealmFactory registered realm [" + jmsRealm.toString() + "]");
    }
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }

	public String getFirstDatasourceJmsRealm() {
		Iterator it = getRegisteredRealmNames();
		while (it.hasNext()) {
			String jr = (String) it.next();
			if (StringUtils.isNotEmpty(getJmsRealm(jr).getDatasourceName())) {
				return jr;
			}
		}
		return null;
	}

    public List getRegisteredDatasourceRealmNamesAsList() {
        Iterator it = getRegisteredRealmNames();
        List result = new ArrayList();
        while (it.hasNext()) {
			String jr = (String) it.next();
			if (StringUtils.isNotEmpty(getJmsRealm(jr).getDatasourceName())) {
	        	result.add(jr);
			}
        }
        return result;
    }
}
