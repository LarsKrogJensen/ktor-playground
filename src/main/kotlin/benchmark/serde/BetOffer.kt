package benchmark.serde

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class BetOffer(
    override val id: Long,
    override val version: Long,
    val eventId: Long,
    val type: BetOfferType,
    val outcomes: List<Outcome>,
    val cashoutStatus: CashoutStatus,
    val onSite: Boolean?,
    val suspended: Boolean,
    val offeredLive: Boolean,
    val offeredPrematch: Boolean,
) : Entity

@Serializable
data class Outcome(
    val id: Long,
    val version: Long,
    @Serializable(with = BigDecimalSerializer::class)
    val odds: BigDecimal?,
    @Serializable(with = BigDecimalSerializer::class)
    val probability: BigDecimal?,
    @Serializable(with = BigDecimalSerializer::class)
    val winProbability: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val exactLineProbability: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val drawProbability: BigDecimal? = null,
    val oddsVersion: Int?,
    val bettable: Boolean,
    val cashoutStatus: CashoutStatus,
)

enum class CashoutStatus {
    UNKNOWN,
    DISABLED,
    ENABLED,
    SUSPENDED,
}


enum class BetOfferType {
    UNKNOWN,
    THREE_WAY,
    TWO_WAY,
    TWO_WAY_DEAD_HEAT,
    TWO_WAY_DNB,
    OVER_UNDER,
    ODD_EVEN,
    TWO_WAY_HANDICAP,
    RACE_TO_VALUE,
    RACE_TO_VALUE_TWO_WAY,
    RACE_TO_VALUE_DNB,
    DOUBLE_CHANCE,
    THREE_WAY_HANDICAP,
    HALFTIME_FULLTIME,
    LAST_OCCURRENCE,
    LAST_OCCURRENCE_DNB,
    OCCURRENCE_NUMBER,
    OCCURRENCE_NUMBER_DNB,
    CORRECT_SCORE,
    POSITION,
    OUTRIGHT,
    ASIAN_HANDICAP,
    SIDE_BET,
    HEAD_TO_HEAD,
    SCORE_CAST,
    SCORER,
    YES_NO,
    MULTI_POSITION,
    WIN_CAST,
    ASIAN_OVER_UNDER,
    OCCURRENCE_TO_HAPPEN,
    FOUR_WAY,
    WINNING_MARGIN,
    PLAYER_OCCURRENCE_NUMBER,
    PLAYER_LAST_OCCURRENCE,
    PLAYER_OCCURRENCE_LINE,
    OCCURRENCE_METHOD,
}
