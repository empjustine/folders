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

	public Folder folder;

	@Before
	public void beforeEach() throws IOException {
		final File tmpFolder = this.temporaryFolder.newFolder();
		this.folder = new LocalFolder(tmpFolder.getAbsolutePath() + File.separator);
	}

	@Test
	public void testLocalList() throws IOException {
		final List<String> list = this.folder.list();
		Assert.assertThat(list, IsInstanceOf.instanceOf(List.class));
	}

	@Test
	public void testLocalVoid() throws IOException {
		this.folder.write("a", "aaa");
		Assert.assertEquals(this.folder.list().size(), 1);
		this.folder.remove("a");
		Assert.assertEquals(this.folder.list().size(), 0);
	}

	@Test
	public void testLocalStreams() throws IOException {
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
