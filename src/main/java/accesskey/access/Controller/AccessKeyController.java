package accesskey.access.Controller;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Service.AccessKeyService;
import accesskey.access.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accesskeys")
public class AccessKeyController {

    private final AccessKeyService accessKeyService;


    @Autowired
    public AccessKeyController(AccessKeyService accessKeyService){
        this.accessKeyService = accessKeyService;

    }

    //Create a new access key(Admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<AccessKey> createAccessKey(@RequestBody AccessKey accessKey){
        AccessKey newAccesskey = accessKeyService.createAccessKey(accessKey);
        return ResponseEntity.ok(newAccesskey);
    }

    //Find active access keys for a user(School_IT and admin)
    @PreAuthorize("hasAnyRole('ROLE_SCHOOL_IT', 'ROLE_ADMIN')")
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<AccessKey> findActiveAccessKey(@PathVariable Integer userId){

        AccessKey activeAccessKey = accessKeyService.findActiveAccessKey(userId);

        if (activeAccessKey != null){

            return ResponseEntity.ok(activeAccessKey);
        }

        else {

            return ResponseEntity.notFound().build();

        }

    }

    //Find all expired access keys (Admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/expired")
    public ResponseEntity<List<AccessKey>> findExpiredAccessKeys(){

        List<AccessKey> expiredKeys = accessKeyService.findExpiredAccessKeys();

        return ResponseEntity.ok(expiredKeys);
    }

    //Revoke an access key (Admin only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{keyId}/revoke")
    public ResponseEntity<Void> revokeAccessKey(@PathVariable Integer keyId){

        accessKeyService.revokeAccessKey(keyId);

        return ResponseEntity.noContent().build();

    }

    //Find access keys for a specific user (School IT and Admin)
    @PreAuthorize("hasAnyRole('ROLE_SCHOOL_IT', 'ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccessKey>> findAccessKeysByUserId(@PathVariable Integer userId){
        List<AccessKey> accessKeys = accessKeyService.findAccessKeysByUserId(userId);
        return ResponseEntity.ok(accessKeys);
    }




}
