package accesskey.access.DTO;

import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private String email;

    private String password;


}
