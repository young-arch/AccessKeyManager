package accesskey.access.Service;

import accesskey.access.Entity.User;
import accesskey.access.Exceptions.InvalidCredentialsException;
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
        //Set a default role if the role is not provided
        if(user.getRole() == null){
            user.setRole(accesskey.access.Entity.Role.SCHOOL_IT); //Default role as SCHOOL_IT
        }else{
            if(user.getRole() != accesskey.access.Entity.Role.SCHOOL_IT && user.getRole() != accesskey.access.Entity.Role.ADMIN){
            //Validate the provided role
                throw new IllegalArgumentException("Invalid role. Allowed roles are School_IT and Admin");
            }
        }
        //Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //Generate a verification token
        String verificationToken = generateVerificationToken();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpirationTime(LocalDateTime.now().plusMinutes(20)); //Token is valid for 20 minutes

        //Send Verification email
        String verificationLink = generateVerificationLink(verificationToken);
        emailService.sendEmail(user.getEmail(), "Account Verification", "Please verify your account by clicking the Verification link: " + verificationLink);

        //Save the user
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


    //Login user
    public User loginUser(String email, String password){
        //Find user by email
        User user = findUserByEmail(email);
        if(user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid password");
        }
        return user;
    }


    //Initiate password Reset using UUID
    public void initiatePasswordReset(String email){
        User user = findUserByEmail(email);

        if (user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        //Generate a unique reset token
        String resetToken = generateResetToken();

        saveResetToken(email, resetToken);

        String resetLink = generateResetLink(resetToken);

        //Send the password reset email
        emailService.sendEmail(email, "Password Reset Request", "Please reset your password by clicking the link: " + resetLink);

    }

    public String generateResetToken(){
        //Generate the Token
        return UUID.randomUUID().toString();
    }

    public void saveResetToken(String email, String token){
        //Find the user by email
        User user = findUserByEmail(email);
        if(user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }

        user.setResetToken(token);
        user.setResetTokenExpirationTime(LocalDateTime.now().plusMinutes(20));// OTP is valid for 10 minutes
        userRepository.save(user);

    }
    public String generateResetLink(String resetToken){
        return "https://accesskeymanager-kp51.onrender.com/auth/reset-password?token=" + resetToken;
    }

    public String generateResetLinkForFrontend(String resetToken) {
        return "https://accesskeymanager-kp51.onrender.com/auth/reset-password?token=" + resetToken;
    }




    public void resetPasswordWithToken(String resetToken, String newPassword, String confirmPassword){

        //Verify the token and the associated user
        User user = verifyResetToken(resetToken);

        //Check if entered passwords match
        if(!newPassword.equals(confirmPassword)){
            throw new IllegalArgumentException("Passwords do not match.");
        }

        //Update user's password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetToken(null); //Clear the OTP
        user.setResetTokenExpirationTime(null); //Clear the OTP expiration time
        userRepository.save(user);

        //Notification to the user for password change
        emailService.sendEmail(user.getEmail(), "Password Reset Successful", "Your password has been reset successfully.");
    }

    public User verifyResetToken(String resetToken){
        //Find the user
        User user = userRepository.findByResetToken(resetToken);

        if(user == null){
            throw new UserNotFoundException("Invalid token or Expired token");
        }

        //Check if the token has expired
        LocalDateTime currentTime = LocalDateTime.now();
        if(user.getResetTokenExpirationTime().isBefore(currentTime)){
            throw new UserNotFoundException("Token has expired");
        }
        return user;

    }

    //Initiate User Verification
    public void initiateVerification(String email){
        User user = findUserByEmail(email);

       //Generate a verification token
        String token = generateVerificationToken();

       //Save it
        saveVerificationToken(email, token);

        //Generate the verification link
        String verificationLink = generateVerificationLink(token);

        //Send the verification email
        emailService.sendEmail(email, "Email Verification", "To verify your email, please click the link: " + verificationLink);

    }

    public void saveVerificationToken(String email, String token) {
        // Find the user by email
        User user = findUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        user.setVerificationToken(token);
        user.setVerificationTokenExpirationTime(LocalDateTime.now().plusHours(7)); // Token valid for 7 hours
        userRepository.save(user);
    }




    //Generate verification token
    private String generateVerificationToken(){
        return UUID.randomUUID().toString();
    }

    //Generate verification link
    private String generateVerificationLink(String token){
        return "https://accesskeymanager-kp51.onrender.com/api/users/verify/confirm?token=" + token;
    }

    //Verify user's email
    public void confirmVerification(String token){
        //verify token and the associated user
        User user = verifyVerificationToken(token);

        //MArk user as verified
        user.setVerified(true);
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "Email Verification Successful", "Your email has been verified successfully");
    }

    public User verifyVerificationToken(String token){
        //Find user by verification token
        User user = userRepository.findByVerificationToken(token);

        if(user == null){
            throw new UserNotFoundException("Invalid token or expired token");
        }

        //Check if token has expired
        LocalDateTime currentTime = LocalDateTime.now();
        if (user.getVerificationTokenExpirationTime().isBefore(currentTime)){
            throw new UserNotFoundException("Token has expired");
        }
        return user;
    }

    //Delete a user email
    public void deleteUserByEmail(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UserNotFoundException("User with email " + email + " not found.");
        }
        userRepository.delete(user);
    }

    public User findUserById(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

}
