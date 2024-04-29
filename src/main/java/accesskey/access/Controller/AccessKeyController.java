package accesskey.access.Controller;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Service.AccessKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accesskeys")
public class AccessKeyController {

    private final AccessKeyService accessKeyService;

    @Autowired
    public AccessKeyController(AccessKeyService accessKeyService){
        this.accessKeyService = accessKeyService;

    }

    //Create a new access key
    @PostMapping
    public ResponseEntity<AccessKey> createAccessKey(@RequestBody AccessKey accessKey){
        AccessKey newAccesskey = accessKeyService.createAccessKey(accessKey);
        return ResponseEntity.ok(newAccesskey);
    }

    //Find active access keys for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<AccessKey> findActiveAccesskey(@PathVariable Integer userId){
        AccessKey activeAccessKey = accessKeyService.findActiveAccessKey(userId);
        if (activeAccessKey != null){
            return ResponseEntity.ok(activeAccessKey);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
