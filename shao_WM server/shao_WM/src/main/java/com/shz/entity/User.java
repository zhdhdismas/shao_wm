package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"username","phone","email"})
public class User implements UserDetails, Serializable {

    @TableId(type = IdType.AUTO)
    private int uid;
    @NonNull
    private String username;
    private String password;
    @NonNull
    private String phone;
    @NonNull
    private String email;
    private boolean enabled;
    private Timestamp regTime;
    private int roleName;
    private String avatar;
    private boolean staff;

    @TableField(exist = false)
    private List<Address> addresses;

    @TableField(exist = false)
    private int count;



    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }



    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    @JsonIgnore
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<> ();
        authorities.add(new SimpleGrantedAuthority ("ROLE_" + roleName ));
        return authorities;
    }



}
