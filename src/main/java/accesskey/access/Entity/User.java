package accesskey.access.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;


    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Setter
    @Getter
    @Column
    private String resetToken;

    @Setter
    @Getter
    @Column
    private LocalDateTime resetTokenExpirationTime;

    @Setter
    @Getter
    @Column
    private String verificationToken;


    @Setter
    @Getter
    @Column
    private LocalDateTime verificationTokenExpirationTime;



    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }



}

