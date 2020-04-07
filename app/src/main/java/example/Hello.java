package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.Context;

import example.Hello.Request;
import example.Hello.Response;

/**
 * https://docs.aws.amazon.com/ja_jp/lambda/latest/dg/java-handler-interfaces.html
 */
public class Hello implements RequestHandler<Request, Response> {

    // Define two classes/POJOs for use with Lambda function.
    public static class Request {
        String firstName;
        String lastName;
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        public Request(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        public Request() {}
    }

    public static class Response {
        String greetings;
        public String getGreetings() {
            return greetings;
        }
        public void setGreetings(String greetings) {
            this.greetings = greetings;
        }
        public Response(String greetings) {
            this.greetings = greetings;
        }
        public Response() { }
    }

    public Response handleRequest(Request request, Context context){
        String greetingString = greeting(request);
        context.getLogger().log(greetingString);
        return new Response(greetingString);
    }

    public static String greeting(Request request) {
        return String.format("Hello, %s %s.", request.firstName, request.lastName);
    }

    public static void main(String[] args) {
        Request request = new Request();
        request.setFirstName("Albert");
        request.setLastName("Camus");
        String greetingString = Hello.greeting(request);
        System.out.println(greetingString);
    }
}