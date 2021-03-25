import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;

public class MyHttpServer {
    public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzXMzPHjqwmHsAJ8thkP9\n" +
            "abNbyFuUqbmJNwKmG5j9wVcC4D1hMFY6MzTNTZWoI3VviYbKJXhcqR35WlEfmXCs\n" +
            "WItIsG+8N8+uxY1qyUJxvqi2VkJnQc+60OwZ7CaSVHLdOfoYvNmnSJeCWb+Ukhda\n" +
            "T5AaR+oDNjvjT5VMqe1cGiafJMOZV363QrYY0LLUis37YapWynbx6g0MHMX14riF\n" +
            "htodqHxis7Kl7NCH8DkZr+mDxlpz5DU6MeDG/LH8GeqtgDAetSe+P9azBj1tClI3\n" +
            "0EJCd/3Z2KwzslW9mF1Jy8SioMxuTUdjPZCUQrzDcVRPyDX/09ODRAe0l6u37HRG\n" +
            "JwIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/events");
        context.setHandler(MyHttpServer::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        boolean verified = verifyRequestInfo(exchange);
        String response = "";
        exchange.sendResponseHeaders(verified ? 200 : 400, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static boolean verifyRequestInfo(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String requestMethod = exchange.getRequestMethod();
        System.out.println(requestMethod + " " + uri);

        System.out.println("-- headers --");
        Headers requestHeaders = exchange.getRequestHeaders();
        requestHeaders.entrySet().forEach(System.out::println);
        String signature = requestHeaders.get("X-Payload-Signature").get(0);
        String t = signature.split(",")[0].split("=")[1];
        String v = signature.split(",")[1].split("=")[1];

        System.out.println("-- body --");
        InputStream requestBody = exchange.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(requestBody));
        String payload = br.readLine();
        System.out.println(payload);

        VerifySignature verifySignature = new VerifySignature();
        return verifySignature.verify(v, t + "." + payload, PUBLIC_KEY);
    }
}
