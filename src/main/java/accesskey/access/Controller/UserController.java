package accesskey.access.Controller;

import accesskey.access.Entity.User;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController{

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //Create a new user(Accessible to everyone)
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser =  userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    //Find a user by email(Accessible to admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<User> findUserByEmail(@PathVariable String email){
        User user = userService.findUserByEmail(email);
        if(user != null){
            return ResponseEntity.ok(user);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    //Update a user's password(Accessible to user only if they match the email)
    @PreAuthorize("#email == authentication.principal.username")
    @PutMapping("/{email}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable String email, @RequestBody String newPassword){
        userService.updatePassword(email, newPassword);
        return ResponseEntity.noContent().build();
    }

    //Initiate Password Reset
    @PostMapping("/password/reset")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody String email){
        userService.initiatePasswordReset(email);
        return ResponseEntity.noContent().build();
    }

    //Password Reset
    public ResponseEntity<Void> resetPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword){
        userService.resetPassword(token, newPassword);
        return ResponseEntity.noContent().build();
    }




}
