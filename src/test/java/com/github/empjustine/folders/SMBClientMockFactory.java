package com.github.empjustine.folders;

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
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class SMBClientMockFactory {

	private final SMBClient smbClient = Mockito.mock(SMBClient.class);
	private final Session session = Mockito.mock(Session.class);
	private final Connection connection = Mockito.mock(Connection.class);
	private final DiskShare diskShare = Mockito.mock(DiskShare.class);
	private final FileIdBothDirectoryInformation fileIdBothDirectoryInformation = Mockito.mock(FileIdBothDirectoryInformation.class);
	private final List<FileIdBothDirectoryInformation> fileIdBothDirectoryInformations = Collections.singletonList(this.fileIdBothDirectoryInformation);
	private final File file = Mockito.mock(File.class);

	public SMBClientMockFactory() {
	}

	public void restart() {
		Mockito.reset(
			this.smbClient,
			this.session,
			this.connection,
			this.diskShare,
			this.fileIdBothDirectoryInformation,
			this.file
		);

		try {
			Mockito.doReturn(this.connection).when(this.smbClient).connect(Matchers.anyString());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		Mockito.doReturn(this.session).when(this.connection).authenticate(Matchers.any(AuthenticationContext.class));
		Mockito.doReturn(this.diskShare).when(this.session).connectShare(Matchers.anyString());
		Mockito.doReturn(this.fileIdBothDirectoryInformations).when(this.diskShare).list(Matchers.anyString());
		Mockito.doReturn("AAA_BBB_CCC.TXT").when(this.fileIdBothDirectoryInformation).getFileName();
		Mockito.doReturn(this.file).when(this.diskShare).openFile(
			Matchers.anyString(),
			Matchers.anySetOf(AccessMask.class),
			Matchers.anySetOf(FileAttributes.class),
			Matchers.anySetOf(SMB2ShareAccess.class),
			Matchers.any(SMB2CreateDisposition.class),
			Matchers.anySetOf(SMB2CreateOptions.class)
		);
		Mockito.doReturn(new ByteArrayInputStream("wow".getBytes(StandardCharsets.UTF_8))).when(this.file).getInputStream();
		Mockito.doReturn(new ByteArrayOutputStream()).when(this.file).getOutputStream();
	}

	public SMBClient getSmbClient() {
		return this.smbClient;
	}

	public DiskShare getDiskShare() {
		return this.diskShare;
	}
}

