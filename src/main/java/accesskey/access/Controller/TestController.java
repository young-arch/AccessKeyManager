package accesskey.access.Controller;

import accesskey.access.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final EmailService emailService;

    @Autowired
    public TestController(EmailService emailService){
        this.emailService = emailService;
    }

    @GetMapping("/send-email")
    public String sendTestEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text){
        emailService.sendEmail(to, subject, text);
        return "Email sent succcessfully";
    }
}
