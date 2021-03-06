package org.silkframework.runtime.resource

import java.io.{ByteArrayOutputStream, IOException, InputStream}
import java.time.Instant

import org.silkframework.config.{Config, DefaultConfig}

import scala.io.{Codec, Source}

/**
 * A resource, such as a file.
 */
trait Resource {

  /**
   * The local name of this resource.
   */
  def name: String

  /**
   * The path of this resource.
   */
  def path: String

  /**
    * Checks if this resource exists.
    */
  def exists: Boolean

  /**
    * Returns the size of this resource in bytes.
    * Returns None if the size is not known.
    */
  def size: Option[Long]

  /**
    * The time that the resource was last modified.
    * Returns None if the time is not known.
    */
  def modificationTime: Option[Instant]

  /**
   * Creates an input stream for reading the resource.
   *
   * @return An input stream for reading the resource.
   *         The caller is responsible for closing the stream after reading.
   */
  def inputStream: InputStream

  /**
    * Reads the input stream with a provided read function.
    * This method should usually be preferred over requesting an inputStream as it takes care of closing the stream after reading is done.
    */
  def read[T](reader: InputStream => T): T = {
    val is = inputStream
    try {
      reader(is)
    } finally {
      is.close()
    }
  }

  /**
   * Loads this resource into a string.
   */
  def loadAsString(implicit codec: Codec): String = {
    checkSizeForInMemory()
    val source = Source.fromInputStream(inputStream)(codec)
    try {
      source.getLines.mkString("\n")
    } finally {
      source.close()
    }
  }

  /**
    * Loads all lines of this resource into a sequence.
    */
  def loadLines(implicit codec: Codec): Seq[String] = {
    checkSizeForInMemory()
    val source = Source.fromInputStream(inputStream)(codec)
    try {
      source.getLines.toList
    } finally {
      source.close()
    }
  }

  /**
    * Loads this resource into a byte array.
    */
  def loadAsBytes: Array[Byte] = {
    checkSizeForInMemory()
    val in = inputStream
    try {
      val out = new ByteArrayOutputStream()
      var b = in.read()
      while (b > -1) {
        out.write(b)
        b = in.read()
      }
      out.toByteArray
    } finally {
      in.close()
    }
  }

  /**
    * True, if this resource does exist and is not empty.
    * False, otherwise.
    */
  def nonEmpty: Boolean = {
    if(exists) {
      size match {
        case Some(s) =>
          s > 0
        case None =>
          val in = inputStream
          try {
            in.read() != -1
          } finally {
            in.close()
          }
      }
    } else {
      false
    }
  }

  /**
   * Returns the name of this resource.
   */
  override def toString: String = name

  /**
    * Checks if this resource is not too large to be loaded into memory.
    *
    * @throws IOException If this resource is too large to be loaded into memory.
    */
  protected def checkSizeForInMemory(): Unit = {
    for(s <- size) {
      if(s > Resource.maxInMemorySize) {
        throw new IOException(s"Resource $name is too large to be loaded into memory (size: $size, maximum allowed size: ${Resource.maxInMemorySize}).")
      }
    }
  }
}

object Resource {

  /**
    * Maximum resource size in bytes that should be loaded into memory.
    */
  private lazy val maxInMemorySize = {
    DefaultConfig.instance.forClass(classOf[Resource]).getMemorySize("maxInMemorySize").toBytes
  }

}