package accesskey.access.Service;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.User;
import accesskey.access.Exceptions.AccessKeyNotFoundException;
import accesskey.access.Repository.AccessKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        //Checks if the user has Admin role
        if(!isUserAdmin()){
            throw new SecurityException("Unauthorized: User must have an admin role to create access keys");
        }
        return accessKeyRepository.save(accessKey);
    }

    //Find access keys by user ID
    public List<AccessKey> findAccessKeysByUserId(Integer userId){
        //Check if current user is authorized to view access keys for this user ID
        if(!isCurrentUserOrAdmin(userId)){
            throw new SecurityException("Unauthorized: You can only view access keys for your own account or if you are an admin");
        }
        return accessKeyRepository.findByUserId(userId);
    }

    //Find the active access key for a specific user
    public AccessKey findActiveAccessKey(Integer userId){
        //Check if the current user is authorized to view access keys for this user ID
        if(!isCurrentUserOrAdmin(userId)){
            throw new SecurityException("Unauthorized: You can only view access keys which are ACTIVE for your own account or if you are an admin");
        }

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

    //Methods for authorization checks
    private boolean isUserAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    UserService userService;
    private boolean isCurrentUserOrAdmin(Integer userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            //Get the current user's email
            String currentUserEmail = authentication.getName();

            //Check if the current user has admin role or owner of the account
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            User currentUser = userService.findUserByEmail(currentUserEmail);
            boolean isCurrentUser = currentUser.getId().equals(userId);
            return isAdmin || isCurrentUser;
        }
        return false;
    }



}
