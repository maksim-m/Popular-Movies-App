Popular Movies App
==================

[![Build Status](https://travis-ci.org/maksim-m/Popular-Movies-App.svg?branch=master)](https://travis-ci.org/maksim-m/Popular-Movies-App)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9d71713560374c938dba8a476ce8debf)](https://www.codacy.com/app/maksim-m/Popular-Movies-App)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/9d71713560374c938dba8a476ce8debf)](https://www.codacy.com/app/maksim-m/Popular-Movies-App)

A simple Android app, that helps user to discover movies. 
This is Project 1 & Project 2 of Udacity's Android Developer Nanodegree.

**Features:**

- Discover the most popular, the highest rated and the most rated movies
- Watch movie trailers and teasers
- Read reviews from other users
- Mark movies as favorites
- Search for movies
- Offline work
- Material design
- UI optimized for phone and tablet

**Download:**

You can download APK [on releases page][5].

Screenshots
-----------

<img width="45%" src=".github/screen1.png" />
<img width="45%" src=".github/screen2.png" />

<img width="89%" src=".github/screen3.png" />

<img width="90%" src=".github/screen4.png" />

Developer setup
---------------

### Requirements

- Java 8
- Latest version of Android SDK and Android Build Tools

### API Key

The app uses themoviedb.org API to get movie information and posters. You must provide your own [API key][1] in order to build the app.

Just put your API key into `~/.gradle/gradle.properties` file (create the file if it does not exist already):

```gradle
MY_MOVIE_DB_API_KEY="abc123"
```

### Building

You can build the app with Android Studio or with `./gradlew assembleDebug` command.

### Testing

This project integrates a combination of [local unit tests][2], [instrumented tests][3] and [code analysis tools][4].

Just run `build.sh` to ensure that project code is valid and stable.
This will run local unit tests on the JVM, instrumented tests on connected device (or emulator) and analyse code with Checkstyle, Findbugs and PMD.

License
-------

    Copyright 2016 Maksim Moiseikin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://www.themoviedb.org/documentation/api
[2]: app/src/test/
[3]: app/src/androidTest/
[4]: quality/
[5]: https://github.com/maksim-m/Popular-Movies-App/releases
