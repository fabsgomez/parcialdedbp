package com.example.demo.tickettype.domain;

import com.example.demo.campusevent.domain.CampusEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private CampusEvent event;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(nullable = false)
    private Integer registeredCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketTypeStatus status;

    public boolean hasAvailableSlots() {
        return status == TicketTypeStatus.AVAILABLE && registeredCount < capacity;
    }

    public void incrementRegistration() {
        registeredCount = registeredCount + 1;
        if (registeredCount >= capacity) {
            status = TicketTypeStatus.FULL;
        }
    }
}
