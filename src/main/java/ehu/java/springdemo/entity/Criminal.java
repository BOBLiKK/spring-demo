package ehu.java.springdemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "criminals")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Criminal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private String reasonForWanted;

    @Column(nullable = false)
    private Double reward;

    @Lob
    @Column(nullable = true)
    private byte[] photo;

    @Column(nullable = true)
    private String comment;

    public Criminal(String firstName, String lastName, LocalDate dateOfBirth, String nationality, String reasonForWanted, Double reward, byte[] photo, String comment) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.reasonForWanted = reasonForWanted;
        this.reward = reward;
        this.photo = photo;
        this.comment = comment;
    }
}
