package net.gentledot.demorestapi.accounts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Schema(example = "40", description = "자동 생성 값")
    @Id
    @GeneratedValue
    private Integer id;

    @Schema(example = "user@email.com")
    @Column(unique = true)
    private String email;

    @Schema(hidden = true)
    private String password;

    @Schema(hidden = true)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
