# WebSocket support

Websocket is a technology that allows two way communication between browser and web server. ActFramework provides websocket support since R1.4.0. 

## Introduction

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
public void handleMessage(Message message, WebSocketContext context) {
    String roomName = message.room;
    context.sendJsonToTagged(message, message.room);
}
```

In the above case, we implemented a multiple rooms chat service by using `WebSocketContext::sendJsonToTagged(Object msg, String tag)` API, which send the message to all connections tagged by `message.room` as per above code. Now the question is how to tag a connection, and we will show how to do that in the next section:

## Handle connect event

When browser issue `new websocket('ws://myhost/myurl')` it will initialize HTTP GET request to `/myurl` and undertow will be able to upgrade the protocol and setup websocket connection on that URL. ActFramework allows application to respond to the connect through event listener mechanism:

```java
@OnEvent
public static void handleConnection(WebSocketConnectEvent event) {
    final WebSocketContext context = event.source();
    context.tag(Room.MAIN);
    String username = context.username();
    if (S.notBlank(username)) {
        userOf(context).runWith(user -> {user.rooms.forEach(context::tag); return null;});
    }
}
```

The above code respond to `WebSocketConnectEvent` and tag the connection with `Room.MAIN` constant, meaning any new incoming connection will be added into the main room.
