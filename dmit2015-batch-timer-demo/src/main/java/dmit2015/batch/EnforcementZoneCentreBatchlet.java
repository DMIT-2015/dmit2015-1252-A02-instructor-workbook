package dmit2015.batch;

import dmit2015.entity.EnforcementZoneCentre;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.Batchlet;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.context.JobContext;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Batchlets are task oriented step that is called once.
 * It either succeeds or fails. If it fails, it CAN be restarted and it runs again.
 */
@Named
@Dependent
public class EnforcementZoneCentreBatchlet implements Batchlet {

    @Inject
    private Logger logger;

    @PersistenceContext(unitName = "mssql-jpa-pu")
    private EntityManager entityManager;

    @Inject
    private JobContext jobContext;

    @Inject
    @BatchProperty(name = "input_file")
    private String inputFile;

    /**
    * Perform the batchlet task and return an exit status string when processing
    * finishes successfully.
    *
    * Important:
    * The value returned from process() is the exit status, not the Jakarta Batch
    * BatchStatus. Returning "FAILED" does not cause the step or job to fail.
    * To mark the batch step/job as FAILED, this method must throw an exception.
    *
    * The rollbackOn = Exception.class setting ensures that both checked and
    * unchecked exceptions cause the transaction to roll back. This is important
    * because batch processing may throw checked exceptions such as
    * FileNotFoundException or other data-processing exceptions.
    *
    * @return "COMPLETED" when processing finishes successfully
    * @throws Exception if processing fails so the batch runtime marks the step/job as FAILED
    */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public String process() throws Exception {

        try {
            // Ensure inputFile property is set
            if (inputFile == null || inputFile.trim().isEmpty()) {
                throw new IllegalArgumentException("The 'inputFile' batch property is not set.");
            }

            Path filePath = Paths.get(inputFile);
            // Validate the file on the filesystem
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("Input file not found on filesystem: " + filePath.toAbsolutePath());
            }
            if (!Files.isReadable(filePath)) {
                throw new IllegalArgumentException("The input file is not readable: " + filePath.toAbsolutePath());
            }

            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;

                // Skip the first line because it contains column headings
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    Optional<EnforcementZoneCentre> optionalEnforcementZoneCentre =
                            EnforcementZoneCentre.parseCsv(line);
                    EnforcementZoneCentre currentEnforcementZoneCentre =
                            optionalEnforcementZoneCentre.orElseThrow();

                    entityManager.persist(currentEnforcementZoneCentre);
                }
            }

            return BatchStatus.COMPLETED.toString();

        } catch (Exception ex) {
            String errorMessage = String.format(
                    "Batch job %s failed to complete.",
                    jobContext.getJobName());
            logger.log(Level.SEVERE, errorMessage, ex);
            throw ex;
        }
    }

    @Override
    public void stop() throws Exception {
        logger.info("Batch job stop requested.");
    }
}