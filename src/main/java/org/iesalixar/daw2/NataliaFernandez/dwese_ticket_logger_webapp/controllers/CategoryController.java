package org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.dao.CategoryDAO;
import org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.entity.Category;
import org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    //Inyecta automáticamente una instancia de la clase FileStorageService
    @Autowired
    private FileStorageService fileStorageService;

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
        return "category"; // Nombre de la vista que muestra la lista de categorías
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("category", new Category()); // Crear un nuevo objeto categoría
        List<Category> listCategories = categoryDAO.listAllCategories(); // Obtener todas las categorías
        model.addAttribute("listCategories", listCategories); // Agregar la lista de categorías al modelo
        return "category-form"; // Nombre de la vista del formulario
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
        List<Category> listCategories = categoryDAO.listAllCategories(); // Obtener todas las categorías
        model.addAttribute("listCategories", listCategories); // Agregar la lista de categorías al modelo
        return "category-form"; // Nombre de la vista del formulario
    }

    @PostMapping("/insert") // para Se ha modificado el insert agragando otro RequestParam que espera el parametro imageFile con
     //con el MultiparFile es una clase de Spring que nos permite acceder al contenido del archivo.
    public String insertCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 @RequestParam(value = "parentCategory", required = false) Integer parentCategoryId,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {

        logger.info("Insertando nueva categoría con nombre: {}", category.getName());

        // Si hay errores de validación, regresa al formulario
        if (result.hasErrors()) {
            List<Category> listCategories = categoryDAO.listAllCategories();
            model.addAttribute("listCategories", listCategories);
            return "category-form";
        }

        try {
            // Maneja la relación reflexiva con la categoría padre
            if (parentCategoryId != null && parentCategoryId > 0) {
                Category parentCategory = categoryDAO.getCategoryById(parentCategoryId);
                if (parentCategory != null) {
                    category.setParentCategory(parentCategory);
                }
            }

            // Verificar si la categoría ya existe por nombre
            if (categoryDAO.existsCategoryByName(category.getName())) {
                logger.warn("El nombre de la categoría '{}' ya existe.", category.getName());
                String errorMessage = messageSource.getMessage("msg.category-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/categories/new";
            }

            // Procesa la carga de imagen si existe
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    category.setImage(fileName);
                }
            }

            // Llama al DAO para insertar la categoría
            categoryDAO.insertCategory(category);
            logger.info("Categoría '{}' insertada con éxito.", category.getName());
        } catch (Exception e) {
            logger.error("Error al insertar la categoría '{}': {}", category.getName(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.category-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/categories/new";
        }

        return "redirect:/categories";
    }



    @PostMapping("/update")
    public String updateCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes, Locale locale, Model model) {

        logger.info("Actualizando categoría con ID {}", category.getId());

        // Validar si hay errores en el formulario
        if (result.hasErrors()) {
            logger.warn("Errores de validación encontrados: {}", result.getAllErrors());
            List<Category> listCategories = categoryDAO.listAllCategories();
            model.addAttribute("listCategories", listCategories);
            return "category-form"; // Regresa al formulario con los errores
        }

        try {
            // Verificar si el nombre ya existe para otra categoría
            if (categoryDAO.existsCategoryByNameAndNotId(category.getName(), category.getId())) {
                logger.warn("El nombre de la categoría '{}' ya existe para otra categoría.", category.getName());
                String errorMessage = messageSource.getMessage("msg.category-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/categories/edit?id=" + category.getId();
            }

            // Procesar la carga de la imagen
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    category.setImage(fileName); // Asignar el nombre de la imagen
                }
            }

            // Actualizar la categoría
            categoryDAO.updateCategory(category);
            logger.info("Categoría con ID {} actualizada con éxito.", category.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Categoría actualizada con éxito.");
        } catch (Exception e) {
            logger.error("Error al actualizar la categoría con ID {}: {}", category.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.category-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/categories/edit?id=" + category.getId();
        }

        return "redirect:/categories";
    }


    @PostMapping("/delete") // Método para eliminar una categoría
    public String deleteCategory(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando categoría con ID {}", id);
        try {
            Category category = categoryDAO.getCategoryById(id);
            categoryDAO.deleteCategory(id);

            // Eliminar la imagen asociada, si existe
            if (category != null && category.getImage() != null && !category.getImage().isEmpty()) {
                fileStorageService.deleteFile(category.getImage());
            }

            logger.info("Categoría con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la categoría con ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la categoría.");
        }
        return "redirect:/categories"; // Redirigir a la lista de categorías
    }
}
