public class InputException extends Throwable{
    String message;
    InputException(){}
    InputException(String message){
        this.message = message;
    }
}
