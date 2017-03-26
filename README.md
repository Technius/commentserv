# Commentserv

This is a simple commenting system built with Scala and Akka HTTP, with
PostgreSQL as the database. Do not use this in production; it uses way too much
memory for what it should accomplish.

## Running

Enter interactive mode in `sbt`, and then use `sbt re-start`.

For convenience, a `docker-compose.yml` is provided to quickly start up
postgresql instances.

## API Endpoints

| Endpoint | Method | Example | Description |
| -------- | ------ | ------- | ----------- |
| /threads | GET | /threads | Lists all threads (no comments) |
| /threads | POST | /threads?title=Title&slug=slug | Creates a new thread |
| /threads/$id | GET | /threads/1 | Retrieves a thread and its comments |

## License
Copyright 2017 Bryan Tan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
