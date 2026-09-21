package com.capitalbanking.stage.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RI_AUTHORITY_WS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements GrantedAuthority {

    @Id
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private UserRoleName name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authorities", cascade = {CascadeType.PERSIST})
    private Set<User> user = new HashSet<>();

    @Override
    public String getAuthority() {
        return name != null ? name.name() : null;
    }

    public UserRoleName getLabel() {
        return name;
    }
}