package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDto {
    @NotBlank(groups = Create.class)
    String name;
    @NotBlank(groups = Create.class)
    @Email(message = "Enter correct e-mail", groups = {Create.class, Update.class})
    String email;
}
