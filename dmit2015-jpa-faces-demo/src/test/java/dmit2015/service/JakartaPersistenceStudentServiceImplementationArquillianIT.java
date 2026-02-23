package dmit2015.service;

import dmit2015.config.ApplicationConfig;
import dmit2015.entity.Movie;
import dmit2015.entity.Student;
import dmit2015.entity.StudentInitializer;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import net.datafaker.Faker;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ArquillianTest
public class JakartaPersistenceStudentServiceImplementationArquillianIT { // The class must be declared as public

    static Faker faker = new Faker();

    static String mavenArtifactIdId;

    @Deployment
    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        mavenArtifactIdId = model.getArtifactId();
        final String archiveName = model.getArtifactId() + ".war";
        return ShrinkWrap.create(WebArchive.class, archiveName)
                .addAsLibraries(pomFile.resolve("org.codehaus.plexus:plexus-utils:3.4.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:3.0").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.27.6").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("net.datafaker:datafaker:2.5.1").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.3.232").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:13.2.0.jre11").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:23.9.0.25.07").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.postgresql:postgresql:42.7.7").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.mysql:mysql-connector-j:9.2.0").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.mariadb.jdbc:mariadb-java-client:3.5.3").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial:6.6.28.Final").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson:3.0.4").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Student.class, StudentInitializer.class, StudentService.class, JakartaPersistenceStudentService.class)
                .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Inject
    @Named("jakartaPersistenceStudentService")
    private JakartaPersistenceStudentService studentService;

    @Resource
    private UserTransaction beanManagedTransaction;

    @BeforeAll
    static void beforeAllTests() {
        // code to execute before all tests in the current test class
    }

    @AfterAll
    static void afterAllTests() {
        // code to execute after all tests in the current test class
    }

    @BeforeEach
    void beforeEachTestMethod() throws SystemException, NotSupportedException {
        // Start a new transaction
        beanManagedTransaction.begin();
    }

    @AfterEach
    void afterEachTestMethod() throws SystemException {
        // Rollback the transaction
        beanManagedTransaction.rollback();
    }

    @Test
    void findAll_whenSeeded_returnsInExpectedOrder() {
        // Act
        List<Student> students = studentService.getAllStudents();

        // Assert
        assertEquals(3, students.size());

        Student first = students.getFirst();
        assertAll("first student",
                () -> assertEquals("First1", first.getFirstName()),
                () -> assertEquals("Last1", first.getLastName()),
                () -> assertEquals("DMIT2015-A02", first.getSection())
        );

        Student last = students.getLast();
        assertAll("last student",
                () -> assertEquals("First3", last.getFirstName()),
                () -> assertEquals("Last3", last.getLastName()),
                () -> assertEquals("DMIT2015-A02", last.getSection())
        );
    }

}