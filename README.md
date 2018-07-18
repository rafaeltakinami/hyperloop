# hyperloop

[![Build Status](https://travis-ci.org/GuiaBolso/hyperloop.svg?branch=master)](https://travis-ci.org/GuiaBolso/hyperloop)

Hyperloop is a simple library used to validate and send [events](https://github.com/GuiaBolso/events-protocol) to a specified destination.

The library is divided in 2 modules: validator and transport

## Validator

The Validator module contains all the code used to check that an event respects a specified schema. For example the schema
and event defined below are a valid representation of the usage of the lib:

***schema:***
```yaml
schema:
 version: 1

event: 
  name: example:event
  version: 1

types:
 $File:
   owner:
     of: $User
   name:
     of: string
 $User:
   name:
     of: string
     is:
       - required
       - encripted
   birthdate:
     of: date(yyyy/mm/dd)
     is:
       - required
   gender:
     of: string

validation:
 payload:
   users:
     of: array($User)
   file:
     of: $File
 identity:
   userId:
     of: long
     is:
      - required
 metadata:
   origin:
     of: string
     is:
      - required
```

***event:***
```json
{
  "name": "example:event",
  "version": 1,
  "payload": {
    "users": [
      {
        "name": "Thiago Bardella",
        "birthdate": "22/05/1990",
        "gender": "male"
      },
      {
        "name": "Bruno Ortiz",
        "birthdate": "15/02/1990",
        "gender": "male"
      }
    ],
    "file": {
      "name": "document.png",
      "owner": {
        "name": "Thiago Bardella",
        "birthdate": "22/05/1990",
        "gender": "male"
      }
    }
  },
  "identity":{
    "userId": 12345
  },
  "metadata":{
    "origin": "system_xpto"
  }
}
```

### Usage

//TODO: insert usage of validator

## Transport

The transport module is used to validate and send the event to a specified destination. Currently the supported destination are:

* AWS SQS

### Usage

```kotlin
val event = ...
val sqsTransport = SQSTransport(sqs, "queue-url")
val hyperloop = Hyperloop(sqsTransport, NoOpCryptographyEngine())

val result = hyperloop.offer(event)
println("Message id: ${result.messageId}")
```

The Hyperloop class receives to interfaces as parameters, the first is the transport that it will use to send the event
the later one is the cryptography engine that it will be used to encrypt any data sent by the transport.