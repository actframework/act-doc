# WebSocket support

Websocket is a technology that allows two way communication between browser and web server. ActFramework provides websocket support since R1.4.0. 

## Introduction - A simple chat server

A simple [chat app](https://github.com/actframework/act-demo-apps/edit/master/chatroom) with WebSocket support:

```java
public class ChatApp {

    @GetAction
    public void home() {
    }

    @WsAction("msg")
    public void onMessage(String message, WebSocketContext context) {
        // suppress blank lines
        if (S.notBlank(message)) {
            context.sendToPeers(message);
        }
    }

    public static void main(String[] args) throws Exception {
        Act.start("chat room");
    }
}
```

Here is the code for html page to handle websocket communication:

```html
<script>
  var socket;
  if (window.WebSocket) {
      socket = new WebSocket("ws://localhost:5460/msg");
      socket.onmessage = function (event) {
          var home = document.getElementById('chat');
          home.innerHTML = home.innerHTML + event.data + "<br />";
      };
  } else {
      alert("Your browser does not support Websockets. (Use Chrome)");
  }
  function send(message) {
      if (!window.WebSocket) {
          return false;
      }
      if (socket.readyState == WebSocket.OPEN) {
          socket.send(message);
      } else {
          alert("The socket is not open.");
      }
      return false;
  }
</script>
```

## Introduction 2 - an echo server

An echo server always echo back the message sent through a connection, we can use `WebSocketContext.sendToSelf(String)` API:

```java
@WsAction("echo")
public void onMessage(String message, WebSocketContext context) {
    context.sendToSelf(message);
}
```

## Handling websocket message

Like `@GetAction`, `@PostAction` etc, `@WsAction` is used to mark a websocket message handler method:

```java
@WsAction("/ws/msg")
public void handleMessage(String messageText, WebSocketContext context) {
    context.sendToPeers(messageText);
}
```

In the above code, it defines a websocket message handler method `handleMessage`, there are two parameters in the method:

* `String messsageText` - any text string sent to the websocket connection from browser
* `WebSocketContext context` - the framework injected websocket context

The method use `WebSocketContext::sendToPeers(String)` API to send the received message to all websocket connections connected to the same URL: `/ws/msg`. Obviously this is a typical chatroom app.

## Autobind to complex type

It is possible for browser to send a complex data structure to the server, e.g.:

```javascript
socket.send(JSON.stringify({room: '@room', text: msg, from: me.id, nickname: me.nickname}));
```

And the server side we have a class `Message` defined as:

```java
@Data
public class Message implements SimpleBean {

    public String text;

    public String room;

    public String from;

    public String nickname;

}
```

Then we can declare our message handler as:

```java
@WsAction("/chat")
public void handlePojoMessage(Message pojo, WebSocketContext context) {
    context.sendJsonToTagged(pojo, pojo.room);
}
```

In the above case, we implemented a multiple rooms chat service by using `WebSocketContext::sendJsonToTagged(Object msg, String tag)` API, which send the message to all connections tagged by `message.room` as per above code. Now the question is how to tag a connection, and we will show how to do that in the next section:

## Handle connect/disconnect event

When browser issue `new websocket('ws://myhost/myurl')` it will initialize HTTP GET request to `/myurl` and undertow will be able to upgrade the protocol and setup websocket connection on that URL. ActFramework allows application to respond to the connect through event listener mechanism:

```java
private static final AtomicInteger CONN_COUNTER = new AtomicInteger(0);

@OnEvent
public static void handleConnection(WebSocketConnectEvent event) {
    CONN_COUNTER.incrementAndGet();
    final WebSocketContext context = event.source();
    context.tag(Room.MAIN);
}
```

The above code respond to `WebSocketConnectEvent` by increment the connection counter and tagging the connection with `Room.MAIN` constant through `WebSocketContext.tag(String label)` API, meaning any new incoming connection will be added into the main room, thus any message sent to the main room will be dispatched to new connections. A message sent to the main room could be something like:

```json
{
    "text": "Hi",
    "room": "main",
    "from": "tom@abc.com",
    "nickname: "Tommy"
}
```

Developer can also code the logic handling the websocket connection close event:

```java
public static void handleConnectionClose(WebSocketCloseEvent event) {
    final WebSocketContext context = event.source();
    CONN_COUNTER.decrementAndGet();
}
```

**Note** `WebSocketConnectEvent` and `WebSocketCloseEvent` are raised whenever a connection is established or closed, without regarding to the URL of the connection. Thus if an application has multiple websocket endpoints, and the logic is relevant to specific URL, then it must check the URL of the connection:

```java
public static void handleConnectionClose(WebSocketCloseEvent event) {
    final WebSocketContext context = event.source();
    if ("/ws/endpoint1".equals(context.url())) {
        System.out.println("endpoint1 closed");
    }
}
```

## Send message to specific user

If the application implemented authentication by storing the username into `H.Session` with key defined in `AppConfig.sessionKeyUsername()`, ActFramework will add a connection from a logged in user into username registry. And it is super easy for the application to code the logic send a messge to specific user. For example, suppose the app needs to implement a news updates through websocket, it can code the logic as:

```java
@OnEvent
public static void handleNewsUpdate(NewsUpdateEvent event, User.Dao userDao, WebSocketConnectionManager connectionManager) {
    NewsUpdate update = event.source();
    List<User> users = userDao.findBySubscription(update.topic());
    for (User user: users) {
        connectionManager.sendJsonToUser(update, user.username());
    }
}
```

