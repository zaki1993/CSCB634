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
@Table(name = "directors")
public class Director {
    @Id
    private Long id;  // Same as User.id

    @OneToOne(cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // You can add director-specific fields here
}