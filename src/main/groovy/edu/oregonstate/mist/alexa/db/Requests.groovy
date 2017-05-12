package edu.oregonstate.mist.alexa.db

import org.apache.http.HttpEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils

class Requests {
    HttpClient httpClient

    String oauth2Url

    Requests(HttpClient httpClient, String oauth2Url) {
        this.httpClient = httpClient
        this.oauth2Url = oauth2Url
    }

    def get(String url) {
        "benny is alive!"
    }

    String getAccessToken() {
        CloseableHttpResponse response

        def data = new HashMap<>()
        String parameters = "secret"

        try {
            HttpPost post = new HttpPost(oauth2Url)
            post.setHeader("Content-Type", "application/x-www-form-urlencoded")
            post.setHeader("Accept", "application/json")

            post.setEntity(new StringEntity(parameters))

            response = httpClient.execute(post)
            HttpEntity entity = response.getEntity()
            def entityString = EntityUtils.toString(entity)

//            data = this.mapper.readValue(entityString,
//                    new TypeReference<HashMap<String, LibraryHours>>() {
//                    })

            EntityUtils.consume(entity)
        } finally {
            response?.close()
        }
        data
    }
}
