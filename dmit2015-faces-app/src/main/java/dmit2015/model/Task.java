package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Data // includes @Getter, @Setter, @ToString, etc..
@NoArgsConstructor
public class Task {

    private String id;  // unique identifier

    @NotBlank(message = "Description is required")
    @Size(min = 3, max=255,
            message = "Description must between 3 and 255 characters")
    private String description;

    @NotNull(message = "Priority must be assigned")
    private TaskPriority priority;    // Low, Medium, High

    private boolean done;

    public Task(Task other) {
        this.id = other.getId();
        this.description = other.getDescription();
        this.priority = other.getPriority();
        this.done = other.isDone();
    }

    public static Task copyOf(Task other) {
        return new Task(other);
    }

    public static Task of(Faker faker) {
        var currentTask = new Task();
        currentTask.setId(UUID.randomUUID().toString());
        currentTask.setDescription("Nuke " + faker.fallout().location());
        TaskPriority[] priorities = TaskPriority.values();
        int randomPriorityIndex = RandomGenerator.getDefault().nextInt(0, priorities.length);
        currentTask.setPriority(priorities[randomPriorityIndex]);
        currentTask.setDone(RandomGenerator.getDefault().nextBoolean());
        return currentTask;
    }

}
