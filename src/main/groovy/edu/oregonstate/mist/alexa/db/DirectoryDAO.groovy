package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

@InheritConstructors
class DirectoryDAO extends ApiDAO {
    public static final String NO_RESULTS = "Sorry, I couldn't find that"

    String getInfo(def slots) {
        String query = slots["query"]["value"].toString()
        String queryEncoded = URLEncoder.encode(query, "UTF-8")
        String fieldNameEnglish = slots["field"]["value"]
        String fieldName

        switch (fieldNameEnglish) {
            case "email": fieldName = "emailAddress"
                break
            case "phone number": fieldName = "officePhoneNumber"
                break
        }

        def directoryPrefix = alexaConfiguration['directoryUrl'].toString()
        String url = directoryPrefix + queryEncoded
        def jsonApiObject = requests.get(url, true)

        // check if it'NO_RESULTS a 404 or empty body
        if (!jsonApiObject) {
            return NO_RESULTS
        }

        if (!jsonApiObject['data']) {
            return NO_RESULTS
        }

        //@todo: handle multiple results
        def fieldValue = jsonApiObject['data'][0]['attributes'][fieldName]

        "${query}'s $fieldNameEnglish is $fieldValue"
    }
}
