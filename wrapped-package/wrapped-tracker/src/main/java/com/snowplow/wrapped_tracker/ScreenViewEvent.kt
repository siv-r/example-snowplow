package com.snowplow.wrapped_tracker

import androidx.annotation.RestrictTo
import com.snowplowanalytics.snowplow.event.SelfDescribing
import com.snowplowanalytics.snowplow.payload.SelfDescribingJson
import org.jetbrains.annotations.NotNull

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ScreenViewEvent(@NotNull timeOnScreen: Int, @NotNull scrollDepth: Int, @NotNull screenName: String) {
    val data: SelfDescribing
    private val json: SelfDescribingJson
    private val properties = mutableMapOf<String, Any>()

    init {
        properties["time_on_screen"] = timeOnScreen
        properties["scroll_depth"] = scrollDepth
        properties["screenName"] = screenName
        json = SelfDescribingJson(
            "iglu:com.snowplow.custom/screen_view_event/jsonschema/1-0-0",
            properties
        )
        data = SelfDescribing(json)
    }
}
