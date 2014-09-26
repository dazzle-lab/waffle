package dazzle.waffle.adapter

import java.io.InputStream
import java.nio.file.Path
import scala.util.Try

trait Adapter {
  /**
   * Reads the content of the file
   *
   * @param key key
   * @return input stream
   */
  def read(key: String): Try[InputStream]

  /**
   * Writes the given content into the file
   *
   * @param key key
   * @param content an instance of path
   */
  def write(key: String, content: Path): Try[Long]

  /**
   * Writes the given content into the file
   *
   * @param key key
   * @param content an instance of input stream
   */
  def write(key: String, content: InputStream, length: Long): Try[Long]

  /**
   * Deletes the file
   *
   * @param key key
   * @return boolean
   */
  def delete(key: String): Try[Unit]

  /**
   * Moves the file
   *
   * @param sourceKey source file path
   * @param targetKey target file path
   * @return boolean
   */
  def move(sourceKey: String, targetKey: String): Try[Unit]

  /**
   * Gets last modofied time
   *
   * @param key key
   * @return mills
   */
  def mtime(key: String): Try[Long]

  /**
   * Indicates whether the file exists
   *
   * @param key file path
   * @return boolean
   */
  def exists(key: String): Boolean
}
