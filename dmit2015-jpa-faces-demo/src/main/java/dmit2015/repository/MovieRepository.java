package dmit2015.repository;

import dmit2015.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MovieRepository {
    @PersistenceContext //(unitName = "postgresql-jpa-pu") // unitName is optional if persistence.xml contains only one persistence-unit
    private EntityManager em;

    @Transactional
    public void add(Movie newMovie) {
        em.persist(newMovie);
    }

    @Transactional
    public void update(Movie updatedMovie) {
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
        if (!em.contains(existingMovie)) {
            existingMovie = em.merge(existingMovie);
        }
        em.remove(existingMovie);
    }

    @Transactional
    public void deleteById(Long id) {
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
        return em.createQuery("SELECT m FROM Movie m ", Movie.class)
                .getResultList();
    }

    public List<Movie> findAllOrderByTitle() {
        return em.createQuery("SELECT m FROM Movie m ORDER BY m.title", Movie.class)
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

