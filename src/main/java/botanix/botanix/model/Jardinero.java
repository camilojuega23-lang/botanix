package botanix.botanix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "jardinero")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Jardinero {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id_jardinero;

    @NotBlank( message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank( message = "El docuemnto es obligatorio")
    private String documento;

    @NotBlank( message = "la direccion es obligatorio")
    private String direccion;

    @NotBlank( message = "El rol es obligatorio")
    private String rol;

}
