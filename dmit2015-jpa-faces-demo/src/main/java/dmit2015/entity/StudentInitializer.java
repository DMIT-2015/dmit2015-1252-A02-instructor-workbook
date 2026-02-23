package dmit2015.entity;

import dmit2015.entity.Student;
import dmit2015.service.JakartaPersistenceStudentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class StudentInitializer {
    private final Logger logger = Logger.getLogger(StudentInitializer.class.getName());

    @Inject
    @Named("jakartaPersistenceStudentService")
    private JakartaPersistenceStudentService studentService;


    /**
     * Using the combination of `@Observes` and `@Initialized` annotations, you can
     * intercept and perform additional processing during the phase of beans or events
     * in a CDI container.
     * <p>
     * The @Observers is used to specify this method is in observer for an event
     * The @Initialized is used to specify the method should be invoked when a bean type of `ApplicationScoped` is being
     * initialized
     * <p>
     * Execute code to create the test data for the entity.
     * This is an alternative to using a @WebListener that implements a ServletContext listener.
     * <p>
     * ]    * @param event
     */
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        logger.info("Initializing students");

        if (studentService.count() == 0) {
            try {
                var student1 = new Student();
                student1.setFirstName("First1");
                student1.setLastName("Last1");
                student1.setEmail("first1.last1@dmit2015.ca");
                student1.setSection("DMIT2015-A02");
                studentService.createStudent(student1);

                var student2 = new Student();
                student2.setFirstName("First2");
                student2.setLastName("Last2");
                student2.setEmail("first2.last2@dmit2015.ca");
                student2.setSection("DMIT2015-A02");
                studentService.createStudent(student2);

                var student3 = new Student();
                student3.setFirstName("First3");
                student3.setLastName("Last3");
                student3.setEmail("first3.last3@dmit2015.ca");
                student3.setSection("DMIT2015-A02");
                studentService.createStudent(student3);

            } catch (Exception ex) {
                logger.warning(ex.getMessage());
            }

            logger.info("Created " + studentService.count() + " records.");
        }
    }
}