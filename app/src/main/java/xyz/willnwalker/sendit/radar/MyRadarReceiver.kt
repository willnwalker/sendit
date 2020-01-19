package xyz.willnwalker.sendit.radar

import android.content.Context
import android.location.Location
import io.radar.sdk.Radar
import io.radar.sdk.RadarReceiver
import io.radar.sdk.model.RadarEvent
import io.radar.sdk.model.RadarUser

class MyRadarReceiver: RadarReceiver() {

    override fun onEventsReceived(context: Context, events: Array<RadarEvent>, user: RadarUser) {
        // do something with context, events, user
        println("User: " + user)
    }

    override fun onLocationUpdated(context: Context, location: Location, user: RadarUser) {
        // do something with context, location, user
    }

    override fun onError(context: Context, status: Radar.RadarStatus) {
        // do something with context, status
    }

}