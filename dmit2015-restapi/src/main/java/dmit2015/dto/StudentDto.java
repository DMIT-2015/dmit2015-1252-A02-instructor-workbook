package dmit2015.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class StudentDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String courseSection;

    private String username;
}
