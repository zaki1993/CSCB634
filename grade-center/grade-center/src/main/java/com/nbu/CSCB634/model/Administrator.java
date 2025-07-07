package com.nbu.CSCB634.model;

import com.nbu.CSCB634.model.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "administrators")
public class Administrator {
    @Id
    private Long id;  // Same as User.id

    @OneToOne(cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
