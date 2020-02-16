
package com.google.errorprone.bugpatterns;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.sun.tools.javac.code.Symbol;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_StaticImports_StaticImportInfo extends StaticImports.StaticImportInfo {

  private final String importedName;
  private final String canonicalName;
  private final Optional<String> simpleName;
  private final ImmutableSet<Symbol> members;

  AutoValue_StaticImports_StaticImportInfo(
      String importedName,
      String canonicalName,
      Optional<String> simpleName,
      ImmutableSet<Symbol> members) {
    if (importedName == null) {
      throw new NullPointerException("Null importedName");
    }
    this.importedName = importedName;
    if (canonicalName == null) {
      throw new NullPointerException("Null canonicalName");
    }
    this.canonicalName = canonicalName;
    if (simpleName == null) {
      throw new NullPointerException("Null simpleName");
    }
    this.simpleName = simpleName;
    if (members == null) {
      throw new NullPointerException("Null members");
    }
    this.members = members;
  }

  @Override
  public String importedName() {
    return importedName;
  }

  @Override
  public String canonicalName() {
    return canonicalName;
  }

  @Override
  public Optional<String> simpleName() {
    return simpleName;
  }

  @Override
  public ImmutableSet<Symbol> members() {
    return members;
  }

  @Override
  public String toString() {
    return "StaticImportInfo{"
         + "importedName=" + importedName + ", "
         + "canonicalName=" + canonicalName + ", "
         + "simpleName=" + simpleName + ", "
         + "members=" + members
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof StaticImports.StaticImportInfo) {
      StaticImports.StaticImportInfo that = (StaticImports.StaticImportInfo) o;
      return (this.importedName.equals(that.importedName()))
           && (this.canonicalName.equals(that.canonicalName()))
           && (this.simpleName.equals(that.simpleName()))
           && (this.members.equals(that.members()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.importedName.hashCode();
    h *= 1000003;
    h ^= this.canonicalName.hashCode();
    h *= 1000003;
    h ^= this.simpleName.hashCode();
    h *= 1000003;
    h ^= this.members.hashCode();
    return h;
  }

}
