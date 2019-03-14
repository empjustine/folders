package com.github.empjustine.folders;

import com.github.empjustine.folders.impl.RemoteFolder;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class RemoteFolderTest {

	final SMBClientMockFactory smbClientMockFactory = new SMBClientMockFactory();
	final SMBClient smbClient = smbClientMockFactory.getSmbClient();
	Folder folder;

	@Before
	public void beforeEach() {
		this.smbClientMockFactory.restart();
		this.folder = new RemoteFolder("testHostname", "testDiskShare", "testPath\\", AuthenticationContext.guest(), smbClient);
	}

	@Test
	public void testRemoteList() throws IOException {
		final List<String> list = this.folder.list();
		Assert.assertThat(list, IsInstanceOf.instanceOf(List.class));
	}

	@Test
	public void testRemoteVoid() throws IOException {
		this.folder.write("a", "aaa");
		this.folder.remove("a");
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
