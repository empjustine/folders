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
package com.github.empjustine.folders.impl;

import com.github.empjustine.folders.Folder;
import com.github.empjustine.folders.IOExceptionFunction;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalFolder implements Folder {

	private final String path;

	public LocalFolder(final String path) {
		this.path = path;
	}

	@Override
	public List<String> list() throws IOException {
		final Path fullPath = Paths.get(this.path);
		try (Stream<Path> list = Files.list(fullPath)) {
			return list.map(Path::getFileName)
				.map(String::valueOf)
				.collect(Collectors.toList());
		}
	}

	@Override
	public <T> T withInputStream(@NotNull final String fileName, @NotNull final IOExceptionFunction<? super InputStream, T> block) throws IOException {
		final String fullPath = path + fileName;
		try (FileInputStream inputStream = new FileInputStream(fullPath)) {
			return block.apply(inputStream);
		}
	}

	@Override
	public <T> T withOutputStream(@NotNull final String fileName, @NotNull final IOExceptionFunction<? super OutputStream, T> block) throws IOException {
		final String fullPath = path + fileName;
		try (FileOutputStream outputStream = new FileOutputStream(fullPath)) {
			return block.apply(outputStream);
		}
	}

	@Override
	public void write(@NotNull final String fileName, @NotNull final String payload) throws IOException {
		this.withOutputStream(fileName, outputStream -> {
			try (
				final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				final BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
			) {
				bufferedWriter.write(payload);
			}
			return null;
		});
	}

	@Override
	public void remove(@NotNull final String fileName) throws IOException {
		final Path fullPath = Paths.get(this.path + fileName);
		Files.delete(fullPath);
	}
}
