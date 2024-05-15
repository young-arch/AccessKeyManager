package accesskey.access.Controller;

import accesskey.access.DTO.UserLoginRequest;
import accesskey.access.Entity.User;
import accesskey.access.Exceptions.UserNotFoundException;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController{
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());


    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //Create a new user(Accessible to everyone)
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        try {

            User newUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating user: " +e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    //Find a user by email(Accessible to admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<User> findUserByEmail(@PathVariable String email){
        try{
            User user = userService.findUserByEmail(email);
            return ResponseEntity.ok(user);
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error Finding user by email: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //Update a user's password(Accessible to user only if they match the email)
    @PreAuthorize("#email == authentication.principal.username")
    @PutMapping("/{email}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String email, @RequestBody String newPassword){
        try {
            userService.updatePassword(email, newPassword);
            return ResponseEntity.noContent().build();
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error updating password: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //Initiate Password Reset(Accessible to everyone)
    @PostMapping("/password/reset")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody String email){
        try {
        userService.initiatePasswordReset(email);
        return ResponseEntity.noContent().build();
        }catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error initiating password reset: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //Reset Password(Accessible to everyone)
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<Void> resetPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword){
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.noContent().build();
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error resetting password: " + e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //User Login(Accessible to everyone)
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody UserLoginRequest userLoginRequest){
        try{
            User user = userService.loginUser(userLoginRequest.getEmail(), userLoginRequest.getPassword());
            return ResponseEntity.ok(user);
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error logging in user: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //Initiate User Verification(Accessible to everyone)
    @PostMapping("/verify")
    public ResponseEntity<Void> initiateVerification(@RequestBody String email){
        try {
            userService.initiateVerification(email);
            return ResponseEntity.noContent().build();
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error initiating verification: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //Confirm User Verification
    @GetMapping("/verify/confirm")
    public ResponseEntity<Void> confirmVerification(@RequestParam("token") String token){
        try {
            userService.confirmVerification(token);
            return ResponseEntity.noContent().build();
        }catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, "Error confirming verification: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



}
