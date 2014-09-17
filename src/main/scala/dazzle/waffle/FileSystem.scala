package dazzle.waffle

import dazzle.waffle.adapter.Adapter
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.io.{File, InputStream}
import scala.util.Try

/**
 * Filesystem
 *
 * @param adapter an adapter
 */
class FileSystem(adapter: Adapter) {
  /**
   * Reads the content of the file
   *
   * @param key file path
   * @return input stream
   */
  def read(key: String): Try[InputStream] = adapter.read(key)

  /**
   * Writes the given content into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: File): Try[Unit] = adapter.write(key, content)

  /**
   * Writes the given path(nio2) into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: Path): Try[Unit] = Try {
    write(key, content.toFile).get
  }

  /**
   * Writes the given string into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: String): Try[Unit] = Try {
    val temp = Files.createTempFile(null, null)
    Files.write(temp, content.getBytes(StandardCharsets.UTF_8))

    write(key, temp.toFile).get
  }

  /**
   * Deletes the file
   *
   * @param key file path
   */
  def delete(key: String): Try[Unit] = adapter.delete(key)

  /**
   * Renames the file
   *
   * @param sourceKey source file path
   * @param targetKey target file path
   */
  def rename(sourceKey: String, targetKey: String): Try[Unit] = adapter.rename(sourceKey, targetKey)

  /**
   * Indicates whether the file exists
   *
   * @param key file path
   * @return boolean
   */
  def exists(key: String): Try[Boolean] = adapter.exists(key)
}
