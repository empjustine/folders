package com.github.empjustine.folders;

import com.github.empjustine.folders.impl.LocalFolder;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class LocalFolderTest {

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Folder folder;

	@Before
	public void beforeEach() throws IOException {
		final File tmpFolder = temporaryFolder.newFolder();
		this.folder = new LocalFolder(tmpFolder.getAbsolutePath() + "\\");
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
