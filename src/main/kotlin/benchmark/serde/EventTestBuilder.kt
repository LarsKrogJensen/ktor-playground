package benchmark.serde

import com.kambi.betvalidation.satellite.domain.*
import java.time.OffsetDateTime
import java.time.ZoneOffset

const val defaultTestEventId = 1234L
const val defaultTestEventGroupId = 99L

fun defaultTestEvent() = Event(
    id = defaultTestEventId,
    version = 1,
    name = "Liverpool - Manchester",
    sportId = "FOOTBALL",
    type = EventType.MATCH,
    status = EventStatus.NOT_PLAYED,
    eventGroupId = defaultTestEventGroupId,
    startDate = OffsetDateTime.of(2025, 10, 25, 10, 0, 0, 0, ZoneOffset.UTC),
    live = false,
    participants = listOf(
        defaultTestEventParticipant().copy(participantId = 1),
        defaultTestEventParticipant().copy(participantId = 2),
    ),
    eventTreePath = listOf(
        EventGroup(1, "Football"),
        EventGroup(2, "England"),
        EventGroup(defaultTestEventGroupId, "Premier League"),
    )
)


fun defaultTestEventParticipant() = EventParticipant(
    participantId = 1,
    type = ParticipantType.TEAM,
    scratched = false,
    teamParticipants = listOf(
        defaultTestEventTeamParticipant().copy(participantId = 12),
        defaultTestEventTeamParticipant().copy(participantId = 13),
    )
)

fun defaultTestEventTeamParticipant() = EventTeamParticipant(
    participantId = 12,
    version = 1,
)
