package accesskey.access.Controller;

import accesskey.access.Entity.User;
import accesskey.access.Exceptions.UserNotFoundException;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("auth/")
public class AuthenticationFront {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String createUser(User user, Model model) {
        try {
            userService.createUser(user);
            model.addAttribute("signupSuccess", "Verification link has been sent to your email. Please check your inbox.");
            return "signup";
        } catch (Exception e) {
            model.addAttribute("signupError", "An error occurred during signup. Please try again.");
            return "signup";
        }
    }

    @GetMapping("/check-email")
    public String checkEmail() {
        return "checkEmail";
    }

    @GetMapping("/verify/confirm")
    public ResponseEntity<Void> confirmVerification(@RequestParam("token") String token) {
        try {
            userService.confirmVerification(token);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
