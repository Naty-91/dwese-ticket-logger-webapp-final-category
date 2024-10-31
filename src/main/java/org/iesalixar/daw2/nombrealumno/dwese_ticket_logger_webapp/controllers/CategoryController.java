package org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.dao.CategoryDAO;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listCategories(Model model) {
        logger.info("Solicitando la lista de todas las categorías...");
        List<Category> listCategories = null;
        try {
            listCategories = categoryDAO.listAllCategories();
            logger.info("Se han cargado {} categorías.", listCategories.size());
        } catch (Exception e) {
            logger.error("Error al listar las categorías: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error al listar las categorías.");
        }
        model.addAttribute("listCategories", listCategories);
        return "category";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        logger.info("Mostrando formulario de edición para la categoría con ID {}", id);
        Category category = null;
        try {
            category = categoryDAO.getCategoryById(id);
            if (category == null) {
                logger.warn("No se encontró la categoría con ID {}", id);
            }
        } catch (Exception e) {
            logger.error("Error al obtener la categoría con ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Error al obtener la categoría.");
        }
        model.addAttribute("category", category);
        return "category-form"; // Nombre de la plantilla Thymeleaf para el formulario
    }

    @PostMapping("/insert")
    public String insertCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {
        logger.info("Insertando nueva categoría con nombre {}", category.getName());
        try {
            if (result.hasErrors()) {
                List<Category> listCategories = categoryDAO.listAllCategories();
                model.addAttribute("listCategories", listCategories);
                return "category-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            if (categoryDAO.existsCategoryByName(category.getName())) {
                logger.warn("El código de la categoría {} ya existe.", category.getName());
                String errorMessage = messageSource.getMessage("msg.category-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/categories/new";
            }
            categoryDAO.insertCategory(category);
            logger.info("Categoría {} insertada con éxito.", category.getName());
        } catch (Exception e) {
            logger.error("Error al insertar la categoría {}: {}", category.getName(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.category-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/categories"; // Redirigir a la lista de categorías
    }

    @PostMapping("/update")
    public String updateCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                 RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Actualizando categoría con ID {}", category.getId());
        try {
            if (result.hasErrors()) {
                return "category-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            if (categoryDAO.existsCategoryByNameAndNotId(category.getName(), category.getId())) {
                logger.warn("El código de la categoría {} ya existe para otra categoría.", category.getName());
                String errorMessage = messageSource.getMessage("msg.category-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/categories/edit?id=" + category.getId();
            }
            categoryDAO.updateCategory(category);
            logger.info("Categoría con ID {} actualizada con éxito.", category.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar la categoría con ID {}: {}", category.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.category-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/categories"; // Redirigir a la lista de categorías
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando categoría con ID {}", id);
        try {
            categoryDAO.deleteCategory(id);
            logger.info("Categoría con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la categoría con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la categoría.");
        }
        return "redirect:/categories"; // Redirigir a la lista de categorías
    }
}
