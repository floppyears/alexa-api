package edu.oregonstate.mist.alexa

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.alexa.core.AlexaResponse
import edu.oregonstate.mist.alexa.core.OutputSpeech
import edu.oregonstate.mist.alexa.core.Response
import edu.oregonstate.mist.alexa.db.CoursesDAO
import edu.oregonstate.mist.alexa.db.DirectoryDAO
import edu.oregonstate.mist.alexa.db.LocationsDAO
import edu.oregonstate.mist.alexa.db.TermsDAO
import edu.oregonstate.mist.api.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
    private static final Logger LOGGER = LoggerFactory.getLogger(AlexaResource.class)

    TermsDAO termsDAO
    DirectoryDAO directoryDAO
    LocationsDAO locationsDAO
    CoursesDAO coursesDAO

    AlexaResource(TermsDAO termsDAO,
                  DirectoryDAO directoryDAO,
                  LocationsDAO locationsDAO,
                  CoursesDAO coursesDAO) {
        this.termsDAO = termsDAO
        this.directoryDAO = directoryDAO
        this.locationsDAO = locationsDAO
        this.coursesDAO = coursesDAO
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    AlexaResponse bennySkill(def alexaRequest) {
        //TODO: Handle LaunchRequest and SessionEndedRequest
        if (alexaRequest["request"]["type"].toString() == "IntentRequest") {
            return intentResponse(alexaRequest)
        }
    }

    private AlexaResponse intentResponse(def intentRequest) {
        LOGGER.info(intentRequest.toString())

        String intent = intentRequest["request"]["intent"]["name"].toString()
        def slots = intentRequest["request"]["intent"]["slots"]
        String responseSpeech = "I don't know what your intent was."
        String responseSpeechSsml
        String responseType = "PlainText"

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
            case "PAC": responseSpeech = null
                responseSpeechSsml = coursesDAO.randomPAC(slots)
                responseType = "SSML"
                break
        }

        new AlexaResponse(
                response: new Response(
                        outputSpeech: new OutputSpeech(
                                type: responseType,
                                text: responseSpeech,
                                ssml: responseSpeechSsml
                        )
                )
        )
    }
}
