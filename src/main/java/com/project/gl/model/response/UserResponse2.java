package com.project.gl.model.response;

import java.time.LocalDateTime;

import com.project.gl.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse2 {

    private Long id;
    private String name;
    private String email;
    private Integer userGroupId;
    private String userGroup;
    private Integer cabangId;
    private String cabangName;
    private String cabangKode;    
    private String active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Mapping null-safe dari entity User ke UserResponse2.
     */
    public static UserResponse2 from(User user) {
        if (user == null) {
            return null;
        }

        Integer cabangId = null;
        String cabangName = null;
        String cabangKode = null;
        if (user.getCabang() != null) {
            cabangId = user.getCabang().getId();
            cabangName = user.getCabang().getNamaCabang();
            cabangKode = user.getCabang().getKodeCabang();
        } else {
            cabangId = user.getCabangId(); // fallback ke kolom cabang_id
        }

        Integer userGroupId = null;
        String userGroupName = null;
        if (user.getUserGroup() != null) {
            userGroupId = user.getUserGroup().getId();
            userGroupName = user.getUserGroup().getName();
        }

        return UserResponse2.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userGroupId(userGroupId)
                .userGroup(userGroupName)
                .cabangId(cabangId)
                .cabangName(cabangName)
                .cabangKode(cabangKode)
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}