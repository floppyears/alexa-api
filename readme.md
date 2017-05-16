# Alexa API

Wrapper API to generate responses for Amazon Echo. This API was created during as a hackathon project, with some minor additions created afterwards.

## Resources

### POST: /alexa
This endpoint serves a response based on the intent name and slots (if applicable) in the body of the request.
The body of the request should be formatted per [Amazon's specification](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/alexa-skills-kit-interface-reference).
The Alexa API will return a JSON response per the [same specification](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/alexa-skills-kit-interface-reference#response-body-syntax).

For a list of intents able to be used with the Alexa API, see the [intent schema](intent-schema.json). The intent schema should be used in Amazon's Alexa Skills Kit.

## Setup

### Generate Keys

HTTPS is required for Web APIs in development and production. Use `keytool(1)` to generate public and private keys.

Generate key pair and keystore:

    $ keytool \
        -genkeypair \
        -dname "CN=Jane Doe, OU=Enterprise Computing Services, O=Oregon State University, L=Corvallis, S=Oregon, C=US" \
        -ext "san=dns:localhost,ip:127.0.0.1" \
        -alias doej \
        -keyalg RSA \
        -keysize 2048 \
        -sigalg SHA256withRSA \
        -validity 365 \
        -keystore doej.keystore

Export certificate to file:

    $ keytool \
        -exportcert \
        -rfc \
        -alias "doej" \
        -keystore doej.keystore \
        -file doej.pem

Import certificate into truststore:

    $ keytool \
        -importcert \
        -alias "doej" \
        -file doej.pem \
        -keystore doej.truststore

## Gradle

This project uses the build automation tool Gradle. Use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to download and install it automatically:

    $ ./gradlew

The Gradle wrapper installs Gradle in the directory `~/.gradle`. To add it to your `$PATH`, add the following line to `~/.bashrc`:

    $ export PATH=$PATH:/home/user/.gradle/wrapper/dists/gradle-2.4-all/WRAPPER_GENERATED_HASH/gradle-2.4/bin

The changes will take effect once you restart the terminal or `source ~/.bashrc`.

## Tasks

List all tasks runnable from root project:

    $ gradle tasks

### IntelliJ IDEA

Generate IntelliJ IDEA project:

    $ gradle idea

Open with `File` -> `Open Project`.

### Configure

Copy [configuration-example.yaml](configuration-example.yaml) to `configuration.yaml`. Modify as necessary, being careful to avoid committing sensitive data.

### Build

Build the project:

    $ gradle build

JARs [will be saved](https://github.com/johnrengelman/shadow#using-the-default-plugin-task) into the directory `build/libs/`.

### Run

Run the project:

    $ gradle run

## Incorporate Updates from the Skeleton

Fetch updates from the skeleton:

    $ git fetch skeleton

Merge the updates into your codebase as before.
Note that changes to CodeNarc configuration may introduce build failures.

    $ git checkout feature/abc-124-branch
    $ git merge skeleton/master
    ...
    $ git commit -v

