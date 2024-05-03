package accesskey.access.Service;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Exceptions.AccessKeyNotFoundException;
import accesskey.access.Repository.AccessKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        AccessKey activeAccessKey = accessKeyRepository.findByUserIdAndStatus(userId, "ACTIVE");

        if(activeAccessKey == null){
            throw new AccessKeyNotFoundException("Active access key not found for user ID: " + userId);
        }
        return activeAccessKey;
    }

    //Update the status of an access key
    public void updateAccessKeyStatus(Integer keyId, String newStatus){
        accessKeyRepository.updateStatusById(keyId, newStatus);
    }

    //Find and revoke an access key
    public void revokeAccessKey(Integer keyId){
        accessKeyRepository.updateStatusById(keyId, "REVOKED");
    }

    //Find all expired access keys
    public List<AccessKey> findExpiredAccessKeys(){
        return accessKeyRepository.findAllByExpiryDateBefore(LocalDateTime.now());
    }

    //Find access key by key string
    public AccessKey findAccessKeyByKey(String key){
        return accessKeyRepository.findByKey(key);
    }


}