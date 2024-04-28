package accesskey.access.Controller;

import accesskey.access.Entity.User;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController{

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //Create a new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser =  userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    //Find a user by email
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

}
