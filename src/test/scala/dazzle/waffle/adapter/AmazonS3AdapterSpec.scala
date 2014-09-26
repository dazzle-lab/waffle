package dazzle.waffle.adapter

import java.io.{FileNotFoundException, InputStream}
import java.nio.file.{Paths, Files}
import collection.JavaConversions._
import com.amazonaws.AmazonClientException
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.AmazonS3Client
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

import scala.util.Try

@RunWith(classOf[JUnitRunner])
class AmazonS3AdapterSpec extends Specification with Mockito {
  trait WithAmazonS3ClientMock extends Scope {
    val bucketName = "bucketname"
    val key        = "path/to/file"
    val clientMock = mock[AmazonS3Client]

    clientMock.listBuckets() returns List(new Bucket(bucketName))
  }

  "AmazonS3#read" should {
    "return return successful try input stream if file exists" in new WithAmazonS3ClientMock {
      val objectMock = mock[S3Object]
      val objectInputStreamMock = mock[S3ObjectInputStream]

      objectMock.getObjectContent returns objectInputStreamMock
      clientMock.getObject(bucketName, key) returns objectMock

      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.read(key) must beSuccessfulTry[InputStream].withValue(objectInputStreamMock)
      there was one(clientMock).getObject(bucketName, key)
      there was one(objectMock).getObjectContent
    }

    "return failed try exception if file does not exists" in new WithAmazonS3ClientMock {
      clientMock.getObject(bucketName, "path/to/file") throws mock[AmazonClientException]

      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.read(key) must beFailedTry[InputStream].withThrowable[FileNotFoundException]
      there was one(clientMock).getObject(bucketName, key)
    }
  }

  "AmazonS3#write" should {
    "return successful try unit if content exists" in new WithAmazonS3ClientMock {
      clientMock.putObject(any[PutObjectRequest]) returns mock[PutObjectResult]

      val temp = Files.createTempFile(null, null)
      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.write(key, temp) must beSuccessfulTry[Long]
      there was one(clientMock).putObject(any[PutObjectRequest])
    }

    "return failed try exception if content does not exists" in new WithAmazonS3ClientMock {
      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.write(key, Paths.get("")) must beFailedTry[Long].withThrowable[FileNotFoundException]
    }
  }

  "AmazonS3#delete" should {
    "return successful try unit" in new WithAmazonS3ClientMock {
      val adapter = new AmazonS3Adapter(clientMock, bucketName)
      adapter.delete("path/to/file") must beSuccessfulTry[Unit]

      there was one(clientMock).deleteObject(bucketName, key)
    }
  }

  "AmazonS3#move" should {
    "return successful try unit" in new WithAmazonS3ClientMock {
      val sourceKey = "path/to/file1"
      val targetKey = "path/to/file2"

      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.move(sourceKey, targetKey) must beSuccessfulTry[Unit]
      there was one(clientMock).copyObject(bucketName, sourceKey, bucketName, targetKey)
      there was one(clientMock).deleteObject(bucketName, sourceKey)
    }
  }

  "AmazonS3#mtime" should {
    "return successful try long" in new WithAmazonS3ClientMock {
      val metadataMock = mock[ObjectMetadata]
      metadataMock.getContentLength returns 100L
      clientMock.getObjectMetadata(bucketName, key) returns metadataMock

      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.mtime(key) must beSuccessfulTry.withValue(100L)
      there was one(metadataMock).getContentLength
      there was one(clientMock).getObjectMetadata(bucketName, key)
    }
  }

  "AmazonS3#exists" should {
    "return true if metadata exists" in new WithAmazonS3ClientMock {
      clientMock.getObjectMetadata(bucketName, key) returns mock[ObjectMetadata]

      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.exists(key) must beTrue
      there was one(clientMock).getObjectMetadata(bucketName, key)
    }

    "returns false if metadata does not exists" in new WithAmazonS3ClientMock {
      clientMock.getObjectMetadata(bucketName, key) throws mock[AmazonClientException]

      val adapter = new AmazonS3Adapter(clientMock, bucketName)

      adapter.exists(key) must beFalse
      there was one(clientMock).getObjectMetadata(bucketName, key)
    }
  }
}
