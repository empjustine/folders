package com.github.empjustine.folders;

import com.github.empjustine.folders.impl.RemoteFolder;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.share.DiskShare;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class RemoteFolderTest {

	final SMBClientMockFactory smbClientMockFactory = new SMBClientMockFactory();
	final SMBClient smbClient = this.smbClientMockFactory.getSmbClient();
	Folder folder;

	@Before
	public void beforeEach() {
		this.smbClientMockFactory.restart();
		this.folder = new RemoteFolder("testHostname", "testDiskShare", "testPath\\", AuthenticationContext.guest(), this.smbClient);
	}

	@Test
	public void testRemoteList() throws IOException {
		final List<String> list = this.folder.list();
		Assert.assertThat(list, IsInstanceOf.instanceOf(List.class));
	}

	@Test
	public void testRemoteVoid() throws IOException {
		this.folder.write("a", "aaa");
		final DiskShare diskShare = this.smbClientMockFactory.getDiskShare();
		Mockito.verify(diskShare, Mockito.times(1)).openFile(
			Mockito.anyString(),
			Mockito.anySetOf(AccessMask.class),
			Mockito.anySetOf(FileAttributes.class),
			Mockito.anySetOf(SMB2ShareAccess.class),
			Mockito.any(SMB2CreateDisposition.class),
			Mockito.anySetOf(SMB2CreateOptions.class)
		);
		this.folder.remove("a");
		Mockito.verify(diskShare, Mockito.times(1)).rm(Mockito.anyString());
	}

	@Test
	public void testRemoteStreams() throws IOException {
		this.folder.withOutputStream("a", outputStream -> {
			Assert.assertThat(outputStream, IsInstanceOf.instanceOf(OutputStream.class));
			return null;
		});

		this.folder.withInputStream("a", inputStream -> {
			Assert.assertThat(inputStream, IsInstanceOf.instanceOf(InputStream.class));
			return null;
		});
	}
}
