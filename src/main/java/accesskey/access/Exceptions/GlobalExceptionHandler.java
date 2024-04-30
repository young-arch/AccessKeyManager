package accesskey.access.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    //User Not Found Exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

    }

    //Access Key Not Found Exception
    @ExceptionHandler(AccessKeyNotFoundException.class)
    public ResponseEntity<String> handleAccessKeyNotFoundException(AccessKeyNotFoundException ex, WebRequest request){

        return  new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

    }

    //Illegal Argument Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, WebRequest request){

        return new ResponseEntity<>("An error occured: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
