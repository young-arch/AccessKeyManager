package accesskey.access.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordResetConfirmRequest {

    private String newPassword;
    @Getter
    @Setter
    private String confirmPassword;

}
