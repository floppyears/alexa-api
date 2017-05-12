package edu.oregonstate.mist.alexa

import com.fasterxml.jackson.annotation.JsonProperty
import edu.oregonstate.mist.api.Configuration
import edu.oregonstate.mist.api.Credentials
import io.dropwizard.client.HttpClientConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

class AlexaConfiguration extends Configuration {

    @JsonProperty('apis')
    @NotNull
    @Valid
    Map<String, String> alexaConfiguration

    @Valid
    @NotNull
    HttpClientConfiguration httpClientConfiguration
}
