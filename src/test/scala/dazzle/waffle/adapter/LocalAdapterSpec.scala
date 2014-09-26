package dazzle.waffle.adapter

import java.io.{File, FileNotFoundException, InputStream}
import java.nio.file.{NoSuchFileException, Files}

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LocalAdapterSpec extends Specification with Mockito {
  "Local#read" should {
    "return successful try input stream if file exists" in {
      val temp = Files.createTempFile(null, null)
      val root = temp.getParent
      val adapter = new LocalAdapter(root.toString)

      adapter.read(temp.getFileName.toString) must beSuccessfulTry[InputStream]
    }

    "return failed try exception if file does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new LocalAdapter(root.toString)

      adapter.read("foo/bar") must beFailedTry[InputStream].withThrowable[FileNotFoundException]
    }
  }

  "Local#write" should {
    "return successful try long" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new LocalAdapter(root.toString)

      adapter.write("foo/bar", temp) must beSuccessfulTry[Long]
      adapter.read("foo/bar") must beSuccessfulTry[InputStream]
    }

    "return faild try exception if path does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new LocalAdapter(root.toString)
      val file = new File("")

      adapter.write("foo/bar", file.toPath) must beFailedTry[Long].withThrowable[FileNotFoundException]
    }
  }

  "Local#delete" should {
    "return successful try unit if source file exists" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new LocalAdapter(root.toString)

      adapter.write("foo/bar", temp)
      adapter.delete("foo/bar") must beSuccessfulTry[Unit]
      adapter.read("foo/bar") must beFailedTry[InputStream].withThrowable[FileNotFoundException]
    }

    "return failed try exception if source file does not exists" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new LocalAdapter(root.toString)

      adapter.delete("foo/bar") must beFailedTry[Unit].withThrowable[NoSuchFileException]
    }
  }

  "Local#move" should {
    "return successful try unit if source file exists" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new LocalAdapter(root.toString)

      adapter.write("foo/bar", temp)
      adapter.move("foo/bar", "foo/buzz") must beSuccessfulTry[Unit]
      adapter.read("foo/buzz") must beSuccessfulTry[InputStream]
      adapter.read("foo/bar") must beFailedTry[InputStream].withThrowable[FileNotFoundException]
    }

    "return failed try exception if source file does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new LocalAdapter(root.toString)

      adapter.move("foo/bar", "foo/buzz") must beFailedTry[Unit].withThrowable[NoSuchFileException]
    }
  }

  "Local#mtime" should {
    "return successful try long" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new LocalAdapter(root.toString)

      adapter.write("foo/bar", temp)
      adapter.mtime("foo/bar") must beSuccessfulTry[Long]
    }

    "return failed try exception if source file does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new LocalAdapter(root.toString)

      adapter.mtime("foo/bar") must beFailedTry[Long]
    }
  }
}
