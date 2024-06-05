package accesskey.access.Controller;

import accesskey.access.Entity.KeyDetails;
import accesskey.access.Service.AccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminWeb {

    @Autowired
    private AccessKeyService accessKeyService;

    @GetMapping()
    public String getAllKeys(Model model, @RequestParam(value = "email", required = false) String email) {
        List<KeyDetails> allKeys = this.accessKeyService.getAllAccessKeys();
        model.addAttribute("accessKeys", allKeys);

        if (email != null && !email.isEmpty()) {
            try {
                KeyDetails activeAccessKey = this.accessKeyService.getActiveAccessKeyByEmail(email);
                model.addAttribute("activeAccessKey", activeAccessKey);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active access key not found for email: " + email);
            }
        }

        return "adminPanel";
    }

    @GetMapping("/revoke")
    public String revokeKey(@RequestParam("email") String email) {
        this.accessKeyService.revokeAccessKey(email);
        return "redirect:/admin";
    }
}
