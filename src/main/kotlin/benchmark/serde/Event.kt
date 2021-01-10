package com.kambi.betvalidation.satellite.domain

import benchmark.serde.BigDecimalSerializer
import benchmark.serde.Entity
import benchmark.serde.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Event(
    override val id: Long,
    override val version: Long,
    val name: String,
    val sportId: String,
    val type: EventType,
    val status: EventStatus,
    val eventGroupId: Long,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startDate: OffsetDateTime,
    val live: Boolean,
    val participants: List<EventParticipant>,
    val eventTreePath: List<EventGroup>,
) : Entity

@Serializable
data class EventGroup(
    val id: Long,
    val name: String? = null
)

@Serializable
data class EventParticipant(
    val participantId: Long,
    val type: ParticipantType,
    val scratched: Boolean,
    val teamParticipants: List<EventTeamParticipant>,
)

@Serializable
data class EventTeamParticipant(
    val participantId: Long,
    val version: Long,
)

enum class EventType {
    UNKNOWN,
    MATCH,
    COMPETITION,
}

enum class EventStatus {
    UNKNOWN,
    PENDING,
    NOT_PLAYED,
    SETTLED,
    DELETED,
}

enum class ParticipantType {
    UNKNOWN,
    PARTICIPANT,
    TEAM,
    LABEL,
}
