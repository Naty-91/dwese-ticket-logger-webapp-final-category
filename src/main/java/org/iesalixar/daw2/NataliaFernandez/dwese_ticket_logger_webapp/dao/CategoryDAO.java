package org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.dao;

import org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.entity.Category;

import java.util.List;

public interface CategoryDAO {

    List<Category> listAllCategories();

    void insertCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(int id);

    Category getCategoryById(int id);

    boolean existsCategoryByName(String name);

    boolean existsCategoryByNameAndNotId(String name, int id);
}
