package accesskey.access.Service;
import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.KeyDetails;
import accesskey.access.Entity.User;
import accesskey.access.Exceptions.AccessKeyNotFoundException;
import accesskey.access.Repository.AccessKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccessKeyService implements AccessKeyServiceInterface{
    private final UserService userService;
    private final AccessKeyRepository accessKeyRepository;

//    public AccessKeyService(AccessKeyRepository accessKeyRepository){
//        this.accessKeyRepository = accessKeyRepository;
//    }

    //Create a new Access key
    public AccessKey createAccessKeyWithCustomName(String customName, Integer userId){
        //Check if current user has an active key


        //Generate a custom key name
        String key = generateRandomString(10) + "-" + customName;

        AccessKey accessKey = new AccessKey();
        accessKey.setKey(key);
        accessKey.setStatus(AccessKey.AccessKeyStatus.ACTIVE);
        accessKey.setProcurementDate(LocalDateTime.now());
        accessKey.setExpiryDate(LocalDateTime.now().plusDays(30));
        accessKey.setUser(this.userService.findUserById(userId));
        accessKey.setAccessKeyName(customName);

        return accessKeyRepository.save(accessKey);
    }

    public Integer getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new SecurityException("No authenticated user found");
        }
        String currentUserEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(currentUserEmail);
        return currentUser.getId();
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
    public void revokeAccessKey(String email){
        KeyDetails keyDetails = this.getActiveAccessKeyByEmail(email);

        AccessKey key = accessKeyRepository.findByKey(keyDetails.getKey());
        key.setStatus(AccessKey.AccessKeyStatus.REVOKED);

        accessKeyRepository.save(key);
    }

    //Find all expired access keys
    public List<AccessKey> findExpiredAccessKeys(){
        //Check if the user has admin role
        if(isUserAdmin()){
            throw new SecurityException("Unauthorized: User must have admin role to view expired access keys");
        }
        return accessKeyRepository.findAllByExpiryDateBefore(LocalDateTime.now());
    }



    //Method to generate a random String
    private String generateRandomString(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i<length; i++){
            int index = (int) (characters.length() * Math.random());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    //Methods for authorization checks
    private boolean isUserAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || authentication.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

//    private boolean isCurrentUserOrAdmin(Integer userId){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null){
//            //Get the current user's email
//            String currentUserEmail = authentication.getName();
//
//            //Check if the current user has admin role or owner of the account
//            boolean isAdmin = authentication.getAuthorities().stream()
//                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
//            User currentUser = userService.findUserByEmail(currentUserEmail);
//            boolean isCurrentUser = currentUser.getId().equals(userId);
//            return !isAdmin && !isCurrentUser;
//        }
//        return true;
//    }




    @Override
    public List<KeyDetails> getAllAccessKeysByEmail(String email) {
        List<KeyDetails> keyByEmail = new ArrayList<>();
        List<KeyDetails> allKeys = this.getAllAccessKeys();
        for (KeyDetails key: allKeys){
            if (key.getEmail().equalsIgnoreCase(email)){
                keyByEmail.add(key);
            }
        }
        return keyByEmail;
    }

    @Override //Working
    public List<KeyDetails> getAllAccessKeys() {
        List<AccessKey> keyList = accessKeyRepository.findAll();
        Map<Integer, KeyDetails> keyMap = new HashMap<>();
        keyList.forEach(key -> {
            KeyDetails keyDetails = new KeyDetails();
            keyDetails.setKey(key.getKey());
            keyDetails.setStatus(key.getStatus());
            keyDetails.setProcurementDate(key.getProcurementDate());
            keyDetails.setExpiryDate(key.getExpiryDate());
            keyDetails.setEmail(key.getUser().getEmail());
            keyDetails.setKeyName(key.getAccessKeyName());
            keyMap.put(key.getId(), keyDetails);
        });

        return keyMap.values().stream().toList();

    }

    @Override
    public KeyDetails getActiveAccessKeyByEmail(String email) {
        KeyDetails activeKeyByEmail = new KeyDetails();
        List<KeyDetails> allKeys = this.getAllAccessKeys();

        for (KeyDetails key: allKeys){
            if(key.getStatus().equals(AccessKey.AccessKeyStatus.ACTIVE));
            activeKeyByEmail = key;
        }
        return activeKeyByEmail;
    }
}
