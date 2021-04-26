package com.example.routes

import com.example.data.collections.Track
import com.example.data.deleteTracks
import com.example.data.getTrack
import com.example.data.insertTrack
import com.example.data.requests.DeleteTracksRequest
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.tracksRoute() {

    route("/insertTrack") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {

                    val track = try {
                        call.receive<Track>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    if (insertTrack(track, track.coordinates.last().latitude, track.coordinates.last().longitude)) {
                        call.respond(OK)
                    } else {
                        call.respond(Conflict)
                    }


                }
            }
        }
    }


    route("/deleteTrack") {
        authenticate("owners") {
            post {
                withContext(Dispatchers.IO) {

                    val track = try {
                        call.receive<DeleteTracksRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(BadRequest)
                        return@withContext
                    }

                    if (deleteTracks(track.orderId)) {
                        call.respond(OK)
                    } else {
                        call.respond(Conflict)
                    }


                }
            }
        }
    }

    route("/getTrack/{order}") {
        get {
            withContext(Dispatchers.IO) {
                val orderId = call.parameters["order"]!!

                val trackPoints = getTrack(orderId)

                if (trackPoints != null) {
                    call.respond(OK, trackPoints)
                }

            }
        }
    }

}