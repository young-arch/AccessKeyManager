package accesskey.access.Controller;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Exceptions.AccessKeyNotFoundException;
import accesskey.access.Service.AccessKeyService;
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

    //Update the status of an access key
    @PutMapping("/{keyId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateAccessKeyStatus(@PathVariable Integer keyId, @RequestBody String newStatus){
        accessKeyService.updateAccessKeyStatus(keyId, newStatus);
        return ResponseEntity.noContent().build();
    }

    //Find all access keys with details
    @PreAuthorize("hasAnyRole('ROLE_SCHOOL_IT', 'ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<AccessKey>> findAllAccessKeysWithDetails(){
        List<AccessKey> accessKeys = accessKeyService.findAllAccessKeysWithDetails();
        return ResponseEntity.ok(accessKeys);
    }

    //Check if there is an active key for a specific user
    @PreAuthorize("hasAnyRole('ROLE_SCHOOL', 'ROLE_ADMIN')")
    @GetMapping("/user/{userId}/active/check")
    public ResponseEntity<Boolean> existsActiveKeyForUser(@PathVariable Integer userId){
        boolean existsActiveKey = accessKeyService.existsActiveKeyForUser(userId);
        return ResponseEntity.ok(existsActiveKey);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/active/email")
    public ResponseEntity<AccessKey> findActiveAccessKeyByEmail(@RequestParam String email){
        try {
            AccessKey activeAccessKey = accessKeyService.findActiveAccessKeyByEmail(email);
            return ResponseEntity.ok(activeAccessKey);
        }catch (AccessKeyNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }






}
