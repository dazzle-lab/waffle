package dazzle.waffle.adapter

import com.amazonaws.services.s3.model.{ObjectMetadata, CannedAccessControlList, PutObjectRequest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.AmazonClientException
import collection.JavaConversions._
import java.io.{FileInputStream, FileNotFoundException, InputStream}
import java.nio.file.{Files, Path}
import scala.util.Try

/**
 * AmazonS3 adapter
 *
 * ==Usage==
 * {{{
 * import com.amazonaws.services.s3.AmazonS3Client
 * import com.amazonaws.auth.BasicAWSCredentials
 * import dazzle.waffle.FileSystem
 * import dazzle.waffle.adapter.AmazonS3
 *
 * val credentials = new BasicAWSCredentials("access_key_id", "secret_access_key_id")
 * val s3client    = new AmazonS3Client(credentials)
 * val s3adapter   = new AmazonS3(s3client, "bucket_name")
 * val filesystem  = new FileSystem(s3adapter)
 *
 * filesystem.read("path/to/file") match {
 *   case Success(stream) => stream.read
 *   case Failure(ex) => ...
 * }
 * }}}
 *
 * @param client amazon s3 client
 * @param bucket bucket name
 */
class AmazonS3(client: AmazonS3Client, bucket: String) extends Adapter {
  require(client.listBuckets().exists(_.getName == bucket))

  override def read(key: String): Try[InputStream] = Try {
    try {
      client.getObject(bucket, key).getObjectContent
    } catch {
      case e: AmazonClientException => throw new FileNotFoundException()
    }
  }

  override def write(key: String, content: Path): Try[Long] = Try {
    val length = Files.size(content)
    val stream = new FileInputStream(content.toString)

    write(key, stream, length).get
  }

  override def write(key: String, content: InputStream, length: Long): Try[Long] = Try {
    val metadata = new ObjectMetadata()
    metadata.setContentLength(length)

    val request  = new PutObjectRequest(bucket, key, content, metadata).withCannedAcl(CannedAccessControlList.PublicReadWrite)

    client.putObject(request)
    length
  }

  override def delete(key: String): Try[Unit] = Try {
    client.deleteObject(bucket, key)
  }

  override def move(sourceKey: String, targetKey: String): Try[Unit] = Try {
    client.copyObject(bucket, sourceKey, bucket, targetKey)
    client.deleteObject(bucket, sourceKey)
  }

  override def mtime(key: String): Try[Long] = Try {
    val metadata = client.getObjectMetadata(bucket, key)
    metadata.getContentLength
  }

  override def exists(key: String): Boolean = {
    try {
      client.getObjectMetadata(bucket, key)
      true
    } catch {
      case e: AmazonClientException  => false
    }
  }
}
