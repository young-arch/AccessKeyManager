package accesskey.access.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@ToString
public class KeyDetails {

    private String key;
    private AccessKey.AccessKeyStatus status;
    private LocalDateTime procurementDate;
    private LocalDateTime expiryDate;
    private String email;
    private String keyName;

}
