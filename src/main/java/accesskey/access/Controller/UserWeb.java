package accesskey.access.Controller;

import accesskey.access.Entity.KeyDetails;
import accesskey.access.Service.AccessKeyService;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserWeb {

    @Autowired
    private AccessKeyService accessKeyService;

    @Autowired
    private UserService userService;

    @GetMapping()
    public String userKeys(Authentication authentication, Model model) {
        String email = authentication.getName();
        model.addAttribute("email", email);
        List<KeyDetails> myKeys = accessKeyService.getAllAccessKeysByEmail(email);
        model.addAttribute("accessKeys", myKeys);
        return "userPanel";
    }

    @PostMapping("/create")
    public String createKey(@RequestParam String customKeyName, Authentication authentication) {
        accessKeyService.createAccessKeyWithCustomName(customKeyName, userService.findUserByEmail(authentication.getName()).getId());
        return "redirect:/user";
    }
}
