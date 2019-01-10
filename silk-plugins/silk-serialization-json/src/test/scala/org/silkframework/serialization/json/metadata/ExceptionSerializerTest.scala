package org.silkframework.serialization.json.metadata

import org.scalatest.{FlatSpec, Matchers}
import org.silkframework.entity.metadata.ExceptionSerializer
import org.silkframework.runtime.serialization.{ReadContext, WriteContext}

class ExceptionSerializerTest extends FlatSpec with Matchers {

  //* test objects //*
  "ExceptionSerializer" should "not fail when exceptions without certain features occur (missing string constructor, empty cause)" in {
    val cau = new Throwable("cause")
    val ex1 = NoStringConstructorThrowable(cau) // see CMEM-1472, lead to trouble with some of Sparks exceptions
    val ex2 = UnknownCauseException("no known cause") // has been observed to lead to to NPE in situations will lead to emulated exception
    val throwable1 = serializeThrowable(ex1)
    val throwable2 = serializeThrowable(ex2)
    throwable1.getMessage shouldBe "NoStringConstructor Test Message"
    throwable2.getMessage shouldBe "Emulated Exception of class: org.silkframework.serialization.json.metadata.UnknownCauseException original message: "
  }

  "ExceptionSerializerJson" should "not fail when exceptions without certain features occur (missing string constructor, empty cause)" in {
    val cau = new Throwable("cause")
    val ex1 = NoStringConstructorThrowable(cau) // see CMEM-1472, lead to trouble with some of Sparks exceptions
    val ex2 = UnknownCauseException("no known cause") // has been observed to lead to to NPE in situations
    val throwable1 = serializeThrowableJson(ex1)
    val throwable2 = serializeThrowableJson(ex2)
    throwable1.getMessage shouldBe "Emulated Exception of class: org.silkframework.serialization.json.metadata.NoStringConstructorThrowable original message: NoStringConstructor Test Message"
    throwable2.getMessage shouldBe "Emulated Exception of class: org.silkframework.serialization.json.metadata.UnknownCauseException original message: null"
  }

  "ExceptionSerializerJson" should "not fail when an contain null values as messages" in {
    // Specifically test for Json, XML handles that differently
    val exceptionWithNulls: Throwable = NullMessageException("With String constructor, but no 'message'")
    val throwable = serializeThrowableJson(exceptionWithNulls)
    throwable.getMessage shouldBe "Emulated Exception of class: org.silkframework.serialization.json.metadata.NullMessageException original message: null"
  }

  "ExceptionSerializer" should "not fail when an contain null values as messages" in {
    // but we ca try xml as well
    val exceptionWithNulls: Throwable = NullMessageException("With String constructor, but no 'message'")
    val throwable = serializeThrowable(exceptionWithNulls)
    throwable.getMessage shouldBe "Emulated Exception of class: org.silkframework.serialization.json.metadata.NullMessageException original message: "
  }


  /* Helper methods that serialize and deserialize a Throwable and return it. */
  def serializeThrowable(exception: Throwable): Throwable = {
    val serializer = new ExceptionSerializer
    try {
      val nde = serializer.write(exception)(WriteContext())
      val res = serializer.read(nde)(ReadContext())
      res
    }
    catch {
      case _: NoSuchMethodException =>
        fail(new Exception("Exception Serializer failed to de-/serialize an Exception without a String Constructor"))
      case _: NullPointerException =>
        fail(new Exception("Exception Serializer failed to de-/serialize an Exception with missing feature"))
      case t: Throwable =>
        fail(t)
    }
  }

  def serializeThrowableJson(exception: Throwable): Throwable = {
    val serializer = new ExceptionSerializerJson
    try {
      val nde = serializer.write(exception)(WriteContext())
      val res = serializer.read(nde)(ReadContext())
      res
    }
    catch {
      case _: NoSuchMethodException =>
        fail(new Exception("Exception Serializer failed to de-/serialize an Exception without a String Constructor"))
      case _: NullPointerException =>
        fail(new Exception("Exception Serializer failed to de-/serialize an Exception with missing feature"))
      case t: Throwable =>
        fail(t)
    }
  }

}

case class NoStringConstructorThrowable(ex: Throwable) extends Throwable {
  override def getMessage: String = "NoStringConstructor Test Message"
}

case class NullMessageException(s: String) extends Throwable {
  override def getMessage: String = null
}

case class UnknownCauseException(message: String) extends Throwable {
  override def getCause: Throwable = UnknownCause(this.message)
}

case class UnknownCause(message: String) extends Throwable {
  override def getMessage: String = "UnknownCause Test Message"
  override def getCause: UnknownCause = null
}
