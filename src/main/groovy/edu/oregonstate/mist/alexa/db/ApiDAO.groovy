package edu.oregonstate.mist.alexa.db

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.HttpClient

class ApiDAO {
    Map<String, String> alexaConfiguration
    Requests requests
    ObjectMapper mapper = new ObjectMapper()

    public ApiDAO(HttpClient httpClient, Map<String, String> alexaConfiguration) {
        this.alexaConfiguration = alexaConfiguration
        requests = new Requests(httpClient, alexaConfiguration['oauth2Url'].toString(),
                alexaConfiguration['oauth2Params'].toString())
    }

    /**
     * Takes a list and joins the elements using commas for all the elements, but uses
     * "and" for the last element.
     *
     * @param list
     * @return
     */
    static String joinList(def list) {
        if (!list) {
             return "Sorry, the list is empty"
        }

        def listText = ""
        list.each {
            if (it == list.last()) {
                listText += " and "
            } else if (it != list.first()) {
                listText += ", "
            }

            listText += it
        }

        listText
    }
}
