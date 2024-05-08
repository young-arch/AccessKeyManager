package accesskey.access.Repository;

import accesskey.access.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    void updatePasswordByEmail(String email, String newPassword);

    User findByResetToken(String token);


}
