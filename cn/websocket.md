# WebSocket 支持

Websocket 是一种在 HTTP 协议之上的一种技术，可以让浏览器与后台 Web 服务器之间进行双向通讯。ActFramework 自 R1.4.0 开始提供对 WebSocket 的支持 

## 介绍 I - 一个简单的聊天服务

这个简单的[聊天服务应用](https://github.com/actframework/act-demo-apps/edit/master/chatroom) 展示了如何使用 ActFramework 实现一个群聊服务:

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

前端代码大致是：

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

## 介绍 II - 一个 Echo 服务

使用 ActFramework 提供的 `WebSocketContext.sendToSelf(String)` API 来实现一个 Echo 服务：

```java
@WsAction("echo")
public void onMessage(String message, WebSocketContext context) {
    context.sendToSelf(message);
}
```

## 处理上传消息

就像普通的 HTTP 消息响应器使用 `@GetAction`, `@PostAction` 等, 我们使用 `@WsAction` 注解来标识一个 WebSocket 的消息服务端点：

```java
@WsAction("/ws/msg")
public void handleMessage(String messageText, WebSocketContext context) {
    context.sendToPeers(messageText);
}
```

上面的代码定义了一个 WebSocket 消息处理器方法 `handleMessage`, 其中有两个参数：

* `String messsageText` - 任何通过 websocket 连接发送的文字消息
* `WebSocketContext context` - 框架注入的 `WebSocketContext` 对象

该方法使用 `WebSocketContext::sendToPeers(String)` API 向所有连接到 `/ws/msg` 的 websocket 发送消息。很明显这是一个聊天室应用。

## 绑定复杂类型

浏览器可以向服务器发送 JSON 编码的复杂类型，比如：

```javascript
socket.send(JSON.stringify({room: '@room', text: msg, from: me.id, nickname: me.nickname}));
```

在服务器端可以定义一个 `Message` 类:

```java
@Data
public class Message implements SimpleBean {

    public String text;

    public String room;

    public String from;

    public String nickname;

}
```

然后我们可以直接在消息响应器中使用 `Message` 类作为参数:

```java
@WsAction("/chat")
public void handlePojoMessage(Message pojo, WebSocketContext context) {
    context.sendJsonToTagged(pojo, pojo.room);
}
```

上例中我们使用 `WebSocketContext::sendJsonToTagged(Object msg, String tag)` 实现了一个简单的多聊天室应用. 收到的消息发送给所有加了 `message.room` 标签的 websocket 连接。下一节我们会介绍如何给一个 websocket 连接打上标签：

## 处理连接建立和断开事件

当浏览器中执行 `new websocket('ws://myhost/myurl')` 语句的时候，将发出一个 HTTP GET 请求到 `/myurl`，undertow 会升级 HTTP 协议并建立一个 websocket 连接. ActFramework 通过事件分发机制支持应用设置连接建立时的逻辑:

```java
private static final AtomicInteger CONN_COUNTER = new AtomicInteger(0);

@OnEvent
public static void handleConnection(WebSocketConnectEvent event) {
    CONN_COUNTER.incrementAndGet();
    final WebSocketContext context = event.source();
    context.tag(Room.MAIN);
}
```

上面的事件响应代码会在任何一个 websocket 连接建立时触发。应用会增加连接计数器，然后通过 `WebSocketContext.tag(String label)` API 将新连接打上 `main room` 的标签。这样所有发送到 `main room` 的消息会被分发到新的连接。按照我们上面的代码，客户端发送到 `main room` 的消息可以是：

```json
{
    "text": "Hi",
    "room": "main",
    "from": "tom@abc.com",
    "nickname: "Tommy"
}
```

开发人员也可以针对连接断开事件编码：

```java
public static void handleConnectionClose(WebSocketCloseEvent event) {
    final WebSocketContext context = event.source();
    CONN_COUNTER.decrementAndGet();
}
```

**注意** 任何 websocket 连接在建立或者断开的时候都会触发相应的 `WebSocketConnectEvent` 和 `WebSocketCloseEvent` 事件，而和具体的 websocket 服务 URL 无关。假如应用有多个 websocket 服务端点, 需要处理特定 URL 连接建立断开事件, 应用必须检查连接的 URL：

```java
public static void handleConnectionClose(WebSocketCloseEvent event) {
    final WebSocketContext context = event.source();
    if ("/ws/endpoint1".equals(context.url())) {
        System.out.println("endpoint1 closed");
    }
}
```

## 发送消息到特定用户

如果应用实现了用户认证，且认证用户的用户名按照 `AppConfig.sessionKeyUsername()` 定义的 `key` 保存在 `H.Session` 中，ActFramework 提供了非常方便的 API 来将消息发送给特定用户:

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

