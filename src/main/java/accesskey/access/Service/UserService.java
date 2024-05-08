package accesskey.access.Service;

import accesskey.access.Entity.User;
import accesskey.access.Exceptions.UserNotFoundException;
import accesskey.access.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder, EmailService emailService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    //Update a user's password
    public void updatePassword(String email, String newPassword){
        String encodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePasswordByEmail(email, encodedPassword);

        //Email notification for password reset
        emailService.sendEmail(email, "Password Reset", "Your password has been reset successfully.");
    }

    //Create a new user
    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    //Find a user by email
    public User findUserByEmail(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UserNotFoundException("The user with email " + email + " was not found");

        }
        return user;
    }

    //Check if a user exits by email
    public boolean userExists(String email){

        return userRepository.existsByEmail(email);
    }

    //Initiate password Reset
    public void initiatePasswordReset(String email){
        User user = findUserByEmail(email);
        String token = generateResetToken(); //Generate a unique token;
        saveResetToken(email, token);

        //Send password reset email
        String resetLink = generateResetLink(token);
        emailService.sendEmail(email, "Password Reset Request", "To reset your password, please click the link: " + resetLink);

    }

    public String generateResetToken(){
        //Generate a Unique token using UUID
        return UUID.randomUUID().toString();
    }

    public void saveResetToken(String email, String token){
        //Find the user by email
        User user = findUserByEmail(email);
        if(user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        user.setResetToken(token);
        user.setTokenExpirationTime(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

    }

    public String generateResetLink(String token){

        return "http://localhost:8080/api/users/password/reset/confirm?token=" + token;
    }




}
