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
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccessKeyService implements AccessKeyServiceInterface{
    private final UserService userService;
    private final AccessKeyRepository accessKeyRepository;


    //Create a new Access key
    public AccessKey createAccessKeyWithCustomName(String customName, Integer userId){
        //Check if current user has an active Key
        if (userHasActiveKey(userId)){
            throw new IllegalStateException("User already has an active Key.");
        }
        //Generate a custom key name
        String key = generateRandomString(10) + "-" + customName;

        AccessKey accessKey = new AccessKey();
        accessKey.setKey(key);
        accessKey.setStatus(AccessKey.AccessKeyStatus.ACTIVE);
        accessKey.setProcurementDate(LocalDateTime.now());
        accessKey.setExpiryDate(LocalDateTime.now().plusMinutes(1));
        accessKey.setUser(this.userService.findUserById(userId));
        accessKey.setAccessKeyName(customName);

        return accessKeyRepository.save(accessKey);
    }

    public boolean userHasActiveKey(Integer userId){
        User user = userService.findUserById(userId);
        List<AccessKey> activeKeys = accessKeyRepository.findByUserAndStatus(user, AccessKey.AccessKeyStatus.ACTIVE);
        return !activeKeys.isEmpty();

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
    public KeyDetails getActiveAccessKeyByEmail(String email){
        List<KeyDetails> allKeys = this.getAllAccessKeysByEmail(email);
        for (KeyDetails key : allKeys){
            if (key.getStatus().equals(AccessKey.AccessKeyStatus.ACTIVE)){
                return key;
            }
        }
        throw new AccessKeyNotFoundException("No active access key not found for email: " + email);
    }

}
