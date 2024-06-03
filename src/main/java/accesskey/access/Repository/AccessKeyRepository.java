package accesskey.access.Repository;

import accesskey.access.Entity.AccessKey;
import accesskey.access.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Integer> {
    //Finds all access keys for a specific user
    List<AccessKey> findByUser(User user);

    //Find active access keys for a specific user
    List<AccessKey> findByUserAndStatus(User user, AccessKey.AccessKeyStatus status);


    //Update the status of an access key by ID
    @Modifying
    @Query("UPDATE AccessKey a SET a.status = ?2 WHERE a.id = ?1")
    void updateStatusById(Integer id, AccessKey.AccessKeyStatus newStatus );

    //Find all keys that have expired
    List<AccessKey> findAllByExpiryDateBefore(LocalDateTime now);

    //Find the access key by key String
    AccessKey findByKey(String key);

    @Modifying
    @Query("UPDATE AccessKey a SET a.status = ?2 WHERE a.id IN ?1")
    void updateStatusByIds(List<Integer> ids, AccessKey.AccessKeyStatus newStatus);

}
