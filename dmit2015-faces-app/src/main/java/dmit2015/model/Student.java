package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Student {

    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;

    public Student(Student other) {
        this.id = other.getId();
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
    }

    public static Student copyOf(Student other) {
        return new Student(other);
    }

    public static Student of(Faker faker) {
        Student currentStudent = new Student();
        currentStudent.setId(UUID.randomUUID().toString());
        currentStudent.setFirstName(faker.name().firstName());
        currentStudent.setLastName(faker.name().lastName());
        return currentStudent;
    }

}
