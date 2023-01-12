package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import vn.clmart.manager_service.dto.UserLoginDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends PersistableEntity<String> {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String uid;
    private String code;
    @Column(name = "user_name")
    private String username;
    private String password;

    @Override
    public String getId() {
        return this.uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return uid != null && Objects.equals(uid, user.uid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static User of(UserLoginDto userLoginDto, Long cid, String uid){
        User user = User.builder()
                .username(userLoginDto.getUsername())
                .password(userLoginDto.getPassword())
                .build();
        user.setCreateBy(uid);
        user.setCompanyId(cid);
        return user;
    }
}
