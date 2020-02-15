package edu.wisc.my.messages.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Predicate over User, answering whether the User is or is not in the Audience. <p> Currently
 * AudienceFilter only knows how to consider whether a User is a member of at least one of the
 * required groups for a message. Conceptually other varieties of AudienceFilters might be
 * possible.
 */
public class MessageFilter
  implements Predicate<User> {

  @JsonProperty("goLiveDate")
  private String goLiveDate = null;
  
  @JsonProperty("expireDate")
  private String expireDate = null;

  @JsonProperty("groups")
  private List<String> groups = null;

  public MessageFilter groups(List<String> groups) {
    this.groups = groups;
    return this;
  }

  public MessageFilter addGroupsItem(String groupsItem) {
    if(null == this.groups) {
      this.groups = new ArrayList<String>();
    }
    this.groups.add(groupsItem);
    return this;
  }

  public List<String> getGroups() {
    return groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public String getGoLiveDate() {
    return goLiveDate;
  }

  public void setGoLiveDate(String goLiveDate) {
    this.goLiveDate = goLiveDate;
  }

  public String getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(String expireDate) {
    this.expireDate = expireDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageFilter audienceFilter = (MessageFilter) o;
    return Objects.equals(this.groups, audienceFilter.groups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groups);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AudienceFilter {\n");

    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first
   * line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  @Override
  public boolean test(User user) {
    // If this.groups is null, then no group filter was ever specified,
    // therefore this message is intended for a universal and unfiltered
    // audience - HOWEVER - if there is an array of groups, and that array
    // is empty, then a filter was specified and the creator of this message
    // intended to specify showing this message to no groups.
    if(null == this.groups) {
      return true;
    }

    if(this.groups.size() == 0) {
      return false;
    }
    
    Set<String> requireAtLeastOneOfTheseGroups = new HashSet<>();
    requireAtLeastOneOfTheseGroups.addAll(this.groups);
    requireAtLeastOneOfTheseGroups.retainAll(user.getGroups());

    return (!requireAtLeastOneOfTheseGroups.isEmpty());
  }
}
