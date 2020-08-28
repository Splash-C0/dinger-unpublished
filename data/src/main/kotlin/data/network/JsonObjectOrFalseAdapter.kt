package data.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import java.util.Collections

internal class JsonObjectOrFalseAdapter<T> private constructor(
    private val objectDelegate: JsonAdapter<T>?) : JsonAdapter<T>() {
  class Factory(private val objectDelegateFactory: JsonAdapter.Factory) : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>?, moshi: Moshi) =
        when (hasJsonObjectOrFalseAnnotation(annotations)) {
          false -> null
          true -> JsonObjectOrFalseAdapter(
              objectDelegateFactory.create(type, Collections.emptySet(), moshi))
        }
  }

  override fun fromJson(reader: JsonReader) = when (reader.peek()) {
    JsonReader.Token.BOOLEAN -> when (reader.nextBoolean()) {
      false -> null // Response was false
      else ->
        throw IllegalStateException("Non-false boolean for @JsonObjectOrFalse field")
    }
    JsonReader.Token.BEGIN_OBJECT -> objectDelegate?.fromJson(reader)
    else ->
      throw IllegalStateException("Non-object-non-boolean value for @JsonObjectOrFalse field")
  }

  override fun toJson(writer: JsonWriter, value: T?) =
      objectDelegate?.toJson(writer, value) ?: Unit
}

private fun hasJsonObjectOrFalseAnnotation(annotations: Set<Annotation>?) =
    annotations?.firstOrNull { it.annotationClass == JsonObjectOrFalse::class } != null
