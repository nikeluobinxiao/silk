package org.silkframework.runtime.serialization

import org.silkframework.config.Prefixes
import org.silkframework.runtime.plugin.PluginRegistry

import scala.reflect.ClassTag

/**
  * Utility class for serializing values.
  */
object Serialization {

  /**
    * Holds all registered serialization formats.
    */
  private lazy val serializationFormats: Seq[SerializationFormat[Any, Any]] = {
    implicit val prefixes = Prefixes.empty
    val formatTypes = PluginRegistry.availablePlugins[SerializationFormat[Any, Any]]
    formatTypes.map(_.apply())
  }

  def availableFormats: Seq[SerializationFormat[Any, Any]] = serializationFormats

  def hasSerialization(classToSerialize: Class[_], mimeType: String): Boolean = {
    serializationFormat(classToSerialize, mimeType).isDefined
  }

  def serializationFormat(classToSerialize: Class[_], mimeType: String): Option[SerializationFormat[Any, Any]] = {
    serializationFormats.find(f => f.valueType == classToSerialize && f.mimeTypes.contains(mimeType))
  }

  def hasSerialization[T: ClassTag](mimeType: String): Boolean = {
    val valueType = implicitly[ClassTag[T]].runtimeClass
    serializationFormats.exists(f => f.valueType == valueType && (f.mimeTypes.contains(mimeType) || mimeType == "*/*"))
  }

  def formatForType[T: ClassTag, U: ClassTag]: SerializationFormat[T, U] = {
    val valueType = implicitly[ClassTag[T]].runtimeClass
    val serializedType = implicitly[ClassTag[U]].runtimeClass
    serializationFormats.find(f => f.valueType == valueType && f.serializedType == serializedType) match {
      case Some(format) =>
        format.asInstanceOf[SerializationFormat[T, U]]
      case None =>
        throw new NoSuchElementException(s"No serialization format for type $valueType for serialization type $serializedType available.")
    }
  }

  /**
    * Checks if a format is available for a given MIME type.
    */
  def hasFormatFormMime[T: ClassTag](mimeType: String): Boolean = {
    val valueType = implicitly[ClassTag[T]].runtimeClass
    formatForMimeOption(valueType, mimeType).isDefined
  }

  /**
    * Retrieves a format for a static value type and a given MIME type.
    */
  def formatForMime[T: ClassTag](mimeType: String): SerializationFormat[T, Any] = {
    formatForMime(implicitly[ClassTag[T]].runtimeClass, mimeType).asInstanceOf[SerializationFormat[T, Any]]
  }

  /**
    * Retrieves a format for a dynamic value type and a given MIME type.
    */
  def formatForMime(valueType: Class[_], mimeType: String): SerializationFormat[Any, Any] = {
    formatForMimeOption(valueType, mimeType) match {
      case Some(format) =>
        format
      case None =>
        throw new NoSuchElementException(s"No serialization format for type $valueType for content type $mimeType available.")
    }
  }

  /**
    * Retrieves an optional format for a dynamic value type and a given MIME type.
    */
  def formatForMimeOption(valueType: Class[_], mimeType: String): Option[SerializationFormat[Any, Any]] = {
    val formatOption = serializationFormats.find(f => f.valueType == valueType && (f.mimeTypes.contains(mimeType) || mimeType == "*/*"))
    if(mimeType == "*/*" || mimeType == "text/plain") {
      // If the caller accepts text/plain, fall back to the plain text format so something is shown to the user
      Some(formatOption.getOrElse(PlainTextFormat.asInstanceOf[SerializationFormat[Any, Any]]))
    } else {
      formatOption
    }
  }

}
