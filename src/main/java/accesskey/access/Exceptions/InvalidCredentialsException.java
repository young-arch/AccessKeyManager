package accesskey.access.Exceptions;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String invalidPassword) {
        super(invalidPassword);
    }
}
