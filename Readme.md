# Folders

Unified interface to handle folders, local or remote.

## Getting Started

To add `Folders` as a dependency, in your `pom.xml`:

```xml
	<dependencies>
		<dependency>
			<groupId>com.github.empjustine</groupId>
			<artifactId>folders</artifactId>
			<version>1.0</version>
		</dependency>
	</dependencies>
```

### Examples

#### Connect to a folder, using Environment Variables:

```java
import com.github.empjustine.folders.Folder;
import com.github.empjustine.folders.impl.LocalFolder;
import com.github.empjustine.folders.impl.RemoteFolder;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;

class Example {
	public static void main (String[] args) {
		AuthenticationContext authenticationContext = new AuthenticationContext(
			System.getenv("smb_username"),
			System.getenv("smb_password").toCharArray(),
			System.getenv("smb_domain")
		);
		SMBClient smbClient = new SMBClient();


		Folder remoteFolder = new RemoteFolder(
			System.getenv("smb_hostname"),
			System.getenv("smb_diskshare"),
			System.getenv("smb_path"),
			authenticationContext,
			smbClient
		);

		Folder localFolder = new LocalFolder("/tmp/share/myFolder");
	}
}
```

#### List files in a folder:

```java
	List<String> folderContents = folder.list();
```

#### Get a `InputStream` / read from a file:

```java
	Integer returnFromLambda = folder.withInputStream("fileName.txt", inputStream -> {
		int singleByte = inputStream.read();
		return singleByte * 3;
	});
```

No ARM (automatic resource management or try-with-resources) or manual 
`#close()` method calls required on the provided `InputStream`.

The block of code manages the lifecycle of intermediate connection / disk 
share / file handle objects.

#### Get a `OutputStream` / write to a file:

```java
	folder.withOutputStream("fileName.txt", inputStream -> {
		inputStream.write(1);
		return null; // lambda is *required* to return something.
	});
```

No ARM (automatic resource management or try-with-resources) or manual 
`#close()` method calls required on the provided `OutputStream`.

The block of code manages the lifecycle of intermediate connection / disk 
share / file handle objects.

#### Write a UTF-8 string to a file:

```java
	folder.write("hello.txt", "world!");
```

#### Delete a file:

```java
	folder.remove("hello.txt");
```

## License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) for details.
