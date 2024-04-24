package accesskey.access.Entity;

import jakarta.persistence.*;
import lombok.Data;
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

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime procurement_date;

    @Column(nullable = false)
    private LocalDateTime expiry_date;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updated_at = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "user_id", nullable = false)
    private User user;

    @PreUpdate
    protected void onUpdate(){
        updated_at = LocalDateTime.now();
    }

}
