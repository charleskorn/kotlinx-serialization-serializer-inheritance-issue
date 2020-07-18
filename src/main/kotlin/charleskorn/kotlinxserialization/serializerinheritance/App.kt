package charleskorn.kotlinxserialization.serializerinheritance

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

fun main(args: Array<String>) {
    val json = Json(JsonConfiguration.Stable)

    println("Companion object implements KSerializer directly, no @Serializer attribute")
    println(json.stringify(ThingWithCustomSerializer.serializer(), ThingWithCustomSerializer("hello")))
    println()

    println("Companion object uses base class, has @Serializer attribute, base class implements custom serialize()")
    println(json.stringify(ThingWithCustomSerializerWithInheritanceAndAttribute.serializer(), ThingWithCustomSerializerWithInheritanceAndAttribute("hello")))
    println()

    println("Companion object uses base class, no @Serializer attribute, base class implements custom serialize()")
    println(json.stringify(ThingWithCustomSerializerWithInheritanceAndNoAttribute.serializer(), ThingWithCustomSerializerWithInheritanceAndNoAttribute("hello")))
    println()

    println("Companion object uses base class, has @Serializer attribute, companion implements custom serialize()")
    println(json.stringify(ThingWithCustomSerializerWithPartialInheritance.serializer(), ThingWithCustomSerializerWithPartialInheritance("hello")))
    println()
}

@Serializable(with = ThingWithCustomSerializer.Companion::class)
data class ThingWithCustomSerializer(val name: String) {
    companion object : KSerializer<ThingWithCustomSerializer> {
        override val descriptor = SerialDescriptor("test") {
            element("name", String.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: ThingWithCustomSerializer) {
            encoder.encodeString(value.name + " (serializer 1)")
        }

        override fun deserialize(decoder: Decoder): ThingWithCustomSerializer = throw UnsupportedOperationException()
    }
}

@Serializable(with = ThingWithCustomSerializerWithInheritanceAndAttribute.Companion::class)
data class ThingWithCustomSerializerWithInheritanceAndAttribute(val name: String) {

    @Serializer(forClass = ThingWithCustomSerializerWithInheritanceAndAttribute::class)
    companion object : BaseSerializer() {}
}

abstract class BaseSerializer : KSerializer<ThingWithCustomSerializerWithInheritanceAndAttribute> {
    override val descriptor = SerialDescriptor("test") {
        element("name", String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: ThingWithCustomSerializerWithInheritanceAndAttribute) {
        encoder.encodeString(value.name + " (serializer 2)")
    }

    override fun deserialize(decoder: Decoder): ThingWithCustomSerializerWithInheritanceAndAttribute = throw UnsupportedOperationException()
}

@Serializable(with = ThingWithCustomSerializerWithInheritanceAndNoAttribute.Companion::class)
data class ThingWithCustomSerializerWithInheritanceAndNoAttribute(val name: String) {

    // NOTE: no @Serializer attribute
    companion object : BaseSerializer2() {}
}

abstract class BaseSerializer2 : KSerializer<ThingWithCustomSerializerWithInheritanceAndNoAttribute> {
    override val descriptor = SerialDescriptor("test") {
        element("name", String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: ThingWithCustomSerializerWithInheritanceAndNoAttribute) {
        encoder.encodeString(value.name + " (serializer 3)")
    }

    override fun deserialize(decoder: Decoder): ThingWithCustomSerializerWithInheritanceAndNoAttribute = throw UnsupportedOperationException()
}

@Serializable(with = ThingWithCustomSerializerWithPartialInheritance.Companion::class)
data class ThingWithCustomSerializerWithPartialInheritance(val name: String) {

    @Serializer(forClass = ThingWithCustomSerializerWithPartialInheritance::class)
    companion object : PartialBaseSerializer() {
        override fun serialize(encoder: Encoder, value: ThingWithCustomSerializerWithPartialInheritance) {
            encoder.encodeString(value.name + " (serializer 4)")
        }
    }
}

abstract class PartialBaseSerializer : KSerializer<ThingWithCustomSerializerWithPartialInheritance> {
    override val descriptor = SerialDescriptor("test") {
        element("name", String.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): ThingWithCustomSerializerWithPartialInheritance = throw UnsupportedOperationException()
}
