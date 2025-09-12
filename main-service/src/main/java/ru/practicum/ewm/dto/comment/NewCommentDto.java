package ru.practicum.ewm.dto.comment;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDto {

    @NotNull
    @Size(min = 1, max =  1000)
    private String text;
}
