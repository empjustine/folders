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
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Folder} for SMB shared folders.
 */
public class RemoteFolder implements Folder {
	private static final Set<AccessMask> ACCESS_MASK_READ = EnumSet.of(AccessMask.FILE_READ_DATA);
	private static final Set<AccessMask> ACCESS_MASK_WRITE = EnumSet.of(AccessMask.GENERIC_WRITE);
	private static final Set<FileAttributes> FILE_ATTRIBUTE_NORMAL = EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL);
	private static final Set<SMB2CreateOptions> CREATE_OPTIONS_EMPTY = EnumSet.noneOf(SMB2CreateOptions.class);

	private static final Set<SMB2ShareAccess> SHARE_ACCESS_READ = EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ);
	private static final Set<SMB2ShareAccess> SHARE_ACCESS_WRITE = EnumSet.of(SMB2ShareAccess.FILE_SHARE_WRITE);
	private static final SMB2CreateDisposition CREATE_DISPOSITION_READ = SMB2CreateDisposition.FILE_OPEN;
	private static final SMB2CreateDisposition CREATE_DISPOSITION_WRITE = SMB2CreateDisposition.FILE_OVERWRITE_IF;

	private final String hostname;
	private final String diskShare;
	private final String path;
	private final AuthenticationContext authenticationContext;
	private final SMBClient client;

	public RemoteFolder(
		final String hostname,
		final String diskShare,
		final String path,
		final AuthenticationContext authenticationContext,
		final SMBClient client
	) {
		super();

		this.hostname = hostname;
		this.diskShare = diskShare;
		this.path = path;
		this.authenticationContext = authenticationContext;
		this.client = client;
	}

	public List<String> list() throws IOException {
		final String fullPath = this.path;
		return this.withDiskShare(
			share1 -> share1.list(fullPath)
				.stream()
				.map(FileIdBothDirectoryInformation::getFileName)
				.collect(Collectors.toList())
		);
	}

	public <T> T withInputStream(@NotNull final String fileName, @NotNull final IOExceptionFunction<? super InputStream, T> block) throws IOException {
		final String fullPath = this.path + fileName;
		return this.withDiskShare(share -> {
				try (
					final File file = share.openFile(
						fullPath,
						ACCESS_MASK_READ,
						FILE_ATTRIBUTE_NORMAL,
						SHARE_ACCESS_READ,
						CREATE_DISPOSITION_READ,
						CREATE_OPTIONS_EMPTY
					);
					final InputStream inputStream = file.getInputStream()
				) {
					return block.apply(inputStream);
				}
			}
		);
	}

	public <T> T withOutputStream(@NotNull final String fileName, @NotNull final IOExceptionFunction<? super OutputStream, T> block) throws IOException {
		final String fullPath = this.path + fileName;
		return this.withDiskShare(share -> {
			try (
				final File file = share.openFile(
					fullPath,
					ACCESS_MASK_WRITE,
					FILE_ATTRIBUTE_NORMAL,
					SHARE_ACCESS_WRITE,
					CREATE_DISPOSITION_WRITE,
					CREATE_OPTIONS_EMPTY
				);
				final OutputStream outputStream = file.getOutputStream()
			) {
				return block.apply(outputStream);
			}
		});
	}

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

	public void remove(@NotNull final String fileName) throws IOException {
		final String fullPath = this.path + fileName;
		this.withDiskShare(share -> {
			share.rm(fullPath);
			return null;
		});
	}

	private <T> T withDiskShare(@NotNull final IOExceptionFunction<? super DiskShare, T> block) throws IOException {
		try (
			final Connection connection = this.client.connect(this.hostname);
			final Session session = connection.authenticate(this.authenticationContext);
			final DiskShare share = (DiskShare) session.connectShare(this.diskShare)
		) {
			return block.apply(share);
		}
	}
}
