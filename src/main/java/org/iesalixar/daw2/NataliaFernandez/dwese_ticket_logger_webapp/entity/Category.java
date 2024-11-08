package org.iesalixar.daw2.NataliaFernandez.dwese_ticket_logger_webapp.entity;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "categories") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "{msg.category.name.notEmpty}")
    @Size(max = 100, message = "{msg.category.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "image", nullable =true )/*puede dejar este campo vacio*/
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)  /*relacion muchos a uno donde LAZY indica que los datos
     no se cargan de la base de datos hasta que se accede específicamente al campo */
    @JoinColumn(name = "parent_id", nullable = true)
    private Category parentCategory;



    /*Relacion uno a muchos , la anotacion mapea para
     la categoria padre y cascade indica que las operaciones CRUD se extienden a las subcategorias*/
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Category> subCategories;


    public Integer getParentCategoryId() {
        return (parentCategory != null) ? parentCategory.getId() : null;
    }
    /**
     * Constructor que excluye el campo `id`. Se utiliza para crear instancias de `Category`
     * cuando el `id` aún no se ha generado (por ejemplo, antes de insertarla en la base de datos).
     */
    public Category(String name, String image, Category parentCategory) {
        this.name = name;
        this.image = image;
        this.parentCategory = parentCategory;
    }
}
