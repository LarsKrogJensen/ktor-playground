package benchmark.serde

import java.math.BigDecimal

const val defaultTestBetOfferId = 2222L
const val defaultTestOutcomeId = 3333L

fun defaultTestBetOffer() = BetOffer(
    id = defaultTestBetOfferId,
    eventId = defaultTestEventId,
    type = BetOfferType.THREE_WAY,
    version = 1L,
    outcomes = listOf(defaultTestOutcome()),
    cashoutStatus = CashoutStatus.ENABLED,
    onSite = true,
    suspended = false,
    offeredLive = true,
    offeredPrematch = true,
)

fun defaultTestOutcome() = Outcome(
    id = defaultTestOutcomeId,
    version = 1L,
    odds = BigDecimal("5.25"),
    probability = BigDecimal("0.16864336"),
    oddsVersion = 1,
    bettable = true,
    cashoutStatus = CashoutStatus.ENABLED,
)
