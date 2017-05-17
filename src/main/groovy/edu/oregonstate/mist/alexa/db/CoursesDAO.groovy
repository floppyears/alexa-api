package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

import java.time.LocalTime
import java.time.format.DateTimeFormatter

@InheritConstructors
class CoursesDAO extends ApiDAO {
    private final String bannerTimePattern = "HHmm"

    /**
     * Get term code from a year and season.
     * Term code is used in URI of catalog API request.
     * @param year
     * @param season
     * @return
     */
    private String getTermCode (int year, String season) {
        String termCode
        switch (season) {
            case "summer": termCode = (++year).toString() + "00"
                break
            case "fall": termCode = (++year).toString() + "01"
                break
            case "winter": termCode = year.toString() + "02"
                break
            case "spring": termCode = year.toString() + "03"
                break
        }
        termCode
    }

    /**
     * Get a LocalTime object from a time string and pattern string.
     * @param inputTime
     * @param pattern
     * @return
     */
    private LocalTime parseTime (String inputTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
        LocalTime.parse(inputTime, formatter)
    }

    /**
     * Check if a time occurs within two times.
     * @param desiredTime
     * @param lowerLimit
     * @param upperLimit
     * @return
     */
    private Boolean withinTimeFrame (LocalTime desiredTime,
                                     LocalTime lowerLimit,
                                     LocalTime upperLimit) {
        Boolean isWithinTimeFrame = false

        if ((desiredTime.isAfter(lowerLimit) && desiredTime.isBefore(upperLimit)) ||
                lowerLimit == desiredTime) {
            isWithinTimeFrame = true
        }

        isWithinTimeFrame
    }

    /**
     * Returns a list of class json objects that satisfy schedule criteria.
     * @param jsonApiResponse
     * @param desiredTime
     * @param dayOfWeek
     * @return
     */
    private def getPossibleClasses (def jsonApiResponse,
                                    LocalTime desiredTime,
                                    String dayOfWeek) {
        def possibleClasses = []

        jsonApiResponse["data"].each { pacClass ->
            Boolean addClass = false

            if (pacClass["attributes"]["openSection"].toString() == "true" &&
                    ((pacClass["attributes"]["maximumEnrollment"] -
                            pacClass["attributes"]["enrollment"]) > 0)) {
                pacClass["attributes"]["meetingTimes"].each {
                    if (it[dayOfWeek]) {
                        LocalTime startTime = parseTime(it["startTime"].toString(),
                                bannerTimePattern)
                        LocalTime endTime = parseTime(it["endTime"].toString(),
                                bannerTimePattern)
                        if (withinTimeFrame(desiredTime, startTime, endTime)) {
                            addClass = true
                        }
                    }
                }
            }

            if (addClass) {
                possibleClasses.add(pacClass)
            }
        }
        possibleClasses
    }

    /**
     * Format time so alexa reads the time correctly.
     * @param inputTimeString
     * @return
     */
    String formatTimeForResponse (String inputTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma")
        LocalTime inputTime = parseTime(inputTimeString, bannerTimePattern)
        inputTime.format(formatter)
    }

    /**
     * Returns an alexa response phrase for the days that a class meets
     * @param course
     * @return
     */
    String getDayScheduleResponse (def course) {
        def daysOfTheWeek = [
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "saturday",
                "sunday"
        ]
        def classDays = []

        // TODO: handle classes that have multiple meeting times
        daysOfTheWeek.each {
            if (course["attributes"]["meetingTimes"][0][it]) {
                classDays.add(it)
            }
        }

        "It meets " + joinList(classDays)
    }

    /**
     * Generates a response that will be spoken by alexa.
     * @param course
     * @return
     */
    String generateResponse (def course) {
        Integer availableSpaces = course["attributes"]["maximumEnrollment"] -
                course["attributes"]["enrollment"]

        String availableSpacesResponse
        if (availableSpaces > 1) {
            availableSpacesResponse = "There are ${availableSpaces} spaces "
        } else {
            availableSpacesResponse = "There is ${availableSpaces} space "
        }
        availableSpacesResponse += "left to register for. "

        // TODO: handle classes that have multiple meeting times
        String startTime = formatTimeForResponse(
                course["attributes"]["meetingTimes"][0]["startTime"].toString())
        String endTime = formatTimeForResponse(
                course["attributes"]["meetingTimes"][0]["endTime"].toString())
        String timeSchedule = "${startTime} to ${endTime}. "

        String scheduleResponse = getDayScheduleResponse(course) + " from " + timeSchedule

        // TODO: handle class names that contain roman numerals
        String response = "<speak>" +
                "Does ${course["attributes"]["courseTitle"]} sound like fun? " +
                availableSpacesResponse +
                scheduleResponse +
                "The CRN is <say-as interpret-as=\"digits\">${course["id"]}</say-as>." +
                "</speak>"

        response
    }

    /**
     * Makes call to catalog API and calls functions to manipulate response.
     * @param slots
     * @return
     */
    String randomPAC (def slots) {
        String requestUrl = alexaConfiguration['catalogUrl'].toString()
        String termCode = getTermCode(slots["year"]["value"] as int,
                slots["term"]["value"].toString())

        requestUrl += "?term=" + termCode
        requestUrl += "&subject=PAC"
        requestUrl += "&page[size]=1000"

        def jsonApiObject = requests.get(requestUrl)
        String dayOfWeek = slots["dayofweek"]["value"].toString().toLowerCase()
        LocalTime desiredTime = parseTime(slots["time"]["value"].toString(), "HH:mm")

        def possibleCourses = getPossibleClasses(jsonApiObject, desiredTime, dayOfWeek)

        if (possibleCourses.isEmpty()) {
            return "No PAC classes fit into that schedule."
        }

        def randomCourse =  possibleCourses[new Random().nextInt(possibleCourses.size())]

        generateResponse(randomCourse)
    }
}
