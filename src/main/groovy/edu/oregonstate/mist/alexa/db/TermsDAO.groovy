package edu.oregonstate.mist.alexa.db

import org.apache.http.client.HttpClient

class TermsDAO {
    Map<String, String> alexaConfiguration

    Requests requests

    TermsDAO(HttpClient httpClient, Map<String, String> alexaConfiguration) {
        this.alexaConfiguration = alexaConfiguration
        requests = new Requests(httpClient, alexaConfiguration['oauth2Url'].toString())
    }

    void ping() {
        println "pong"
        println requests.get(alexaConfiguration['termsUrl'].toString())
    }
}
