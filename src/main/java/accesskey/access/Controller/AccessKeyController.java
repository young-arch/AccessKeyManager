package accesskey.access.Controller;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.KeyDetails;
import accesskey.access.Exceptions.AccessKeyNotFoundException;
import accesskey.access.Service.AccessKeyService;
import accesskey.access.Service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accesskeys")
@RequiredArgsConstructor
public class AccessKeyController {

    private final AccessKeyService accessKeyService;

    @Getter
    private final UserService userService;



    //Find all expired access keys (Admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/expired")
    public ResponseEntity<List<AccessKey>> findExpiredAccessKeys(){

        List<AccessKey> expiredKeys = accessKeyService.findExpiredAccessKeys();

        return ResponseEntity.ok(expiredKeys);
    }

    //Revoke an access key (Admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/revoke")
    public ResponseEntity<List<KeyDetails>> revokeAccessKey(@RequestParam("email") String email){

        accessKeyService.revokeAccessKey(email);

        return ResponseEntity.ok(this.accessKeyService.getAllAccessKeys());

    }

    //Find all access keys with details by ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<KeyDetails> findAllAccessKeysWithDetails(){

        return accessKeyService.getAllAccessKeys();
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/active/email")
    public ResponseEntity<KeyDetails> findActiveAccessKeyByEmail(@RequestParam String email){
        try {
            KeyDetails activeAccessKey = accessKeyService.getActiveAccessKeyByEmail(email);
            return ResponseEntity.ok(activeAccessKey);
        }catch (AccessKeyNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }




}
