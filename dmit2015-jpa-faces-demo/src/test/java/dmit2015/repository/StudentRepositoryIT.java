package dmit2015.repository;

import dmit2015.config.ApplicationConfig;
import dmit2015.entity.Movie;
import dmit2015.entity.MovieInitializer;
import dmit2015.entity.Student;
import dmit2015.entity.StudentInitializer;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ArquillianTest
public class StudentRepositoryIT {
    @Deployment
    static WebArchive createDeployment() {
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
/*
   <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.27.7</version>
            <scope>test</scope>
        </dependency>
 */
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsLibraries(pom.resolve("com.h2database:h2:2.3.232").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("com.oracle.database.jdbc:ojdbc11:23.26.0.0.0").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.postgresql:postgresql:42.7.8").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.mariadb.jdbc:mariadb-java-client:3.5.3").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("org.hamcrest:hamcrest:3.0").withTransitivity().asFile())
                .addAsLibraries(pom.resolve("rg.assertj:assertj-core:3.27.7").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Student.class, StudentInitializer.class, StudentRepository.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Inject
    private StudentRepository studentRepository;

    @Resource
    private UserTransaction userTransaction;

    @Test
    void add_whenValidData_persistAndSetsCreateTime() throws SystemException, NotSupportedException {
        userTransaction.begin();
        try {
            // Arrange
            Student newStudent = new Student();
            newStudent.setFirstName("Marco");
            newStudent.setLastName("Pollo");
            newStudent.setEmail("marco.Pollo@dmit2015.ca");
            newStudent.setSection("DMIT2015-A02");
            // Act
            studentRepository.add(newStudent);
            // Assert
            Student savedStudent = studentRepository.findById(newStudent.getId());
            assertAll("saved student",
                    () -> assertEquals(newStudent.getFirstName(), savedStudent.getFirstName()),
                    () -> assertEquals(newStudent.getLastName(), savedStudent.getLastName()),
                    () -> assertEquals(newStudent.getEmail(), savedStudent.getEmail()),
                    () -> assertEquals(newStudent.getSection(), savedStudent.getSection()),
                    () -> assertNotNull(savedStudent.getCreateTime())
                    );
            long minutesSinceCreate = savedStudent.getCreateTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            assertEquals(0, minutesSinceCreate);

        } finally {
            userTransaction.rollback();
        }
    }

    @Test
    void findAll_whenSeed_returnsInExpectedOrder() {
        // Arrange and Act
        List<Student> students = studentRepository.findAll();

        // Assert
        assertEquals(3, students.size());

        Student first = students.getFirst();
        assertAll("first student",
                () -> assertEquals("Marco", first.getFirstName()),
                () -> assertEquals("Pollo", first.getLastName()),
                () -> assertEquals("DMIT2015-A02", first.getSection()),
                () -> assertEquals("m.pollo@dmit2015.ca", first.getEmail())
        );

        Student last = students.getLast();
        assertAll("first student",
                () -> assertEquals("Reysan", last.getFirstName()),
                () -> assertEquals("Pollo", last.getLastName()),
                () -> assertEquals("DMIT2015-A02", last.getSection()),
                () -> assertEquals("r.pollo@dmit2015.ca", last.getEmail())
        );


    }
}
