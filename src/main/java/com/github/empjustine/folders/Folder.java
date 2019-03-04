/*
 * Copyright 2019 Cássio Hideki Kanashiro Oseki
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
package com.github.empjustine.folders;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Unified synchronous and blocking folder interface for both local and
 * remote (SMB) folders.
 */
public interface Folder {

	/**
	 * @return non-recursive, shallow, flat list of folder contents.
	 * @throws IOException propagating upstream errors
	 */
	List<String> list() throws IOException;

	/**
	 * Opens a file for byte-level reading.
	 * <p>
	 * The {@code block} lambda should execute in a synchronously and
	 * blocking way, with a {@link InputStream} parameter.
	 * <p>
	 * Automatic resource management (try-with-resources) should happen once
	 * the {@code block} lambda ends.
	 * <p>
	 * The {@link InputStream} should read the file content.
	 * <p>
	 * @return the same type and value returned from the {@code block}
	 * lambda.
	 * @throws IOException propagating upstream errors
	 */
	<T> T withInputStream(@NotNull final String fileName, @NotNull final IOExceptionFunction<? super InputStream, T> block) throws IOException;

	/**
	 * Opens a file for byte-level writing.
	 * <p>
	 * The {@code block} lambda should execute in a synchronously and
	 * blocking way, with a {@link OutputStream} parameter.
	 * <p>
	 * Automatic resource management (try-with-resources) should happen once
	 * the {@code block} lambda ends.
	 * <p>
	 * The {@link OutputStream} should modify the file content.
	 * <p>
	 * @return the same type and value returned from the {@code block}
	 * lambda.
	 * @throws IOException propagating upstream errors
	 */
	<T> T withOutputStream(@NotNull final String fileName, @NotNull final IOExceptionFunction<? super OutputStream, T> block) throws IOException;

	/**
	 * Opens a file for {@link java.nio.charset.StandardCharsets#UTF_8}
	 * string writing.
	 * @throws IOException propagating upstream errors
	 */
	void write(@NotNull final String fileName, @NotNull final String payload) throws IOException;

	/**
	 * Remove a file from the folder.
	 * @throws IOException propagating upstream errors
	 */
	void remove(@NotNull final String fileName) throws IOException;
}
