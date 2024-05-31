package accesskey.access.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "access_keys")
public class AccessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessKeyStatus status;

    @Column(nullable = false)
    private LocalDateTime procurementDate;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Column
    private String accessKeyName;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //Access key Status values
    public enum AccessKeyStatus{
        ACTIVE,
        EXPIRED,
        REVOKED
    }

}
