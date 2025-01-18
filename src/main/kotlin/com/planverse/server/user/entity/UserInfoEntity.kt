package com.planverse.server.user.entity

import com.planverse.server.common.constant.SystemRole
import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user_info", schema = "public")
class UserInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Size(max = 255)
    @NotNull
    @ColumnDefault("(gen_random_uuid())")
    @Column(name = "key")
    var key: String? = null,

    @NotNull
    @Size(max = 255)
    @Column(name = "name")
    var name: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "nickname")
    var nickname: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "email")
    val email: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "pwd")
    val pwd: String,

    @NotNull
    @Column(name = "authentication")
    var authentication: Boolean,

    @Column(nullable = false)
    @Size(max = 30)
    @Enumerated(EnumType.STRING)
    var role: SystemRole,

    ) : BaseEntity(), UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority(role.name))
        return authorities
    }

    override fun getPassword(): String {
        return pwd
    }

    override fun getUsername(): String {
        return email
    }
}