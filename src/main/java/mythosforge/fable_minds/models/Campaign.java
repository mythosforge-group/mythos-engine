package mythosforge.fable_minds.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
    public class Campaign {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User owner;

        @Column
        private String description;

        @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
        private List<Character> characters = new ArrayList<>();
    }


