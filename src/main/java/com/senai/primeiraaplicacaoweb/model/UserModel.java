package com.senai.primeiraaplicacaoweb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    @Column(length = 100)
    private String name;

    @NotBlank(message = "Informe o email")
    @Email(message = "Email inválido")
    @Column
    private String email;

    @NotBlank(message = "Informe o telefone")
    @Pattern(regexp = "^[0-9()\\s+-]{8,20}$", message = "Telefone inválido")
    @Column(length = 20)
    private String tel;

    @NotNull(message = "Informe a data de nascimento")
    @Past(message = "A data de nascimento precisa ser no passado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(columnDefinition = "date")
    private LocalDate date;
}
