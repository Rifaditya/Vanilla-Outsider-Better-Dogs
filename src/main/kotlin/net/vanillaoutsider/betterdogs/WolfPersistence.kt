package net.vanillaoutsider.betterdogs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.resources.Identifier

/**
 * Persistent wolf data using Fabric Data Attachment API.
 * This properly persists wolf personality across world saves/loads.
 */
data class WolfPersistentData(
    val personalityId: Int = -1,
    val lastDamageTime: Int = 0,
    val lastMischiefDay: Long = 0,
    val bloodFeudTarget: String = "",
    val submissive: Boolean = false,
    val beingDisciplined: Boolean = false
) {
    companion object {
        val CODEC: Codec<WolfPersistentData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("personality").orElse(-1).forGetter { it.personalityId },
                Codec.INT.fieldOf("lastDamageTime").orElse(0).forGetter { it.lastDamageTime },
                Codec.LONG.optionalFieldOf("lastMischiefDay", 0L).forGetter { it.lastMischiefDay },
                Codec.STRING.optionalFieldOf("bloodFeudTarget", "").forGetter { it.bloodFeudTarget },
                Codec.BOOL.optionalFieldOf("submissive", false).forGetter { it.submissive },
                Codec.BOOL.optionalFieldOf("beingDisciplined", false).forGetter { it.beingDisciplined }
            ).apply(instance, ::WolfPersistentData)
        }
        
        val DEFAULT = WolfPersistentData()
    }
}

/**
 * Attachment type registration for wolf persistent data.
 */
object WolfDataAttachments {
    val WOLF_DATA: AttachmentType<WolfPersistentData> by lazy {
        AttachmentRegistry.createPersistent(
            Identifier.parse("betterdogs:wolf_data"),
            WolfPersistentData.CODEC
        )
    }
    
    fun init() {
        // Force class loading to register the attachment
        WOLF_DATA
    }
}

// Extension functions for easy access
fun Wolf.getWolfData(): WolfPersistentData {
    return this.getAttachedOrCreate(WolfDataAttachments.WOLF_DATA) { WolfPersistentData.DEFAULT }
}

fun Wolf.setWolfData(data: WolfPersistentData) {
    this.setAttached(WolfDataAttachments.WOLF_DATA, data)
}

fun Wolf.getPersistedPersonality(): WolfPersonality {
    return WolfPersonality.fromId(getWolfData().personalityId)
}

fun Wolf.setPersistedPersonality(personality: WolfPersonality) {
    val current = getWolfData()
    setWolfData(current.copy(personalityId = personality.id))
}

fun Wolf.hasPersistedPersonality(): Boolean {
    return getWolfData().personalityId >= 0
}

fun Wolf.getPersistedLastDamageTime(): Int {
    return getWolfData().lastDamageTime
}

fun Wolf.setPersistedLastDamageTime(time: Int) {
    val current = getWolfData()
    setWolfData(current.copy(lastDamageTime = time))
}

fun Wolf.getPersistedLastMischiefDay(): Long {
    return getWolfData().lastMischiefDay
}

fun Wolf.setPersistedLastMischiefDay(day: Long) {
    val current = getWolfData()
    setWolfData(current.copy(lastMischiefDay = day))
}

fun Wolf.getPersistedBloodFeudTarget(): String {
    return getWolfData().bloodFeudTarget
}

fun Wolf.setPersistedBloodFeudTarget(uuid: String) {
    val current = getWolfData()
    setWolfData(current.copy(bloodFeudTarget = uuid))
}

fun Wolf.hasPersistedBloodFeud(): Boolean {
    return getWolfData().bloodFeudTarget.isNotEmpty()
}

fun Wolf.getPersistedSubmissive(): Boolean {
    return getWolfData().submissive
}

fun Wolf.setPersistedSubmissive(submissive: Boolean) {
    val current = getWolfData()
    setWolfData(current.copy(submissive = submissive))
}

fun Wolf.getPersistedBeingDisciplined(): Boolean {
    return getWolfData().beingDisciplined
}

fun Wolf.setPersistedBeingDisciplined(beingDisciplined: Boolean) {
    val current = getWolfData()
    setWolfData(current.copy(beingDisciplined = beingDisciplined))
}
