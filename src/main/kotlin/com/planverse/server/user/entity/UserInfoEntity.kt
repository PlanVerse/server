package com.planverse.server.user.entity

import com.planverse.server.common.entity.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "user_info", schema = "public")
class UserInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    var name: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "nickname", nullable = false)
    var nickname: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "email", nullable = false)
    var email: String,

    @NotNull
    @Size(max = 255)
    @Column(name = "pwd", nullable = false)
    var pwd: String,

    @NotNull
    @Column(name = "authentication", nullable = false)
    var authentication: Boolean,

    ) : BaseEntity(), UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return null
    }

    override fun getPassword(): String {
        return pwd
    }

    override fun getUsername(): String {
        return email
    }
}