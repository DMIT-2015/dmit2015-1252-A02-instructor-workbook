package common.batch;

import jakarta.batch.api.AbstractBatchlet;
import jakarta.batch.api.BatchProperty;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Batchlet reads native SQL statements from a script file as a single String
 * and then executes the single SQL string.
 *
 * It either succeeds or fails. If it fails, it CAN be restarted and it runs again.
 */
@Named
@Dependent
public class ExecuteMultiLineSQLStatementBatchlet extends AbstractBatchlet {

    @Inject
    private JobContext jobContext;

    @Inject
    private Logger logger;

    @PersistenceContext//(unitName = "mssql-dwpubsales-jpa-pu")
    private EntityManager entityManager;

    @Inject
    @BatchProperty(name = "sql_script_file")
    private String sqlScriptFile;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public String process() throws Exception {
        try {
            if (sqlScriptFile == null || sqlScriptFile.trim().isEmpty()) {
                throw new IllegalArgumentException("The 'sql_script_file' batch property is not set.");
            }

            InputStream scriptStream = getClass().getResourceAsStream(sqlScriptFile);
            if (scriptStream == null) {
                throw new FileNotFoundException("SQL script file not found in classpath: " + sqlScriptFile);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(scriptStream))) {
                StringBuilder sqlStatementBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlStatementBuilder.append(line).append("\n");
                }

                entityManager.createNativeQuery(sqlStatementBuilder.toString()).executeUpdate();
            }

            return BatchStatus.COMPLETED.toString();

        } catch (Exception ex) {
            logger.log(Level.SEVERE,
                    String.format("Batch job %s failed to complete.", jobContext.getJobName()),
                    ex);
            throw ex;
        }
    }

}