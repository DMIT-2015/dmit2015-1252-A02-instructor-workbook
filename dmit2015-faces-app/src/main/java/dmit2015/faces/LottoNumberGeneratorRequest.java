package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

/**
 * Request-scoped backing bean: new instance per HTTP request.
 * Use for simple actions/data that don't need to persist after the response.
 */
@Named("lottoNumberGeneratorRequest")
@RequestScoped // New instance per HTTP request; no Serializable required
public class LottoNumberGeneratorRequest {

    private static final Logger LOG = Logger.getLogger(LottoNumberGeneratorRequest.class.getName());

    @Getter
    @Setter
    @Min(value = 3, message = "Minimum value for number is {value}")
    private int maxValue = 49;

    @PostConstruct // Runs after @Inject is completed, once per request for this bean
    public void init() {
        // Keep this light; heavy work here runs every request.
        // Example: initialize defaults derived from request context.
    }

    public void onSubmit() {
        try {
            // Generate a random number between 1 and maxValue
            int randomNumber = RandomGenerator.getDefault().nextInt(1,maxValue + 1);
            // Add a global Faces message
            Messages.addGlobalInfo("Your lucky lotto number is {0}", randomNumber);
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
    }

    public void onClear() {
        // Reset request fields (mostly illustrative; a new request creates a new bean anyway)
        maxValue = 49;
    }

    /**
     * Log server-side and show a concise root-cause chain in the UI.
     * Assumes the page includes <p:messages id="error" />.
     */
    protected void handleException(Throwable ex, String userMessage) {
        LOG.log(Level.SEVERE, userMessage != null ? userMessage : "Unhandled error", ex);

        StringBuilder details = new StringBuilder();
        Throwable t = ex;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null && !msg.isBlank()) {
                details.append(t.getClass().getSimpleName())
                        .append(": ")
                        .append(msg);
                if (t.getCause() != null) details.append("  Caused by: ");
            }
            t = t.getCause();
        }

        try {
            Messages.create(userMessage != null ? userMessage : "An unexpected error occurred.")
                    .detail(details.toString())
                    .error()
                    .add("messages");
        } catch (Throwable ignored) {
            // No FacesContext available; skip UI notification safely.
        }
    }
}
