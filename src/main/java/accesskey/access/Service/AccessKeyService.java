package accesskey.access.Service;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Repository.AccessKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessKeyService{

    private final AccessKeyRepository accessKeyRepository;

    @Autowired
    public AccessKeyService(AccessKeyRepository accessKeyRepository){
        this.accessKeyRepository = accessKeyRepository;
    }

    //Create a new Access key
    public AccessKey createAccessKey(AccessKey accessKey){
        return accessKeyRepository.save(accessKey);
    }

    //Find access keys by user ID
    public List<AccessKey> findAccessKeysByUserId(Integer userId){
        return accessKeyRepository.findByUserId(userId);
    }

    //Find the active access key for a specific user
    public AccessKey findActiveAccessKey(Integer userId){
        return accessKeyRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }

    //Update the status of an access key
    public void updateAccessKeyStatus(Integer keyId, String newStatus){
        accessKeyRepository.updateStatusById(keyId, newStatus);
    }

    //Find and revoke an access key
    public void revokeAccessKey(Integer keyId){
        accessKeyRepository.updateStatusById(keyId, "REVOKED");
    }

}
