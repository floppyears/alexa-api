package edu.oregonstate.mist.alexa

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.alexa.core.AlexaResponse
import edu.oregonstate.mist.alexa.core.OutputSpeech
import edu.oregonstate.mist.alexa.core.Response
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

    AlexaResource(TermsDAO termsDAO) {
        this.termsDAO = termsDAO
    }

    @Timed
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    AlexaResponse bennySkill(def alexaRequest) {
        String intent = alexaRequest["request"]["intent"]["name"].toString()
        String responseSpeech = "I don't know what your intent was."

        switch (intent) {
            case "HelloWorld": responseSpeech = "Hello hackathon 2017!"
                break
            case "Terms": responseSpeech = termsDAO.getOpenTerms()
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

    @Timed
    @POST
    @Path("world")
    AlexaResponse world() {
        termsDAO.ping()
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
