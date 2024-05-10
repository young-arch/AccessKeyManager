package accesskey.access.Repository;

import accesskey.access.Entity.AccessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Integer> {
    //Finds all access keys for a specific user
    List<AccessKey> findByUserId(Integer userId);

    //Finds all the active access key for a specific user
    AccessKey findByUserIdAndStatus(Integer user_id, AccessKey.AccessKeyStatus status);

    //Checks if there is an active key for a specific key
    boolean existsByUserIdAndStatus(Integer user_id, AccessKey.AccessKeyStatus status);

    //Update the status of an access key by ID
    void updateStatusById(Integer id, String newStatus );

    //Find all keys that have expired
    List<AccessKey> findAllByExpiryDateBefore(LocalDateTime now);

    //Find the access key by key String
    AccessKey findByKey(String key);

    //Retrieve all access key
    List<AccessKey> findAll();

}
