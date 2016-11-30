/*
 * Copyright 2016 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.spotless;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import com.diffplug.common.collect.ImmutableSortedSet;
import com.diffplug.common.collect.Iterables;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/** Computes a signature for any needed files. */
public final class FileSignature implements Serializable {
	private static final long serialVersionUID = 1L;

	/*
	 * Transient because not needed to uniquely identify a FileSignature instance, and also because
	 * Gradle only needs this class to be Serializable so it can compare FileSignature instances for
	 * incremental builds.
	 */
	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private final transient ImmutableSortedSet<File> sortedFiles;

	private final String[] filenames;
	private final long[] filesizes;
	private final long[] lastModified;

	public FileSignature(File... files) throws IOException {
		this(Arrays.asList(files));
	}

	public FileSignature(Iterable<File> files) throws IOException {
		sortedFiles = ImmutableSortedSet.copyOf(files);

		filenames = new String[sortedFiles.size()];
		filesizes = new long[sortedFiles.size()];
		lastModified = new long[sortedFiles.size()];

		int i = 0;
		for (File file : sortedFiles) {
			filenames[i] = file.getCanonicalPath();
			filesizes[i] = file.length();
			lastModified[i] = file.lastModified();
			++i;
		}
	}

	/** Returns all of the files in this signature, throwing an exception if there are more or less than 1 file. */
	public ImmutableSortedSet<File> files() {
		return sortedFiles;
	}

	/** Returns the only file in this signature, throwing an exception if there are more or less than 1 file. */
	public File getOnlyFile() {
		return Iterables.getOnlyElement(sortedFiles);
	}
}