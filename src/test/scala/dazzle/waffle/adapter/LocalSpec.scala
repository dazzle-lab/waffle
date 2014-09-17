package dazzle.waffle.adapter

import java.io.{File, FileNotFoundException, InputStream}
import java.nio.file.Files

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LocalSpec extends Specification with Mockito {
  "Local#read" should {
    "return successful try input stream if file exists" in {
      val temp = Files.createTempFile(null, null)
      val root = temp.getParent
      val adapter = new Local(root.toString)

      adapter.read(temp.getFileName.toString) must beSuccessfulTry[InputStream]
    }

    "return failed try exception if file does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new Local(root.toString)

      adapter.read("foo/bar") must beFailedTry[InputStream].withThrowable[FileNotFoundException]
    }
  }

  "Local#write" should {
    "return successful try unit if content exists" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new Local(root.toString)

      adapter.write("foo/bar", temp.toFile) must beSuccessfulTry[Unit]
      adapter.read("foo/bar") must beSuccessfulTry[InputStream]
    }

    "return failed try exception if content does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new Local(root.toString)

      adapter.write("foo/bar", new File("")) must beFailedTry[Unit].withThrowable[FileNotFoundException]
    }
  }

  "Local#rename" should {
    "return successful try unit if source file exists" in {
      val root = Files.createTempDirectory(null)
      val temp = Files.createTempFile(null, null)
      val adapter = new Local(root.toString)

      adapter.write("foo/bar", temp.toFile)
      adapter.rename("foo/bar", "foo/buzz") must beSuccessfulTry[Unit]
      adapter.read("foo/buzz") must beSuccessfulTry[InputStream]
      adapter.read("foo/bar") must beFailedTry[InputStream].withThrowable[FileNotFoundException]
    }

    "return failed try exception if source file does not exists" in {
      val root = Files.createTempDirectory(null)
      val adapter = new Local(root.toString)

      adapter.rename("foo/bar", "foo/buzz") must beFailedTry[Unit].withThrowable[FileNotFoundException]
    }
  }
}
