package dazzle.waffle

import dazzle.waffle.adapter.Adapter
import java.io.{File, InputStream}
import java.nio.file.Files
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
    "return successful try unit with file" in {
      val file = mock[File]
      val adapterMock = mock[Adapter]
      adapterMock.write("path/to/file", file) returns Success(Unit)

      val filesystem = new FileSystem(adapterMock)

      filesystem.write("path/to/file", file) must beSuccessfulTry[Unit]
      there was one(adapterMock).write("path/to/file", file)
    }

    "return successful try unit with string" in {
      val adapterMock = mock[Adapter]
      adapterMock.write(any[String], any[File]) returns Success(Unit)

      val filesystem = new FileSystem(adapterMock)
      filesystem.write("path/to/file", "string") must beSuccessfulTry[Unit]

      there was one(adapterMock).write(any[String], any[File])
    }

    "return successful try unit with path" in {
      val adapterMock = mock[Adapter]
      adapterMock.write(any[String], any[File]) returns Success(Unit)

      val temp = Files.createTempFile(null, null)

      val filesystem = new FileSystem(adapterMock)
      filesystem.write("path/to/file", temp) must beSuccessfulTry[Unit]

      there was one(adapterMock).write(any[String], any[File])
    }
  }

  "FileSystem#exists" should {
    "return successful try boolean" in {
      val adapterMock = mock[Adapter]
      adapterMock.exists("path/to/file") returns Success(true)

      val filesystem = new FileSystem(adapterMock)

      filesystem.exists("path/to/file") must beSuccessfulTry[Boolean]
      there was one(adapterMock).exists("path/to/file")
    }
  }
}
