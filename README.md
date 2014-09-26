# Waffle

Waffle is a scala library that provides a filesystem abstraction layer.

This project was inspired from [Gaufrette](https://github.com/KnpLabs/Gaufrette).

## Usage

### Basic

```scala
val filesystem = new FileSystem(adapter)

/** Read */
filesystem.read("path/to/file") match {
  case Success(stream) =>
  case Failure(ex) =>
}

/** Write */
filesystem.write("path/to/file", "string content").map { length => ... }
filesystem.write("path/to/file", path)
filesystem.write("path/to/file", file)
filesystem.write("path/to/file", stream, length)

/** Write with for comprehension */
val total = for {
  fl1 <- filesystem.write("path/to/file1", content1)
  fl2 <- filesystem.write("path/to/file2", content2)
  fl3 <- filesystem.write("path/to/file3", content3)
} yield fl1 + fl2 + fl3

/** Delete */
val result = filesystem.delete("path/to/file") map { true } getOrElse { false }

/** Move */
val result = filesystem.move("path/to/file1", "path/to/file2") map { true } getOrElse { false }

/** Get modified time */
val result = filesystem.mtime("path/to/file1")

/** Indicate whether the file exists */
if (filesystem.exists("path/to/file")) { ... }
```


### Using local file system

```scala
import dazzle.waffle.FileSystem
import dazzle.waffle.adapter.LocalAdapter

val adapter = new LocalAdapter("/tmp")
val filesystem = new FileSystem(adapter)
```

### Using Amazon S3

```scala
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import dazzle.waffle.FileSystem
import dazzle.waffle.adapter.AmazonS3Adapter

val credentials = new BasicAWSCredentials("access_key_id", "secret_access_key_id")
val s3client    = new AmazonS3Client(credentials)
val adapter     = new AmazonS3Adapter(s3client, "bucket_name")
val filesystem  = new FileSystem(adapter)
```
