package com.capitalbanking.stage.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ri_usr_crd_ws")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_Sequence")
    @org.hibernate.annotations.GenericGenerator(
            name = "users_Sequence",
            strategy = "sequence",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence", value = "ri_usr_crd_ws_seq")
            }
    )
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String details;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "modification_date")
    private Date modificationDate;

    @Transient
    @JsonIgnore
    private String oldPassword;

    @Transient
    @JsonIgnore
    private String newPassword;

    @Transient
    private StringBuilder message = new StringBuilder();

    @Column(name = "enabled")
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ri_users_authority_ws",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id", insertable = false)})
    private Set<Authority> authorities = new HashSet<>();


    public long getId() {
        return id != null ? id : 0L;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends Authority> getAuthorities() {
        return this.authorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}