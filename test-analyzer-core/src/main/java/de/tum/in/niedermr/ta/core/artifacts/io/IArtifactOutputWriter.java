package de.tum.in.niedermr.ta.core.artifacts.io;

import java.io.IOException;
import java.util.List;

import de.tum.in.niedermr.ta.core.artifacts.content.ClassFileData;

public interface IArtifactOutputWriter {

	void ensureAllStreamsClosed() throws IOException;

	void writeClass(ClassFileData classFileData) throws IOException;

	void writeResource(ClassFileData resourceFileData) throws IOException;

	void writeClasses(List<ClassFileData> classFileList) throws IOException;
}