package dmit2015.repository;

import dmit2015.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MovieRepository {

    @Inject
    private SecurityContext securityContext;

    @PersistenceContext //(unitName = "postgresql-jpa-pu") // unitName is optional if persistence.xml contains only one persistence-unit
    private EntityManager em;

    private void requiresAuthentication() {
        String username = securityContext.getCallerPrincipal().getName();
        // Unauthenticated username is anonymous
        if (username.equalsIgnoreCase("anonymous")) {
            throw new RuntimeException("Access denied. Only authenticated are allowed to perform this operation.");
        }
    }

    private void requiresSalesRole() {
        requiresAuthentication();
        boolean hasRequiredRole = securityContext.isCallerInRole("Sales");
        if (!hasRequiredRole) {
            throw new RuntimeException("Access denied. Your role does not have permission to perform this operation.");
        }
    }

    private void requiresSalesOrShippingRole() {
        requiresAuthentication();
        boolean hasRequiredRole = securityContext.isCallerInRole("Sales")
                || securityContext.isCallerInRole("Shipping");
        if (!hasRequiredRole) {
            throw new RuntimeException("Access denied. Your role does not have permission to perform this operation.");
        }
    }

    @Transactional
    public void add(Movie newMovie) {
        // Allow only authenticated users with the Sales role to create movie
        requiresSalesRole();
        // Specify ownership of movie
        String username = securityContext.getCallerPrincipal().getName();
        newMovie.setUsername(username);

        em.persist(newMovie);
    }

    @Transactional
    public void update(Movie updatedMovie) {
        // Allow only authenticated users with the Sales role to update movie
        requiresSalesRole();

        Movie existingMovie = em.find(Movie.class, updatedMovie.getId());
        if (existingMovie == null) return;

        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setPrice(updatedMovie.getPrice());
        existingMovie.setRating(updatedMovie.getRating());
        existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
        // merge not needed; managed entity will be flushed at commit
    }

    @Transactional
    public void delete(Movie existingMovie) {
        requiresSalesOrShippingRole();
        if (!em.contains(existingMovie)) {
            existingMovie = em.merge(existingMovie);
        }
        em.remove(existingMovie);
    }

    @Transactional
    public void deleteById(Long id) {
        requiresSalesOrShippingRole();
        Optional<Movie> optionalMovie = findOptionalById(id);
        if (optionalMovie.isPresent()) {
            Movie existingMovie = optionalMovie.orElseThrow();
            em.remove(existingMovie);
        }
    }

    public Movie findById(Long id) {
        return em.find(Movie.class, id);
    }

    public Optional<Movie> findOptionalById(Long id) {
        return Optional.ofNullable(em.find(Movie.class, id));
    }

    public List<Movie> findAll() {
        // Shipping role has access to all Movies
        // Sales role has access to Movies owned by the username
        // Anonymous and other roles are not allowed
        requiresSalesOrShippingRole();
        boolean hasShippingRole = securityContext.isCallerInRole("Shipping");
        if (hasShippingRole) {
            return em.createQuery("SELECT m FROM Movie m ", Movie.class)
                    .getResultList();
        }
        // Must be in Sales role
        String username = securityContext.getCallerPrincipal().getName();
        return em.createQuery("SELECT m FROM Movie m where m.username = :usernameValue", Movie.class)
                .setParameter("usernameValue",username)
                .getResultList();
    }

    public List<Movie> findAllOrderByTitle() {
        // Shipping role has access to all Movies
        // Sales role has access to Movies owned by the username
        // Anonymous and other roles are not allowed
        requiresSalesOrShippingRole();
        boolean hasShippingRole = securityContext.isCallerInRole("Shipping");
        if (hasShippingRole) {
            return em.createQuery("SELECT m FROM Movie m ORDER BY m.title", Movie.class)
                    .getResultList();
        }
        // Must be in Sales role
        String username = securityContext.getCallerPrincipal().getName();
        return em.createQuery("SELECT m FROM Movie m WHERE m.username = :usernameValue ORDER BY m.title", Movie.class)
                .setParameter("usernameValue",username)
                .getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(m) FROM Movie m", Long.class).getSingleResult().longValue();
    }

    @Transactional
    public void deleteAll() {
        em.createQuery("DELETE FROM Movie").executeUpdate();
    }

}

