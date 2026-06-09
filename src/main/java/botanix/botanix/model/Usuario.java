package botanix.botanix.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table (name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @Email(message = "Debe ser un email valido")
    @NotBlank(message = "El correo es obligatorio")
    @Column (unique = true)
    private String correo;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}
