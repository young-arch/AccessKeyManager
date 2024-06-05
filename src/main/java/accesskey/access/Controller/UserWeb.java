package accesskey.access.Controller;

import accesskey.access.Entity.KeyDetails;
import accesskey.access.Service.AccessKeyService;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserWeb {

    @Autowired
    private AccessKeyService accessKeyService;

    @Autowired
    private UserService userService;

    @GetMapping()
    public String userKeys(Authentication authentication, Model model){
        String email = authentication.getName();
        model.addAttribute("favicon", email);
        List<KeyDetails> myKeys = accessKeyService.getAllAccessKeysByEmail(email);
        model.addAttribute("accessKeys", myKeys); // Note the attribute name change
        return "userPanel";
    }

    @PostMapping("/create")
    public String createKey(@RequestParam String customKeyName, Authentication authentication){
        accessKeyService.createAccessKeyWithCustomName(customKeyName, userService.findUserByEmail(authentication.getName()).getId());
        return "redirect:/user";
    }
}
