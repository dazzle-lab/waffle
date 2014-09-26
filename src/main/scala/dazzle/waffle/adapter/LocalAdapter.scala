package dazzle.waffle.adapter

import java.io._
import java.nio.file._
import scala.util.Try

/**
 * Local filesystem adapter
 *
 * @param directory root directory path
 */
class LocalAdapter(directory: String) extends Adapter {
  require(Files.isDirectory(Paths.get(directory)))

  override def read(key: String): Try[InputStream] = Try {
    new FileInputStream(computePath(key).toString)
  }

  override def write(key: String, content: Path): Try[Long] = Try {
    val length = Files.size(content)
    val stream = new FileInputStream(content.toString)

    write(key, stream, length).get
  }

  override def write(key: String, content: InputStream, length: Long): Try[Long] = Try {
    val storedPath = computePath(key)
    val parentPath = storedPath.getParent

    if (!Files.isDirectory(parentPath)) {
      Files.createDirectories(parentPath)
    }

    val os = Files.newOutputStream(storedPath)

    try {
      Iterator.continually(content.read).takeWhile(-1 !=).foreach(b => os.write(b))
      Files.size(storedPath)
    } catch {
      case ex: Exception => throw ex
    } finally {
      os.close()
    }
  }

  override def delete(key: String): Try[Unit] = Try {
    Files.delete(computePath(key))
  }

  override def move(sourceKey: String, targetKey: String): Try[Unit] = Try {
    Files.move(computePath(sourceKey), computePath(targetKey))
  }

  override def mtime(key: String): Try[Long] = Try {
    Files.getLastModifiedTime(computePath(key)).toMillis
  }

  override def exists(key: String): Boolean = {
    try {
      Files.exists(computePath(key))
    } catch {
      case ex: Exception => false
    }
  }

  private def computePath(key: String): Path = {
    Paths.get(directory + "/" + key)
  }
}
