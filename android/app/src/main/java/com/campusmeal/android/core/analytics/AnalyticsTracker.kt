package com.campusmeal.android.core.analytics

/** Analytics event. Properties must never contain tokens, credentials or precise location. */
data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, String> = emptyMap(),
)

/** Submission boundary for analytics events sent to the CampusMeal API. */
interface AnalyticsTracker {
    suspend fun track(event: AnalyticsEvent)
}

/** Discards events until the CampusMeal API analytics endpoint is defined. */
class NoOpAnalyticsTracker : AnalyticsTracker {
    override suspend fun track(event: AnalyticsEvent) = Unit
}
