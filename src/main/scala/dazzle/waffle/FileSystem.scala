package dazzle.waffle

import dazzle.waffle.adapter.Adapter
import java.nio.file.Path
import java.io.{File, ByteArrayInputStream, InputStream}
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
   * @param length length of the content
   */
  def write(key: String, content: InputStream, length: Long): Try[Long] = adapter.write(key, content, length)

  /**
   * Writes the given path(nio2) into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: Path): Try[Long] = adapter.write(key, content)

  /**
   * Writes the given file(io) into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: File): Try[Long] = adapter.write(key, content.toPath)

  /**
   * Writes the given string into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: String): Try[Long] = {
    adapter.write(key, new ByteArrayInputStream(content.getBytes("utf-8")), content.length)
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
  def rename(sourceKey: String, targetKey: String): Try[Unit] = adapter.move(sourceKey, targetKey)

  /**
   * Moves the file
   *
   * @param sourceKey source file path
   * @param targetKey target file path
   */
  def move(sourceKey: String, targetKey: String): Try[Unit] = adapter.move(sourceKey, targetKey)

  /**
   * Gets last modified time
   *
   * @param key file path
   * @return last modified time
   */
  def mtime(key: String): Try[Long] = adapter.mtime(key)

  /**
   * Indicates whether the file exists
   *
   * @param key file path
   * @return boolean
   */
  def exists(key: String): Boolean = adapter.exists(key)
}
