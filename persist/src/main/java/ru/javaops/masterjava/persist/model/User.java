package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    @Column("city_id")
    private @NonNull String cityId;
    @Column("full_name")
    private @NonNull String fullName;
    private @NonNull String email;
    private @NonNull UserFlag flag;

    public User(Integer id, String cityId, String fullName, String email, UserFlag flag) {
        this(cityId, fullName, email, flag);
        this.id=id;
    }
}