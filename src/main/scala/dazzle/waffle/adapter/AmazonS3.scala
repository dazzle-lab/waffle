package dazzle.waffle.adapter

import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest, GetObjectRequest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.{AmazonServiceException, AmazonClientException}
import collection.JavaConversions._
import java.io.{File, FileNotFoundException, InputStream}
import scala.util.{Failure, Success, Try}

/**
 * AmazonS3 adapter
 *
 * @param client amazon s3 client
 * @param bucket bucket name
 */
class AmazonS3(client: AmazonS3Client, bucket: String) extends Adapter {
  require(client.listBuckets().exists(_.getName == bucket))

  override def read(key: String): Try[InputStream] = Try {
    exists(key) match {
      case Success(b) if b  => client.getObject(new GetObjectRequest(bucket, key)).getObjectContent
      case Success(b) if !b => throw new FileNotFoundException
      case Failure(e) => throw e
    }
  }

  override def write(key: String, content: File): Try[Unit] = Try {
    val request = new PutObjectRequest(bucket, key, content).withCannedAcl(CannedAccessControlList.PublicReadWrite)
    client.putObject(request)
  }

  override def delete(key: String): Try[Unit] = Try {
    client.deleteObject(bucket, key)
  }

  override def rename(sourceKey: String, targetKey: String): Try[Unit] = Try {
    client.copyObject(bucket, sourceKey, bucket, targetKey)
    client.deleteObject(bucket, sourceKey)
  }

  override def exists(key: String): Try[Boolean] = Try {
    try {
      client.getObjectMetadata(bucket, key)
      true
    } catch {
      case e: AmazonClientException  => false
      case e: AmazonServiceException => false
    }
  }
}
