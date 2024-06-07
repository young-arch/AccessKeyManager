package accesskey.access.Controller;

import accesskey.access.Entity.User;
import accesskey.access.Exceptions.UserNotFoundException;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthenticationFront {

    @GetMapping("/home")
    public String home() {
        return "index";
    }

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

    @GetMapping("/forgotpassword")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<Void> initiatePasswordReset(@RequestParam String email) {
        try {
            userService.initiatePasswordReset(email);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/password/resets/confirms")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        try {
            userService.resetPasswordWithToken(token, newPassword, confirmPassword);
            model.addAttribute("message", "Your password has been reset successfully. Please login with your new password.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        } catch (UserNotFoundException e) {
            model.addAttribute("error", "Invalid token.");
            return "reset-password";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred. Please try again.");
            return "reset-password";
        }
    }
}
