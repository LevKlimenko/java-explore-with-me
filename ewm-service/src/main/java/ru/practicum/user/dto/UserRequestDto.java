package ru.practicum.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDto {
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    String name;
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    @Email(message = "Enter correct e-mail", groups = {Create.class, Update.class})
    String email;
}