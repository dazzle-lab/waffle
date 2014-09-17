package dazzle.waffle.adapter

import java.io.{InputStream, File, FileInputStream, FileNotFoundException}
import java.nio.file._
import scala.util.{Failure, Success, Try}

/**
 * Local filesystem adapter
 *
 * @param directory root directory path
 */
class Local(directory: String) extends Adapter {
  require(Files.isDirectory(Paths.get(directory)))

  override def read(key: String): Try[InputStream] = Try {
    exists(key) match {
      case Success(b) if  b => new FileInputStream(computePath(key).toString)
      case Success(b) if !b => throw new FileNotFoundException
      case Failure(e) => throw e
    }
  }

  override def write(key: String, content: File): Try[Unit] = Try {
    if (!content.exists()) {
      throw new FileNotFoundException()
    }

    val storedPath = computePath(key)
    val parentPath = storedPath.getParent

    if (!Files.isDirectory(parentPath)) {
      Files.createDirectories(parentPath)
    }

    Files.copy(content.toPath, storedPath, StandardCopyOption.REPLACE_EXISTING)
  }

  override def delete(key: String): Try[Unit] = Try {
    exists(key) match {
      case Success(b) if  b => Files.delete(computePath(key))
      case Success(b) if !b => throw new FileNotFoundException
      case Failure(e) => throw e
    }
  }

  override def rename(sourcekey: String, targetKey: String): Try[Unit] = Try {
    exists(sourcekey) match {
      case Success(b) if  b => Files.move(computePath(sourcekey), computePath(targetKey))
      case Success(b) if !b => throw new FileNotFoundException
      case Failure(e) => throw e
    }
  }

  override def exists(key: String): Try[Boolean] = Try {
    Files.exists(computePath(key))
  }

  private def computePath(key: String): Path = {
    Paths.get(directory + "/" + key)
  }
}
