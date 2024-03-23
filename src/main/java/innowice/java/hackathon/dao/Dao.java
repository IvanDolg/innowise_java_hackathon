package innowice.java.hackathon.dao;

public interface Dao <T, ID> {
    T save(T entity);
}
