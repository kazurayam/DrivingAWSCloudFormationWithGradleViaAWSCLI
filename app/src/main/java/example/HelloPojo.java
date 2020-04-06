package example;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * from https://docs.aws.amazon.com/ja_jp/lambda/latest/dg/with-android-create-package.html
 */
public class HelloPojo {

    // Define two classes/POJOs for use with Lambda function.
    public static class RequestClass {
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

        public RequestClass(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public RequestClass() {
        }
    }

    public static class ResponseClass {
        String greetings;

        public String getGreetings() {
            return greetings;
        }

        public void setGreetings(String greetings) {
            this.greetings = greetings;
        }

        public ResponseClass(String greetings) {
            this.greetings = greetings;
        }

        public ResponseClass() {
        }

    }

    public static ResponseClass myHandler(RequestClass request, Context context){
        String greetingString = greeting(request);
        context.getLogger().log(greetingString);
        return new ResponseClass(greetingString);
    }

    public static String greeting(RequestClass request) {
        return String.format("Hello %s, %s.", request.firstName, request.lastName);
    }

    public static void main(String[] args) {
        RequestClass request = new RequestClass();
        request.setFirstName("Albert");
        request.setLastName("Camus");
        String greetingString = HelloPojo.greeting(request);
        System.out.println(greetingString);
    }
}