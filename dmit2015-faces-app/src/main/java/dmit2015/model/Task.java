package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // includes @Getter, @Setter, @ToString, etc..
public class Task {

    @NotBlank(message = "Description is required")
    @Size(min = 3, max=255,
            message = "Description must between 3 and 255 characters")
    private String description;

    private String priority;    // Low, Medium, High

    private boolean done;

}
