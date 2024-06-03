package accesskey.access.Controller;

import accesskey.access.DTO.PasswordResetConfirmRequest;
import accesskey.access.DTO.PasswordResetRequest;
import accesskey.access.DTO.UserLoginRequest;
import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.KeyDetails;
import accesskey.access.Entity.User;
import accesskey.access.Exceptions.UserNotFoundException;
import accesskey.access.Service.AccessKeyService;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController{
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    private final AccessKeyService accessKeyService;


    private final UserService userService;

    @Autowired
    public UserController(AccessKeyService accessKeyService, UserService userService){
        this.accessKeyService = accessKeyService;

        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_SCHOOL_IT')")
    @GetMapping("/createAccessKey")
    public ResponseEntity<?> createAccessKey(@RequestParam String customKeyName, Authentication authentication){
        Integer userId = this.userService.findUserByEmail(authentication.getName()).getId();
        try {
            AccessKey newAccessKey = accessKeyService.createAccessKeyWithCustomName(customKeyName, userId);
            return ResponseEntity.ok(newAccessKey);
        }catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }


    }



    @PreAuthorize("hasRole('ROLE_SCHOOL_IT')")
    @GetMapping("/myAccessKeys")
    public ResponseEntity<List<KeyDetails>> getAccessKeys(){
        Integer userId = accessKeyService.getCurrentUserId();
        User currentUser = userService.findUserById(userId);
        String email = currentUser.getEmail();
        List<KeyDetails> accessKeys = accessKeyService.getAllAccessKeysByEmail(email);

        return ResponseEntity.ok(accessKeys);
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
    @PostMapping("/password/resets")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody PasswordResetRequest request){
        try {
            String email = request.getEmail();
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
    @PostMapping("/password/resets/confirms")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody PasswordResetConfirmRequest request){
        try {
            userService.resetPasswordWithToken(token,request.getNewPassword(), request.getConfirmPassword());
            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }
        catch (UserNotFoundException e){
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

    //Delete a user by email (Accessible to admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email){

        try{
            userService.deleteUserByEmail(email);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Error deleting user by email: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
