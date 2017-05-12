package edu.oregonstate.mist.alexa.db

import org.apache.http.client.HttpClient

class ApiDAO {
    Map<String, String> alexaConfiguration
    Requests requests

    public ApiDAO(HttpClient httpClient, Map<String, String> alexaConfiguration) {
        this.alexaConfiguration = alexaConfiguration
        requests = new Requests(httpClient, alexaConfiguration['oauth2Url'].toString(),
                alexaConfiguration['oauth2Params'].toString())
    }
}
