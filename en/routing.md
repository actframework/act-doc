# Routing

ActFramework support building routing table in three different ways:

1. Through annotation put on an action handler method
1. Through `resources/routes.conf` file
1. Through configuration API

## Routing with actions handler method annotation

The following annotation are supported:

1. `org.osgl.mvc.annotation.Action`
1. `org.osgl.mvc.annotation.GetAction`
1. `org.osgl.mvc.annotation.PostAction`
1. `org.osgl.mvc.annotation.PutAction`
1. `org.osgl.mvc.annotation.DeleteAction`

Sample code:

```java
@GetAction("/profile/{id}")
public Profile getProfile(String id) {
    return dao.findById(id);
}

@PostAction("/profile")
public void createProfile (Profile profile) {
    dao.save(profile);
}

@PutAction("/profile/{id}/address")
public void updateAddress(String id, Address address) {
    Profile profile = dao.findById(id);
    notFoundIfNull(profile);
    profile.setAddress(address);
    profile.update(profile);
}

@DeleteAction("/profile/{id}")
public void deleteProfile(String id) {
    dao.deleteById(id);
}
```

**Tips**: Use `Action` annotation when an action handler needs to handler request through multiple HTTP methods:

```java
@Action("/", methods = {H.Method.GET, H.Method.POST})
public void home() {}
```

**Tips**: You can have one action handler to answer multiple request URL:

```java
@GetAction({"/profile/{id}", "/profile"})
public Profile getProfile(String id) {
    return dao.findById(id);
}
```

The above code allows the front end send request to `getProfile` using two different styles:

1. `/profile/<profile_id>`
2. `/profile?id=<profile_id>`

## The `routes` file

If you prefer the `PlayFramework` style routing, you are free to create a `routes` file under your `/src/main/resources` folder. The equivalent `routes` file replacing the above annotations should be look like (suppose the controller class is `com.mycom.myprj.MyController`):

```
GET /profile/{id} com.mycom.myprj.MyController.getProfile
POST /profile com.mycom.myprj.MyController.createProfile
PUT /profile/{id}/address com.mycom.myprj.MyController.updateAddress
DELETE /profile/{id}
```

In general any route entry defined in the `routes` file composed of three parts:

```
(GET|POST|DELETE|PUT|*) <url> <handler>
```

### Handler directives

By default the `handler` part of route entry indicate an action handler method defined in a controller class. However you can use handler directive to specify handler that are not action handler method. E.g.

```
# map /tmp url to /tmp dir
GET /tmp externalfile:/tmp
GET /public file:/public
GET /3215430325 echo:some-code
GET /google redirect:http://google.com
```

Act has four built-in directives:

1. `echo`: Any string after `echo:` will be sent to the response. This is especially useful when service, e.g. Godaddy needs you to respond with a specific code when request is sent to a certain endpoint
1. `file`: Allows you to send back static file under the application's base dir
1. `externalfile`: Allows you to send back any static file
1. `redirect`: Allows you to specify a redirection

**Notes**, the entry specified in `routes` file can overwrite the route specified with action annotations.

## Creating route with configuration API

TBD

[Back to index](index.md)