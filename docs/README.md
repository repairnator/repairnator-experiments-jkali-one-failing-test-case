# uportal-messages

This messaging microservice is intended for use with [uportal-home](https://github.com/uPortal-Project/uportal-home).

uPortal messages include both notifications and announcements, and can be tailored for audiences as small as one person. 

## What is a message?

A message is an announcement or notification which can be targeted to an entire user population, a subset of the population, or a single user. 

This microservice processes messages in json form. The details of how that json is structured are found in the uportal-app-framework project's [documentation](https://github.com/uPortal-Project/uportal-app-framework/blob/master/docs/messaging-implementation.md). 

## Configuration

Set the source of your messages in the ``application.properties`` file. In this example, we've selected a json file in our resources directory. 
``` javascript
message.source=classpath:messages.json
```

## To Build

This project uses maven. ```$ mvn package ``` will build a warfile for deployment. 

To run locally, ```$ mvn spring-boot:run ``` will compile and run this microservice. 

## Endpoints



### {*/*}

calls:
```java 
    @RequestMapping("/")
    public @ResponseBody
    void index(HttpServletResponse response)   
```
returns:

```
{"status":"up"}
```
description:
This endpoint returns a small json object indicating that the status of the application is healthy. 

### {*/messages*}

calls:
``` java
   @RequestMapping(value="/messages", method=RequestMethod.GET)
    public @ResponseBody void messages(HttpServletRequest request,
        HttpServletResponse response) 
```
returns:
A JSON object containing messages filtered to the viewing user and the current context.

description:
Intended as the primary endpoint for servicing typical users. The idea is to move all the complication of message 
resolution server-side into this microservice so that a typical client can request this data and uncritically render it.

Currently filters to:

+ Applicable to the current local date-time. (Messages can have not-before and not-after date-times,
specified in ISO format, as in `2018-02-14` or `2018-02-14:09:46:00`), AND
+ Applicable to the user's groups. (Messages can be limited to user groups.)

Expectations:

+ `isMemberOf` header conveying semicolon-delimited group memberships. (This practice is typical in UW-Madison 
Shibboleth SP implementation.) Fails gracefully yet safely: if this header is not present, considers the user a member 
of no groups.

Versioning:
The details of the filtering are NOT a semantically versioned aspect of the API. That is to say, what is versioned
here is that `/messages` returns the messages appropriate for the requesting user. Increasing sophistication in what 
"appropriate" means is not a breaking change.

Security:
WARNING: Does not apply any access control other than filtering to messages applicable to the user's groups. If 
additional access control is needed (it may not be needed), implement it at the container layer. 

### {*/allMessages*}
calls:
``` java
  @RequestMapping(value = "/allMessages", method = RequestMethod.GET)
  public @ResponseBody
  void messages(HttpServletRequest request,
    HttpServletResponse response)
```

returns:
A JSON object, containing every known message, regardless of all criteria. 

description:
Intended as an administrative or troubleshooting view on the data.

Security:
WARNING: Does not apply any access control. Implement access control at the container layer. Whatever access control
is appropriate, apply it to the `/allMessages` path at e.g. the `httpd` layer.
