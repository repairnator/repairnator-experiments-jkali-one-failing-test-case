package net.whydah.identity.user.identity;


import com.netflix.hystrix.exception.HystrixBadRequestException;

import net.whydah.sso.user.types.UserIdentity;

import org.constretto.annotation.Configuration;
import org.constretto.annotation.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.naming.*;
import javax.naming.directory.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * https://wiki.cantara.no/display/whydah/UserIdentity+LDAP+mapping
 */
@Repository
public class LdapUserIdentityDao {
    private static final Logger log = LoggerFactory.getLogger(LdapUserIdentityDao.class);
    static final String ATTRIBUTE_NAME_TEMPPWD_SALT = "destinationIndicator";
    /**
     * The OU (organizational unit) to add users to
     */
    private static final String USERS_OU = "ou=users";
    private static final String ATTRIBUTE_NAME_CN = "cn";
    private static final String ATTRIBUTE_NAME_SN = "sn";
    private static final String ATTRIBUTE_NAME_GIVENNAME = "givenName";
    private static final String ATTRIBUTE_NAME_MAIL = "mail";
    private static final String ATTRIBUTE_NAME_MOBILE = "mobile";
    private static final String ATTRIBUTE_NAME_PASSWORD = "userpassword";   //TODO Should this be userPassword?
    private static final String ATTRIBUTE_NAME_PERSONREF = "employeeNumber";

    private static final StringCleaner stringCleaner = new StringCleaner();

    private final Hashtable<String, String> admenv;
    private final String uidAttribute;
    private final String usernameAttribute;
    private final boolean readOnly;

    //public LdapUserIdentityDao(String ldapUrl, String admPrincipal, String admCredentials, String uidAttribute, String usernameAttribute, boolean readOnly) {

    @Autowired
    @Configure
    public LdapUserIdentityDao(@Configuration("ldap.primary.url") String primaryLdapUrl,
                             @Configuration("ldap.primary.admin.principal") String primaryAdmPrincipal,
                             @Configuration("ldap.primary.admin.credentials") String primaryAdmCredentials,
                             @Configuration("ldap.primary.uid.attribute") String primaryUidAttribute,
                             @Configuration("ldap.primary.username.attribute") String primaryUsernameAttribute,
                             @Configuration("ldap.primary.readonly") String readOnly) {
    /*
    public LdapUserIdentityDao(ConstrettoConfiguration configuration) {
        String primaryLdapUrl = configuration.evaluateToString("ldap.primary.url");
        String primaryAdmPrincipal = configuration.evaluateToString("ldap.primary.admin.principal");
        String primaryAdmCredentials = configuration.evaluateToString("ldap.primary.admin.credentials");
        String primaryUidAttribute = configuration.evaluateToString("ldap.primary.uid.attribute");
        String primaryUsernameAttribute = configuration.evaluateToString("ldap.primary.username.attribute");
        boolean readonly = configuration.evaluateToBoolean("ldap.primary.readonly");
    */
        admenv = new Hashtable<>(4);
        admenv.put(Context.PROVIDER_URL, primaryLdapUrl);
        admenv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        admenv.put(Context.SECURITY_PRINCIPAL, primaryAdmPrincipal);
        admenv.put(Context.SECURITY_CREDENTIALS, primaryAdmCredentials);
        this.uidAttribute = primaryUidAttribute;
        this.usernameAttribute = primaryUsernameAttribute;
        this.readOnly = Boolean.parseBoolean(readOnly);

    }

    public boolean addUserIdentity(LDAPUserIdentity userIdentity) throws NamingException {
        if (readOnly) {
            log.warn("addUserIdentity called, but LDAP server is configured read-only. LDAPUserIdentity was not added.");
            return false;
        }

       

        Attributes attributes = getLdapAttributes(userIdentity);

        // Create the entry
        String userdn = uidAttribute + '=' + userIdentity.getUid() + "," + USERS_OU;
        try {
            new CommandLdapCreateSubcontext(new InitialDirContext(admenv), userdn, attributes).execute();
            log.trace("Added {} with dn={}", userIdentity, userdn);
            return true;
        } catch(HystrixBadRequestException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoPermissionException) {
                log.error("addUserIdentity failed. Not allowed to add userdn={}. {} - ExceptionMessage: {}", userdn, userIdentity.toString(), cause.getMessage());
            } else if (cause instanceof NameAlreadyBoundException) {
                log.info("addUserIdentity failed, user already exists in LDAP: {}", userIdentity.toString());
            } else if (cause instanceof InvalidAttributeValueException) {
                InvalidAttributeValueException iave = (InvalidAttributeValueException) cause;
                StringBuilder strb = new StringBuilder("LDAP user with illegal state. ");
                strb.append(userIdentity.toString());
                if (log.isDebugEnabled()) {
                    strb.append("\n").append(iave);
                } else {
                    strb.append("ExceptionMessage: ").append(iave.getMessage());
                }
                log.warn(strb.toString());
            }
            return false;
        }
    }

    public boolean addUserIdentity(UserIdentity userIdentity, String password) throws NamingException {
        if (readOnly) {
            log.warn("addUserIdentity called, but LDAP server is configured read-only. LDAPUserIdentity was not added.");
            return false;
        }

        Attributes attributes = getLdapAttributes(userIdentity, password);

        // Create the entry
        String userdn = uidAttribute + '=' + userIdentity.getUid() + "," + USERS_OU;
        try {
            new CommandLdapCreateSubcontext(new InitialDirContext(admenv), userdn, attributes).execute();
            log.trace("Added {} with dn={}", userIdentity, userdn);
            return true;
        } catch (HystrixBadRequestException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoPermissionException) {
                log.error("addUserIdentity failed. Not allowed to add userdn={}. {} - ExceptionMessage: {}", userdn, userIdentity.toString(), cause.getMessage());
            } else if (cause instanceof NameAlreadyBoundException) {
                log.info("addUserIdentity failed, user already exists in LDAP: {}", userIdentity.toString());
            } else if (cause instanceof InvalidAttributeValueException) {
                InvalidAttributeValueException iave = (InvalidAttributeValueException) cause;
                StringBuilder strb = new StringBuilder("LDAP user with illegal state. ");
                strb.append(userIdentity.toString());
                if (log.isDebugEnabled()) {
                    strb.append("\n").append(iave);
                } else {
                    strb.append("ExceptionMessage: ").append(iave.getMessage());
                }
                log.warn(strb.toString());
            }
            return false;
        }
    }

    /**
     * Schemas: http://www.zytrax.com/books/ldap/ape/
     */
    private Attributes getLdapAttributes(LDAPUserIdentity userIdentity) {
        // Create a container set of attributes
        Attributes container = new BasicAttributes();
        // Create the objectclass to add
        Attribute objClasses = createObjClasses();
        container.put(objClasses);
        container.put(new BasicAttribute(ATTRIBUTE_NAME_CN, userIdentity.getPersonName()));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_GIVENNAME, userIdentity.getFirstName()));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_SN, userIdentity.getLastName()));
        container.put(new BasicAttribute(uidAttribute, stringCleaner.cleanString(userIdentity.getUid())));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_MAIL, userIdentity.getEmail()));
        container.put(new BasicAttribute(usernameAttribute, stringCleaner.cleanString(userIdentity.getUsername())));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_PASSWORD, userIdentity.getPassword()));



        if (userIdentity.getPersonRef() != null && userIdentity.getPersonRef().length() > 0) {
            container.put(new BasicAttribute(ATTRIBUTE_NAME_PERSONREF, stringCleaner.cleanString(userIdentity.getPersonRef())));
        }

        if (userIdentity.getCellPhone() != null && userIdentity.getCellPhone().length() > 2) {
            container.put(new BasicAttribute(ATTRIBUTE_NAME_MOBILE, stringCleaner.cleanString(userIdentity.getCellPhone())));
        }
        //container.put(new BasicAttribute(ATTRIBUTE_NAME_TEMPPWD_SALT, "TEMPPW"));
        return container;
    }

    /**
     * Schemas: http://www.zytrax.com/books/ldap/ape/
     */
    private Attributes getLdapAttributes(UserIdentity userIdentity, String password) {
        // Create a container set of attributes
        Attributes container = new BasicAttributes();
        // Create the objectclass to add
        Attribute objClasses = createObjClasses();
        container.put(objClasses);
        container.put(new BasicAttribute(ATTRIBUTE_NAME_CN, userIdentity.getPersonName()));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_GIVENNAME, userIdentity.getFirstName()));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_SN, userIdentity.getLastName()));
        container.put(new BasicAttribute(uidAttribute, stringCleaner.cleanString(userIdentity.getUid())));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_MAIL, userIdentity.getEmail()));
        container.put(new BasicAttribute(usernameAttribute, stringCleaner.cleanString(userIdentity.getUsername())));
        container.put(new BasicAttribute(ATTRIBUTE_NAME_PASSWORD, password));


        if (userIdentity.getPersonRef() != null && userIdentity.getPersonRef().length() > 0) {
            container.put(new BasicAttribute(ATTRIBUTE_NAME_PERSONREF, stringCleaner.cleanString(userIdentity.getPersonRef())));
        }

        if (userIdentity.getCellPhone() != null && userIdentity.getCellPhone().length() > 2) {
            container.put(new BasicAttribute(ATTRIBUTE_NAME_MOBILE, stringCleaner.cleanString(userIdentity.getCellPhone())));
        }
        //container.put(new BasicAttribute(ATTRIBUTE_NAME_TEMPPWD_SALT, "TEMPPW"));
        return container;
    }

    public void updateUserIdentityForUid(String uid, LDAPUserIdentity newuser) {
        if (readOnly) {
            log.warn("updateUserIdentityForUid called, but LDAP server is configured read-only. LDAPUserIdentity was not updated.");
            return;
        }

        try {
            LDAPUserIdentity olduser = getUserIndentityByUid(uid);
            updateLdapAttributesForUser(uid, newuser, olduser);
            log.debug("updateUserIdentityForUid updated LDAP - newuser={} olduser:{]", newuser, olduser);
        } catch (NamingException ne) {
            String msg = "updateUserIdentityForUid could not update LDAP. newUser=" + newuser.toString();
            log.error(msg, ne);
            //Should validate client input and return 4xx response.
            throw new IllegalArgumentException(msg, ne);
        }
    }

    private void updateLdapAttributesForUser(String uid, LDAPUserIdentity newuser, LDAPUserIdentity olduser) throws NamingException {
        if (olduser == null) {
            throw new IllegalArgumentException("User " + uid + " not found");
        }
        ArrayList<ModificationItem> modificationItems = new ArrayList<>(7);
        addModificationItem(modificationItems, ATTRIBUTE_NAME_CN, olduser.getPersonName(), newuser.getPersonName());
        addModificationItem(modificationItems, ATTRIBUTE_NAME_GIVENNAME, olduser.getFirstName(), newuser.getFirstName());
        addModificationItem(modificationItems, ATTRIBUTE_NAME_SN, olduser.getLastName(), newuser.getLastName());
        addModificationItem(modificationItems, ATTRIBUTE_NAME_MAIL, olduser.getEmail(), stringCleaner.cleanString(newuser.getEmail()));
        addModificationItem(modificationItems, ATTRIBUTE_NAME_MOBILE, olduser.getCellPhone(), newuser.getCellPhone());
        addModificationItem(modificationItems, ATTRIBUTE_NAME_PERSONREF, olduser.getPersonRef(), newuser.getPersonRef());
        addModificationItem(modificationItems, usernameAttribute, olduser.getUsername(), newuser.getUsername());

        try {
            new CommandLdapModifyAttributes(new InitialDirContext(admenv), createUserDNFromUID(newuser.getUid()), modificationItems.toArray(new ModificationItem[modificationItems.size()])).execute();
        } catch(HystrixBadRequestException he) {
            if (he.getCause() instanceof NamingException) {
                throw (NamingException) he.getCause();
            }
            throw he;
        }
    }

    public void updateUserIdentityForUsername(String username, LDAPUserIdentity newuser) {
        if (readOnly) {
            log.warn("updateUserIdentityForUsername called, but LDAP server is configured read-only. LDAPUserIdentity was not updated.");
            return;
        }

        try {
            LDAPUserIdentity olduser = getUserIndentity(username);
            updateLdapAttributesForUser(username, newuser, olduser);
        } catch (NamingException ne) {
            String msg = "updateUserIdentityForUsername could not update LDAP. newUser=" + newuser.toString();
            log.error(msg, ne);
            //Should validate client input and return 4xx response.
            throw new IllegalArgumentException(msg, ne);
        }
    }

    private void addModificationItem(ArrayList<ModificationItem> modificationItems, String attributeName, String oldValue, String newValue) {
        if ((oldValue != null && oldValue.equals(newValue)) || (oldValue == null && newValue == null)) {
            log.debug("Not changing " + attributeName + "=" + newValue);
        } else if (oldValue == null) {
            log.debug("Adding attribute " + attributeName + "=" + newValue);
            modificationItems.add(new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(attributeName, newValue)));
        } else if (newValue == null) {
            log.debug("Removing attribute '" + attributeName + "'");
            modificationItems.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(attributeName, oldValue)));
        } else {
            log.debug("Changing from " + attributeName + "=" + oldValue + " to " + newValue);
            modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attributeName, newValue)));
        }
    }

    private Attribute createObjClasses() {
        Attribute objClasses = new BasicAttribute("objectClass");
        objClasses.add("top");
        objClasses.add("person");
        objClasses.add("organizationalPerson");
        objClasses.add("inetOrgPerson");
        return objClasses;
    }

    private String createUserDNFromUsername(String username) throws NamingException {
        Attributes attributes = getUserAttributesForUsernameOrUid(username);
        if (attributes == null) {
            log.debug("createUserDNFromUsername failed (returned null), because could not find any Attributes for username={}", username);
            return null;
        }
        String uid = getAttribValue(attributes, uidAttribute);
        return createUserDNFromUID(uid);
    }

    private String createUserDNFromUID(String uid) throws NamingException {
        return uidAttribute + '=' + uid + "," + USERS_OU;
    }

    public LDAPUserIdentity getUserIndentity(String usernameOrUid) throws NamingException {
        Attributes attributes = getUserAttributesForUsernameOrUid(usernameOrUid);
        LDAPUserIdentity id = fromLdapAttributes(attributes);
        return id;
    }

    public LDAPUserIdentity getUserIndentityByUid(String uid) throws NamingException {
        Attributes attributes = getAttributesFor(uidAttribute, uid);
        LDAPUserIdentity id = fromLdapAttributes(attributes);
        return id;
    }


    private LDAPUserIdentity fromLdapAttributes(Attributes attributes) throws NamingException {
        if (attributes == null) {
            return null;
        }

        LDAPUserIdentity id = null;
        Attribute uidAttributeValue = attributes.get(uidAttribute);
        Attribute usernameAttributeValue = attributes.get(usernameAttribute);
        if (uidAttributeValue != null && usernameAttributeValue != null) {
            id = new LDAPUserIdentity();
            id.setUid((String) attributes.get(uidAttribute).get());
            id.setUsername((String) attributes.get(usernameAttribute).get());
            id.setFirstName(getAttribValue(attributes, ATTRIBUTE_NAME_GIVENNAME));
            id.setLastName(getAttribValue(attributes, ATTRIBUTE_NAME_SN));
            id.setEmail(getAttribValue(attributes, ATTRIBUTE_NAME_MAIL));
            id.setPersonRef(getAttribValue(attributes, ATTRIBUTE_NAME_PERSONREF));
            id.setCellPhone(getAttribValue(attributes, ATTRIBUTE_NAME_MOBILE));
        }
        return id;
    }

    public boolean usernameExist(String username) throws NamingException {
        return getUserIndentity(username) != null;
    }


    private Attributes getUserAttributesForUsernameOrUid(String usernameOrUid) throws NamingException {
        Attributes userAttributesForUsername = getAttributesFor(usernameAttribute, usernameOrUid);
        if (userAttributesForUsername != null) {
            return userAttributesForUsername;
        }

        log.debug("No attributes found for username=" + usernameOrUid + ", trying uid");
        return getAttributesFor(uidAttribute, usernameOrUid);
    }

    public List<LDAPUserIdentity> getAllUsers() throws NamingException{
    	SearchControls constraints = new SearchControls();
    	constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
    	NamingEnumeration objs = new InitialDirContext(admenv).search("","(objectClass=*)", constraints);
    	List<LDAPUserIdentity> list = new ArrayList<LDAPUserIdentity>();
    	while (objs.hasMore())
    	{
    		//Each item is a SearchResult object
    		SearchResult match = (SearchResult) objs.next();

    		//Print out the node name
    		//System.out.println("Found "+match.getName()+":");

    		//Get the node's attributes
    		Attributes attrs = match.getAttributes();

    		//NamingEnumeration e = attrs.getAll();

    		LDAPUserIdentity ldapUser = fromLdapAttributes(attrs);
    		
    		if(ldapUser!=null){
    			list.add(ldapUser);
    		}
    		
    		
    		//System.out.print(ldapUser);
    		
    		
    		/*
    		//Loop through the attributes
    		while (e.hasMoreElements())
    		{
    			//Get the next attribute
    			Attribute attr = (Attribute) e.nextElement();

    			//Print out the attribute's value(s)
    			System.out.print(attr.getID()+" = ");
    			for (int i=0; i < attr.size(); i++)
    			{
    				if (i > 0) System.out.print(", ");
    				System.out.print(attr.get(i));
    			}
    			System.out.println();
    		}
    		System.out.println("---------------------------------------");
    		*/
    	}
    	
    	return list;

    }
    
    private Attributes getAttributesFor(String attributeName, String attributeValue) throws NamingException {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        log.trace("getAttributesForUid using {}={}", attributeName, attributeValue);
        try {
            SearchResult searchResult = new CommandLdapSearch(new InitialDirContext(admenv), "", "(" + attributeName + "=" + attributeValue + ")", constraints).execute();
            if (searchResult == null) {
                log.trace("getAttributesForUid found no attributes for {}={}.", attributeName, attributeValue);
                return null;
            }
            return searchResult.getAttributes();
        } catch (HystrixBadRequestException he) {
            if (he.getCause() instanceof NamingException) {
                NamingException pre = (NamingException) he.getCause();
                if (pre instanceof PartialResultException) {
                    log.trace("Partial Search only. Due to speed optimization, full search in LDAP/AD is not enabled. {}: {}, PartialResultException: {}", attributeName, attributeValue, pre.getMessage());
                } else {
                    log.trace("NamingException. {}: {}", attributeName, attributeValue);
                    throw pre;
                }
            }
            throw he;
        }
    }

    private boolean hasResults(NamingEnumeration results) throws NamingException {
        boolean hasResults = false;
        if (results != null) {
            try {
                hasResults = results.hasMore();
            } catch (NamingException ne) {
                log.debug("NamingException trying to do results.hasMore(). Swallowing this exception.");
            }
        }
        return hasResults;
    }


    private String getAttribValue(Attributes attributes, String attributeName) throws NamingException {
        Attribute attribute = attributes.get(attributeName);
        if (attribute != null) {
            return (String) attribute.get();
        } else {
            return null;
        }
    }

    public boolean deleteUserIdentity(String username) {
        if (readOnly) {
            log.warn("deleteUserIdentity called, but LDAP server is configured read-only. username={} was not deleted.", username);
            return false;
        }

        log.info("deleteUserIdentity with username={}", username);
        String userDN;
        try {
            userDN = createUserDNFromUsername(username);
        } catch (NamingException ne) {
            log.error("deleteUserIdentity failed! Could not create user DN from username=" + username, ne);
            return false;
        }

        DirContext ctx;
        try {
            ctx = new InitialDirContext(admenv);
        } catch (NamingException e) {
            log.error("deleteUserIdentity failed! Could not create initial context.", e);
            return false;
        }
        try {
            new CommandLdapDestroySubcontext(ctx, userDN).execute();
            return true;
        } catch (HystrixBadRequestException he) {
            log.error("deleteUserIdentity failed! Could not destroy subcontext. userDN=" + userDN, he.getCause());
            return false;
        }
    }

    public void changePassword(String username, String newPassword) {
        if (readOnly) {
            log.warn("changePassword called, but LDAP server is configured read-only. Password was not changed for username={}", username);
            return;
        }

        Attributes attributes;
        try {
            attributes = getUserAttributesForUsernameOrUid(username);
        } catch (NamingException e) {
            log.error("Error when changing password. Could not get user attributes for username=", username, e);
            return;
        }

        String userDN;
        try {
            userDN = createUserDNFromUsername(username);
        } catch (NamingException e) {
            log.error("Error when changing password. Could not create user DN for username=", username, e);
            return;
        }

        DirContext ctx;
        try {
            ctx = new InitialDirContext(admenv);
        } catch (NamingException e) {
            log.error("Error when changing password. Could not create initial context", e);
            return;
        }

        if (attributes.get(ATTRIBUTE_NAME_TEMPPWD_SALT) != null) {
            ModificationItem mif = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(ATTRIBUTE_NAME_TEMPPWD_SALT));
            ModificationItem[] mis = {mif};
            try {
                new CommandLdapModifyAttributes(ctx, userDN, mis).execute();
            } catch (HystrixBadRequestException he) {
                log.error("Error when changing password. Could not remove attribute " + ATTRIBUTE_NAME_TEMPPWD_SALT + "for username=", username, he.getCause());
                return;
            }
        }

        ModificationItem mi = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(ATTRIBUTE_NAME_PASSWORD, newPassword));
        ModificationItem[] mis = {mi};
        try {
            new CommandLdapModifyAttributes(ctx, userDN, mis).execute();
        } catch(HystrixBadRequestException he) {
            log.error("Error when changing password. Could not replace password for username=", username, he.getCause());
            return;
        }

        log.trace("Password successfully changed for user with username={}", username);
    }


    public void setTempPassword(String username, String password, String salt) {
        if (readOnly) {
            log.warn("setTempPassword called, but LDAP server is configured read-only. TmpPassword was not set for username={}", username);
            return;
        }

        try {
            Attributes attributes = getUserAttributesForUsernameOrUid(username);
            String userDN = createUserDNFromUsername(username);
            DirContext ctx = new InitialDirContext(admenv);
            if (getAttribValue(attributes, ATTRIBUTE_NAME_TEMPPWD_SALT) == null) {
                ModificationItem mif = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(ATTRIBUTE_NAME_TEMPPWD_SALT, salt));
                ModificationItem[] mis = {mif};
                try {
                    new CommandLdapModifyAttributes(ctx, userDN, mis).execute();
                } catch(HystrixBadRequestException he) {
                    log.error("setTempPassword failed for username={} using admin principal={}. Attribute modification: ADD TEMPPWD_SALT.", username, getAdminPrincipal(), he.getCause());
                    return;
                }
            } else {
                ModificationItem mif = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(ATTRIBUTE_NAME_TEMPPWD_SALT, salt));
                ModificationItem[] mis = {mif};
                try {
                    new CommandLdapModifyAttributes(ctx, userDN, mis).execute();
                } catch(HystrixBadRequestException he) {
                    log.error("setTempPassword failed for username={} using admin principal={}. Attribute modification: REPLACE TEMPPWD_SALT.", username, getAdminPrincipal(), he.getCause());
                    return;
                }
            }
            if(password!=null && password.length()>0){
            	ModificationItem mip = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(ATTRIBUTE_NAME_PASSWORD, password));
            	ModificationItem[] mis = {mip};
            	try {
            		new CommandLdapModifyAttributes(ctx, userDN, mis).execute();
            	} catch(HystrixBadRequestException he) {
            		log.error("setTempPassword failed for username={} using admin principal={}. Attribute modification: REPLACE PASSWORD.", username, getAdminPrincipal(), he.getCause());
            		return;
            	}
            }
        } catch (NoPermissionException np) {
            log.error("setTempPassword failed for username={}, not sufficient access rights using admin principal={}. NoPermissionException msg={} ",
                    username, getAdminPrincipal(), np.getMessage());
        } catch (NamingException ne) {
            log.error("setTempPassword failed for username={} using admin principal={}. ", username, getAdminPrincipal(), ne);
        }
    }

    public String getSalt(String user) {
        try {
            Attributes attributes = getUserAttributesForUsernameOrUid(user);
            return getAttribValue(attributes, ATTRIBUTE_NAME_TEMPPWD_SALT);
        } catch (NamingException ne) {
            log.error("", ne);
        }
        return null;
    }

    private String getAdminPrincipal() {
        return admenv.get(Context.SECURITY_PRINCIPAL);
    }
}



