package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

/**
 * Request-scoped backing bean: new instance per HTTP request.
 * Use for simple actions/data that don't need to persist after the response.
 */
@Named("helloWorldRequest")
@RequestScoped // New instance per HTTP request; no Serializable required
public class HelloWorldRequest {

    private static final Logger LOG = Logger.getLogger(HelloWorldRequest.class.getName());

    @Getter
    @Setter
    @NotBlank(message = "Please enter a username value.")
    private String username;

    @PostConstruct // Runs after @Inject is completed, once per request for this bean
    public void init() {
        // Keep this light; heavy work here runs every request.
        // Example: initialize defaults derived from request context.
    }

    public String onSubmit() {
        try {
            Messages.addGlobalInfo("Hello {0} and welcome to Faces World!", username);
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
        return "/exercises/lotto-number-generator?faces-redirect=true";
    }

    public void onClear() {
        // Reset request fields (mostly illustrative; a new request creates a new bean anyway)
        username = "";
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
