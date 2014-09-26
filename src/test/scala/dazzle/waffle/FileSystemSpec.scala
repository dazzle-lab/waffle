package dazzle.waffle

import dazzle.waffle.adapter.Adapter
import java.io.InputStream
import java.nio.file.{Path, Files}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.runner.JUnitRunner
import scala.util.Success

@RunWith(classOf[JUnitRunner])
class FileSystemSpec extends Specification with Mockito {
  "FileSystem#read" should {
    "return successful try input stream" in {
      val inputStream = mock[InputStream]
      val adapterMock = mock[Adapter]
      adapterMock.read("path/to/file") returns Success(inputStream)

      val filesystem = new FileSystem(adapterMock)

      filesystem.read("path/to/file") must beSuccessfulTry[InputStream]
      there was one(adapterMock).read("path/to/file")
    }
  }

  "FileSystem#write" should {
    "return successful try long with an instanceof input stream" in {
      val inputStream = mock[InputStream]
      val adapterMock = mock[Adapter]
      val length = 100
      val key = "path/to/file"

      adapterMock.write(key, inputStream, length) returns Success(length)

      val filesystem = new FileSystem(adapterMock)

      filesystem.write(key, inputStream, length) must beSuccessfulTry.withValue(length)
      there was one(adapterMock).write(key, inputStream, length)
    }

    "return successful try long with an instanceof path" in {
      val path = Files.createTempFile(null, null)
      val adapterMock = mock[Adapter]
      val key = "path/to/file"

      adapterMock.write(key, path) returns Success(100)

      val filesystem = new FileSystem(adapterMock)

      filesystem.write(key, path) must beSuccessfulTry.withValue(100)
      there was one(adapterMock).write(key, path)
    }

    "return successful try long with an instanceof file" in {
      val file = Files.createTempFile(null, null).toFile
      val adapterMock = mock[Adapter]
      val key = "path/to/file"

      adapterMock.write(any[String], any[Path]) returns Success(100)

      val filesystem = new FileSystem(adapterMock)

      filesystem.write(key, file) must beSuccessfulTry.withValue(100)
      there was one(adapterMock).write(any[String], any[Path])
    }

    "return successful try long with an instanceof string" in {
      val adapterMock = mock[Adapter]
      val key = "path/to/file"
      val content = "content"

      adapterMock.write(any[String], any[InputStream], any[Long]) returns Success(100)

      val filesystem = new FileSystem(adapterMock)

      filesystem.write(key, content)
      there was one(adapterMock).write(any[String], any[InputStream], any[Long])
    }
  }

  "FileSystem#delete" should {
    "return successful try unit" in {
      val adapterMock = mock[Adapter]
      val key = "path/to/file"

      adapterMock.delete(key) returns Success(Unit)

      val filesystem = new FileSystem(adapterMock)

      filesystem.delete(key) must beSuccessfulTry[Unit]
      there was one(adapterMock).delete(key)
    }
  }

  "FileSystem#rename" should {
    "return successful try unit" in {
      val adapterMock = mock[Adapter]
      val sourceKey = "path/to/file1"
      val targetKey = "path/to/file2"

      adapterMock.move(sourceKey, targetKey) returns Success(Unit)

      val filesystem = new FileSystem(adapterMock)

      filesystem.rename(sourceKey, targetKey) must beSuccessfulTry[Unit]
      there was one(adapterMock).move(sourceKey, targetKey)
    }
  }

  "FileSystem#move" should {
    "return successful try unit" in {
      val adapterMock = mock[Adapter]
      val sourceKey = "path/to/file1"
      val targetKey = "path/to/file2"

      adapterMock.move(sourceKey, targetKey) returns Success(Unit)

      val filesystem = new FileSystem(adapterMock)

      filesystem.move(sourceKey, targetKey) must beSuccessfulTry[Unit]
      there was one(adapterMock).move(sourceKey, targetKey)
    }
  }

  "FileSystem#exists" should {
    "return boolean" in {
      val adapterMock = mock[Adapter]
      adapterMock.exists("path/to/file") returns true

      val filesystem = new FileSystem(adapterMock)

      filesystem.exists("path/to/file") must beTrue
      there was one(adapterMock).exists("path/to/file")
    }
  }
}
