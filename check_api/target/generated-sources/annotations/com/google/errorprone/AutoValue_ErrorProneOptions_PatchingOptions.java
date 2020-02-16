
package com.google.errorprone;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.errorprone.apply.ImportOrganizer;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
 final class AutoValue_ErrorProneOptions_PatchingOptions extends ErrorProneOptions.PatchingOptions {

  private final Set<String> namedCheckers;
  private final boolean inPlace;
  private final String baseDirectory;
  private final Optional<Supplier<CodeTransformer>> customRefactorer;
  private final ImportOrganizer importOrganizer;

  private AutoValue_ErrorProneOptions_PatchingOptions(
      Set<String> namedCheckers,
      boolean inPlace,
      String baseDirectory,
      Optional<Supplier<CodeTransformer>> customRefactorer,
      ImportOrganizer importOrganizer) {
    this.namedCheckers = namedCheckers;
    this.inPlace = inPlace;
    this.baseDirectory = baseDirectory;
    this.customRefactorer = customRefactorer;
    this.importOrganizer = importOrganizer;
  }

  @Override
  Set<String> namedCheckers() {
    return namedCheckers;
  }

  @Override
  boolean inPlace() {
    return inPlace;
  }

  @Override
  String baseDirectory() {
    return baseDirectory;
  }

  @Override
  Optional<Supplier<CodeTransformer>> customRefactorer() {
    return customRefactorer;
  }

  @Override
  ImportOrganizer importOrganizer() {
    return importOrganizer;
  }

  @Override
  public String toString() {
    return "PatchingOptions{"
         + "namedCheckers=" + namedCheckers + ", "
         + "inPlace=" + inPlace + ", "
         + "baseDirectory=" + baseDirectory + ", "
         + "customRefactorer=" + customRefactorer + ", "
         + "importOrganizer=" + importOrganizer
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ErrorProneOptions.PatchingOptions) {
      ErrorProneOptions.PatchingOptions that = (ErrorProneOptions.PatchingOptions) o;
      return (this.namedCheckers.equals(that.namedCheckers()))
           && (this.inPlace == that.inPlace())
           && (this.baseDirectory.equals(that.baseDirectory()))
           && (this.customRefactorer.equals(that.customRefactorer()))
           && (this.importOrganizer.equals(that.importOrganizer()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= this.namedCheckers.hashCode();
    h *= 1000003;
    h ^= this.inPlace ? 1231 : 1237;
    h *= 1000003;
    h ^= this.baseDirectory.hashCode();
    h *= 1000003;
    h ^= this.customRefactorer.hashCode();
    h *= 1000003;
    h ^= this.importOrganizer.hashCode();
    return h;
  }

  static final class Builder extends ErrorProneOptions.PatchingOptions.Builder {
    private Set<String> namedCheckers;
    private Boolean inPlace;
    private String baseDirectory;
    private Optional<Supplier<CodeTransformer>> customRefactorer = Optional.absent();
    private ImportOrganizer importOrganizer;
    Builder() {
    }
    @Override
    ErrorProneOptions.PatchingOptions.Builder namedCheckers(Set<String> namedCheckers) {
      if (namedCheckers == null) {
        throw new NullPointerException("Null namedCheckers");
      }
      this.namedCheckers = namedCheckers;
      return this;
    }
    @Override
    ErrorProneOptions.PatchingOptions.Builder inPlace(boolean inPlace) {
      this.inPlace = inPlace;
      return this;
    }
    @Override
    ErrorProneOptions.PatchingOptions.Builder baseDirectory(String baseDirectory) {
      if (baseDirectory == null) {
        throw new NullPointerException("Null baseDirectory");
      }
      this.baseDirectory = baseDirectory;
      return this;
    }
    @Override
    ErrorProneOptions.PatchingOptions.Builder customRefactorer(Supplier<CodeTransformer> customRefactorer) {
      if (customRefactorer == null) {
        throw new NullPointerException("Null customRefactorer");
      }
      this.customRefactorer = Optional.of(customRefactorer);
      return this;
    }
    @Override
    ErrorProneOptions.PatchingOptions.Builder importOrganizer(ImportOrganizer importOrganizer) {
      if (importOrganizer == null) {
        throw new NullPointerException("Null importOrganizer");
      }
      this.importOrganizer = importOrganizer;
      return this;
    }
    @Override
    ErrorProneOptions.PatchingOptions autoBuild() {
      String missing = "";
      if (this.namedCheckers == null) {
        missing += " namedCheckers";
      }
      if (this.inPlace == null) {
        missing += " inPlace";
      }
      if (this.baseDirectory == null) {
        missing += " baseDirectory";
      }
      if (this.importOrganizer == null) {
        missing += " importOrganizer";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_ErrorProneOptions_PatchingOptions(
          this.namedCheckers,
          this.inPlace,
          this.baseDirectory,
          this.customRefactorer,
          this.importOrganizer);
    }
  }

}
