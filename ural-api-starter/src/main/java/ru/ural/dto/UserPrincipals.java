package ru.ural.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipals {

    @Schema(description = "UUID пользователя")
    private String uuid;

    @Schema(description = "Email пользователя")
    private String email;

    @Schema(description = "Имя пользователя")
    private String username;

}
