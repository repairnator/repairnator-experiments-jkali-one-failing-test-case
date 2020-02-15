package cz.metacentrum.perun.core.impl.modules.attributes;

import cz.metacentrum.perun.core.api.Attribute;
import cz.metacentrum.perun.core.api.AttributeDefinition;
import cz.metacentrum.perun.core.api.AttributesManager;
import cz.metacentrum.perun.core.api.Group;
import cz.metacentrum.perun.core.api.exceptions.InternalErrorException;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeAssignmentException;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeValueException;
import cz.metacentrum.perun.core.api.exceptions.WrongReferenceAttributeValueException;
import cz.metacentrum.perun.core.impl.PerunSessionImpl;
import cz.metacentrum.perun.core.implApi.modules.attributes.GroupAttributesModuleAbstract;
import cz.metacentrum.perun.core.implApi.modules.attributes.GroupAttributesModuleImplApi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Group vomsRoles attribute.
 *
 * @author Vojtech Sassmann &lt;vojtech.sassmann@gmail.com&gt;
 */
public class urn_perun_group_attribute_def_def_vomsRoles extends GroupAttributesModuleAbstract implements GroupAttributesModuleImplApi {

	private final Pattern pattern = Pattern.compile("^[^<>&]*$");

	@Override
	@SuppressWarnings("unchecked")
	public void checkAttributeValue(PerunSessionImpl perunSession, Group group, Attribute attribute) throws InternalErrorException, WrongAttributeValueException, WrongReferenceAttributeValueException, WrongAttributeAssignmentException {
		if(attribute.getValue() == null) {
			return;
		}
		try {
			List<String> vomRoles = (List<String>) attribute.getValue();
			for (String vomRole : vomRoles) {
				Matcher matcher = pattern.matcher(vomRole);
				if(!matcher.matches()) {
					throw new WrongAttributeValueException(attribute, "Bad group vomsRoles value. It should not contain '<>&' characters.");
				}
			}
		} catch (ClassCastException e) {
			throw new WrongAttributeValueException(attribute, "Value should be a list of Strings.");
		}
	}

	@Override
	public AttributeDefinition getAttributeDefinition() {
		AttributeDefinition attr = new AttributeDefinition();
		attr.setFriendlyName("vomsRoles");
		attr.setDisplayName("Voms roles");
		attr.setDescription("Voms roles");
		attr.setType(ArrayList.class.getName());
		attr.setNamespace(AttributesManager.NS_GROUP_ATTR_DEF);
		return attr;
	}
}
