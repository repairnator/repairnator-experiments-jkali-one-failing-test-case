/**
 * 
 */
package org.semanticweb.elk.benchmark;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AllFilesTaskCollection implements TaskCollection, VisitorTaskCollection {

	private final String[] args_;
	
	public AllFilesTaskCollection(String... args) {
		args_ = args;
	}
	
	@Override
	public Collection<Task> getTasks() throws TaskException {
		File dir = BenchmarkUtils.getFile(args_[0]);
		Collection<Task> tasks = new ArrayList<Task>();
		String[] taskArgs = new String[args_.length];
		
		System.arraycopy(args_, 0, taskArgs, 0, args_.length);
		
		File[] files = sortFiles(dir.listFiles());
		
		for (File file : files) {
			
			try {
				taskArgs[0] = file.getCanonicalPath();
			} catch (IOException e) {
				throw new TaskException(e);
			}
			
			tasks.add(instantiateSubTask(taskArgs));
		}
		
		return tasks;
	}

	protected File[] sortFiles(File[] listFiles) {
		return listFiles;
	}
	
	@Override
	public void dispose() {
	}
	
	@Override
	public Metrics getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void visitTasks(TaskVisitor visitor) throws TaskException {
		for (Task task : getTasks()) {
			visitor.visit(task);
		}
	}

	public abstract Task instantiateSubTask(String[] args) throws TaskException;
	
}
