package org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entity.Supermarket;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entity.Supermarket;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class SupermarketDAOImpl implements SupermarketDAO {

    // Logger para registrar eventos importantes en el DAO
    private static final Logger logger = LoggerFactory.getLogger(SupermarketDAOImpl.class);


    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Lista todos los supermercados de la base de datos.
     * @return Lista de supermercados
     */
    @Override
    public List<Supermarket> listAllSupermarkets() {
        logger.info("Listing all supermarkets from the database.");
        String query = "SELECT s FROM Supermarket s";
        List<Supermarket> supermarkets = entityManager.createQuery(query, Supermarket.class).getResultList();
        logger.info("Retrieved {} supermarkets from the database.", supermarkets.size());
        return supermarkets;
    }

    /**
     * Inserta un nuevo supermercado en la base de datos.
     * @param supermarket Supermercado a insertar
     */
    @Override
    public void insertSupermarket(Supermarket supermarket) {
        logger.info("Inserting supermarket with name: {}", supermarket.getName());
        entityManager.persist(supermarket);
        logger.info("Inserted supermarket with ID: {}", supermarket.getId());
    }

    /**
     * Actualiza un supermercado existente en la base de datos.
     * @param supermarket Supermercado a actualizar
     */
    @Override
    public void updateSupermarket(Supermarket supermarket) {
        logger.info("Updating supermarket with id: {}", supermarket.getId());
        entityManager.merge(supermarket);
        logger.info("Updated supermarket with id: {}", supermarket.getId());
    }

    /**
     * Elimina un supermercado de la base de datos.
     * @param id ID del supermercado a eliminar
     */
    @Override
    public void deleteSupermarket(int id) {
        logger.info("Deleting supermarket with id: {}", id);
        Supermarket supermarket = entityManager.find(Supermarket.class, id);
        if (supermarket != null) {
            entityManager.remove(supermarket);
            logger.info("Deleted supermarket with id: {}", id);
        } else {
            logger.warn("Supermarket with id: {} not found.", id);
        }
    }

    /**
     * Recupera un supermercado por su ID.
     * @param id ID del supermercado a recuperar
     * @return Supermercado encontrado o null si no existe
     */
    @Override
    public Supermarket getSupermarketById(int id) {
        logger.info("Retrieving supermarket by id: {}", id);
        Supermarket supermarket = entityManager.find(Supermarket.class, id);
        if (supermarket != null) {
            logger.info("Region retrieved: {}", supermarket.getName());
        } else {
            logger.warn("No supermarket found with id: {}", id);
        }
        return supermarket;
    }

    /**
     * Verifica si un supermercado con el nombre especificado ya existe en la base de datos.
     * @param name el nombre del supermercado a verificar.
     * @return true si un supermercado con el nombre ya existe, false de lo contrario.
     */
    @Override
    public boolean existsSupermarketByName(String name) {
        logger.info("Checking if supermarket with name: {} exists", name);
        String query = "SELECT COUNT(s) FROM Supermarket s WHERE UPPER(s.name) = :name";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("name", name.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Supermarket with name: {} exists: {}", name, exists);
        return exists;
    }
    /**
     * Verifica si un supermercado con el nombre especificado ya existe en la base de datos,
     * excluyendo un supermercado con un ID específico.
     * @param name el nombre del supermercado a verificar.
     * @param id   el ID del supermercado a excluir de la verificación.
     * @return true si un supermercado con el nombre ya existe (y no es el supermercado con el ID dado),
     *         false de lo contrario.
     */
    @Override
    public boolean existsSupermarketByNameAndNotId(String name, int id) {
            logger.info("Checking if region with name: {} exists excluding id: {}", name, id);
            String query = "SELECT COUNT(s) FROM Superamarket s WHERE UPPER(s.name) = :name AND s.id != :id";
            Long count = entityManager.createQuery(query, Long.class)
                    .setParameter("name", name.toUpperCase())
                    .setParameter("id", id)
                    .getSingleResult();
            boolean exists = count != null && count > 0;
            logger.info("Supermarket with name: {} exists excluding id {}: {}", name, id, exists);
            return exists;
        }
}
