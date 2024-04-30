package accesskey.access.Service;

import accesskey.access.Entity.User;
import accesskey.access.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Create a new user
    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    //Find a user by email
    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    //Update a user's password
    public void updatePassword(String email, String newPassword){

        String encodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePasswordByEmail(email, encodedPassword);
    }

    //Check if a user exits by email
    public boolean userExists(String email){
        return userRepository.existsByEmail(email);
    }


}
