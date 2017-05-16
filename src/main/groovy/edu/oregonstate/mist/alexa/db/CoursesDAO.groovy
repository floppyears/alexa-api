package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

import java.time.LocalTime
import java.time.format.DateTimeFormatter

@InheritConstructors
class CoursesDAO extends ApiDAO {
    private final String bannerTimePattern = "HHmm"

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

    private LocalTime parseTime (String inputTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
        LocalTime.parse(inputTime, formatter)
    }

    private Boolean withinTimeFrame (LocalTime desiredTime,
                                     LocalTime lowerLimit,
                                     LocalTime upperLimit) {
        Boolean isWithinTimeFrame = false

        if ((desiredTime.isAfter(lowerLimit) && desiredTime.isBefore(upperLimit)) ||
                lowerLimit == desiredTime) {
            isWithinTimeFrame = true
        }
        println(isWithinTimeFrame.toString())
        isWithinTimeFrame
    }

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

    String formatTimeForResponse (String inputTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma")
        LocalTime inputTime = parseTime(inputTimeString, bannerTimePattern)
        inputTime.format(formatter)
    }

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
        String response = "Does ${course["attributes"]["courseTitle"]} sound like fun? " +
                availableSpacesResponse +
                scheduleResponse +
                "The CRN is <say-as interpret-as=\"digits\">${course["id"]}</say-as>."

        response
    }

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
