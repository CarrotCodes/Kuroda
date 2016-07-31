# Kuroda

## Introduction

Kuroda is a Jenkins build status plugin that posts notifications to IRC channels.

## Features

* Supports Pipelines as well as Freestyle projects
* Queues messages during a connect 
* Sends messages for started, success, failed, aborted and unstable
* Java for the Jenkins integration layer, Kotlin for the rest

Planned:

* Configuration / reconnection
* Decide whether to add more pipeline commands, or have a choice parameter, so both types of job can share messages
* Throttling
* Validation
* Overriding globally defined options in jobs?
* General tidying

Not planned:

* Scheduling builds from IRC

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).
