package edu.oregonstate.mist.alexa

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.alexa.core.AlexaResponse
import edu.oregonstate.mist.alexa.core.OutputSpeech
import edu.oregonstate.mist.alexa.core.Response
import edu.oregonstate.mist.alexa.db.DirectoryDAO
import edu.oregonstate.mist.alexa.db.TermsDAO
import edu.oregonstate.mist.api.Resource

import javax.annotation.security.PermitAll
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

    AlexaResource(TermsDAO termsDAO, DirectoryDAO directoryDAO) {
        this.termsDAO = termsDAO
        this.directoryDAO = directoryDAO
    }

    @Timed
    @POST
    @Path("hello")
    AlexaResponse hello() {
        new AlexaResponse(
                response: new Response(
                        outputSpeech: new OutputSpeech(
                                type: "PlainText",
                                text: "Hello hackathon 2017!"
                        )
                )
        )
    }

    @Timed
    @POST
    @Path("world")
    AlexaResponse world() {
        println "resource: " + directoryDAO.getInfo("vanvliet", "officePhoneNumber")
        new AlexaResponse(
                response: new Response(
                        outputSpeech: new OutputSpeech(
                                type: "PlainText",
                                text: "Hello hackathon 2017!"
                        )
                )
        )
    }

}
