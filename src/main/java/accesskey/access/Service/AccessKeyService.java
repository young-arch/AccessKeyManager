package accesskey.access.Service;
import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.User;
import accesskey.access.Exceptions.AccessKeyNotFoundException;
import accesskey.access.Exceptions.InvalidRequestException;
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
        Integer userId = accessKey.getId();
        if(isUserAdmin()){
            throw new InvalidRequestException("Unauthorized: User must have an admin role to create access keys");
        }
        if (existsActiveKeyForUser(userId)) {
            throw new IllegalStateException("User already has an active access key");
        }
        return accessKeyRepository.save(accessKey);
    }

    //Find access keys by user ID
    public List<AccessKey> findAccessKeysByUserId(Integer userId){
        //Check if current user is authorized to view access keys for this user ID
        if(isCurrentUserOrAdmin(userId)){
            throw new SecurityException("Unauthorized: You can only view access keys for your own account or if you are an admin");
        }
        return accessKeyRepository.findByUserId(userId);
    }

    //Find the active access key for a specific user
    public AccessKey findActiveAccessKey(Integer userId){
        //Check if the current user is authorized to view access keys for this user ID
        if(isCurrentUserOrAdmin(userId)){
            throw new SecurityException("Unauthorized: You can only view access keys which are ACTIVE for your own account or if you are an admin");
        }

        AccessKey activeAccessKey = accessKeyRepository.findByUserIdAndStatus(userId, AccessKey.AccessKeyStatus.valueOf("ACTIVE"));

        if(activeAccessKey == null){
            throw new AccessKeyNotFoundException("Active access key not found for user ID: " + userId);
        }
        return activeAccessKey;
    }

    //Update the status of an access key
    public void updateAccessKeyStatus(Integer keyId, String newStatus){
        //Check if the use has admin role
        if(isUserAdmin()){
            throw new SecurityException("Unauthorized: User must have admin role to update access key status");
        }
        AccessKey accessKey = accessKeyRepository.findById(keyId)
                .orElseThrow(() -> new AccessKeyNotFoundException("Access key not found with id: " + keyId));
        accessKey.setStatus(AccessKey.AccessKeyStatus.valueOf(newStatus));
        accessKeyRepository.save(accessKey);
    }

    //Find and revoke an access key
    public void revokeAccessKey(Integer keyId){
        //Check if the user has admin role
        if(isUserAdmin()){
            throw new SecurityException("Unauthorized: User must have admin role to revoke access keys");
        }
        accessKeyRepository.updateStatusById(keyId, AccessKey.AccessKeyStatus.valueOf("REVOKED"));
    }

    //Find all expired access keys
    public List<AccessKey> findExpiredAccessKeys(){
        //Check if the user has admin role
        if(isUserAdmin()){
            throw new SecurityException("Unauthorized: User must have admin role to view expired access keys");
        }
        return accessKeyRepository.findAllByExpiryDateBefore(LocalDateTime.now());
    }

    public AccessKey findActiveAccessKeyByEmail(String email){
        User user = userService.findUserByEmail(email);
        if (user == null){
            throw new AccessKeyNotFoundException("User not found with email: " + email);
        }
        return findActiveAccessKey(user.getId());
    }

    //Find all access keys
    public List<AccessKey> findAllAccessKeysWithDetails(){
        return accessKeyRepository.findAll();
    }

    //Check if there is an active key for a specific user
    public boolean existsActiveKeyForUser(Integer userId){
        return accessKeyRepository.existsByUserIdAndStatus(userId, AccessKey.AccessKeyStatus.ACTIVE);
    }

    //Methods for authorization checks
    private boolean isUserAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || authentication.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
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
            return !isAdmin && !isCurrentUser;
        }
        return true;
    }



}
