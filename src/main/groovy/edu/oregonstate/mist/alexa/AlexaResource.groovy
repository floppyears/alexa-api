package edu.oregonstate.mist.alexa

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.alexa.core.AlexaResponse
import edu.oregonstate.mist.alexa.core.OutputSpeech
import edu.oregonstate.mist.alexa.core.Response
import edu.oregonstate.mist.alexa.db.DirectoryDAO
import edu.oregonstate.mist.alexa.db.LocationsDAO
import edu.oregonstate.mist.alexa.db.TermsDAO
import edu.oregonstate.mist.api.Resource

import javax.annotation.security.PermitAll
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("alexa")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@groovy.transform.TypeChecked
class AlexaResource extends Resource {
    TermsDAO termsDAO
    DirectoryDAO directoryDAO
    LocationsDAO locationsDAO

    AlexaResource(TermsDAO termsDAO, DirectoryDAO directoryDAO, LocationsDAO locationsDAO) {
        this.termsDAO = termsDAO
        this.directoryDAO = directoryDAO
        this.locationsDAO = locationsDAO
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    AlexaResponse bennySkill(def alexaRequest) {
        String intent = alexaRequest["request"]["intent"]["name"].toString()
        def slots = alexaRequest["request"]["intent"]["slots"]
        String responseSpeech = "I don't know what your intent was."

        switch (intent) {
            case "HelloWorld": responseSpeech = "Hello hackathon 2017!"
                break
            case "Terms": responseSpeech = termsDAO.getOpenTerms()
                break
            case "Directory": responseSpeech = directoryDAO.getInfo(slots)
                break
            case "Restaurants": responseSpeech = locationsDAO.getOpenRestaurants()
                break
            case "IsOpen": responseSpeech = locationsDAO.openHoursForLocation(slots)
                break
            case "Nearby": responseSpeech = locationsDAO.getNearbyRestaurants(1.0)
                //withinDistance parameter expects miles.
                break
        }

        new AlexaResponse(
                response: new Response(
                        outputSpeech: new OutputSpeech(
                                type: "PlainText",
                                text: responseSpeech
                        )
                )
        )
    }
}
