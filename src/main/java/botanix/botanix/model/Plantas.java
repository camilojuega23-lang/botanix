package botanix.botanix.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "plantas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Plantas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_plantas;

    @NotBlank( message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank( message = "El nomnbre es obligatorio")
    private String nombre;

    @NotBlank( message = "El nomnbre cientifico es obligatorio")
    private String nombre_cientifico;

    @NotBlank( message = "La especie es obligatorio")
    private String especie;

    @NotNull( message = "La cantidad es obligatorio")
    private Integer cantidad;

    @NotNull( message = "El precio es obligatorio")
    private Integer precio;


}
