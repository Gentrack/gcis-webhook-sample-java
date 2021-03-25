# Webhook Example in Java

## Clone this repository and enter the source directory:
```
git clone git@github.com:Gentrack/gcis-webhook-sample-java.git
cd ~/gcis-webhook-sample-java
```

## Compile
```
javac src/*.java -d ./classes
```

## Run
```
java -cp ./classes MyHttpServer
```

The webhook will now process requests at the endpoint: `http://localhost:8500/events`

## Appendix: Test webhook locally using ngrok
* Download [ngrok](https://ngrok.com/) for your operating system.
* Start ngrok on http port 8500, taking note of the https forwarding URL from the console.
```
ngrok http 8500
```
* Log in to the developer portal and create a new application. On the *Event Subscription* page, subscribe to events to test and set the endpoint URL to the ngrok https forwarding URL: e.g., `https://7d55765d.ngrok.io/events`.
* Start webhook as per instructions above.
* Send test events using the simulator from the *Event Subscription* page on the developer portal. Check event payload from the webhook.
