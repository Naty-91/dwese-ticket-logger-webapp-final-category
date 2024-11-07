package org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.entity.Location;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Repository
@Transactional
public class LocationDAOImpl implements LocationDAO {

    private static final Logger logger = LoggerFactory.getLogger(LocationDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista todas las ubicaciones de la base de datos.
     * @return Lista de ubicaciones
     */
    @Override
    public List<Location> listAllLocations() {
        logger.info("Listing all locations from the database.");
        String query = "SELECT l " +
                "FROM Location l " +
                "JOIN FETCH l.province  " +
                "JOIN FETCH l.supermarket ";
        List<Location> locations = entityManager.createQuery(query, Location.class).getResultList();
        logger.info("Retrieved {} locations from the database.", locations.size());
        return locations;
    }

    /**
     * Inserta una nueva ubicación en la base de datos.
     * @param location Ubicación a insertar
     */
    @Override
    public void insertLocation(Location location) {
        logger.info("Inserting location with address: {}", location.getAddress());
        entityManager.persist(location);
        logger.info("Inserted location with ID: {}", location.getId());
    }

    /**
     * Actualiza una ubicación existente en la base de datos.
     * @param location Ubicación a actualizar
     */
    @Override
    public void updateLocation(Location location) {
        logger.info("Updating location with id: {}", location.getId());
        entityManager.merge(location);
        logger.info("Updated location with id: {}", location.getId());
    }

    /**
     * Elimina una ubicación de la base de datos.
     * @param id ID de la ubicación a eliminar
     */
    @Override
    public void deleteLocation(int id) {
        logger.info("Deleting location with id: {}", id);
        Location location = entityManager.find(Location.class, id);
        if (location != null) {
            entityManager.remove(location);
            logger.info("Deleted location with id: {}", id);
        } else {
            logger.warn("Location with id: {} not found.", id);
        }
    }

    /**
     * Recupera una ubicación por su ID.
     * @param id ID de la ubicación a recuperar
     * @return Ubicación encontrada o null si no existe
     */
    @Override
    public Location getLocationById(int id) {
        logger.info("Retrieving location by id: {}", id);
     Location location = entityManager.find(Location.class, id);
        if (location != null) {
            logger.info("Location retrieved: {} - {}", location.getAddress(), location.getCity());
        } else {
            logger.warn("No location found with id: {}", id);
        }
        return location;
    }

    /**
     * Verifica si una ubicación con la dirección especificada ya existe en la base de datos.
     * @param address la dirección de la ubicación a verificar.
     * @return true si una ubicación con la dirección ya existe, false de lo contrario.
     */
    @Override
    public boolean existsLocationByAddress(String address) {
        logger.info("Checking if location with address: {} exists", address);
        String query = "SELECT COUNT(*) FROM Location WHERE UPPER(address) = ?";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("address", address.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Location with address: {} exists: {}", address, exists);
        return exists;
    }

    /**
     * Verifica si una ubicación con la dirección especificada ya existe en la base de datos,
     * excluyendo una ubicación con un ID específico.
     * @param address la dirección de la ubicación a verificar.
     * @param id   el ID de la ubicación a excluir de la verificación.
     * @return true si una ubicación con la dirección ya existe (y no es la ubicación con el ID dado),
     *         false de lo contrario.
     */
    @Override
    public boolean existsLocationByAddressAndNotId(String address, int id) {
        logger.info("Checking if location with address: {} exists excluding id: {}", address, id);
        String query = "SELECT COUNT(*) FROM Location WHERE UPPER(address) = ? AND id != ?";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("address", address.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Province with address: {} exists excluding id {}: {}", address, id, exists);
        return exists;
    }


}
