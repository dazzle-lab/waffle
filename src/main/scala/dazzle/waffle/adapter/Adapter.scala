package dazzle.waffle.adapter

import java.io.{File, InputStream}
import scala.util.Try

trait Adapter {
  /**
   * Reads the content of the file
   *
   * @param key file path
   * @return input stream
   */
  def read(key: String): Try[InputStream]

  /**
   * Writes the given content into the file
   *
   * @param key file path
   * @param content the content
   */
  def write(key: String, content: File): Try[Unit]

  /**
   * Deletes the file
   *
   * @param key file path
   */
  def delete(key: String): Try[Unit]

  /**
   * Renames the file
   *
   * @param sourceKey source file path
   * @param targetKey target file path
   */
  def rename(sourceKey: String, targetKey: String): Try[Unit]

  /**
   * Indicates whether the file exists
   *
   * @param key file path
   * @return boolean
   */
  def exists(key: String): Try[Boolean]
}
