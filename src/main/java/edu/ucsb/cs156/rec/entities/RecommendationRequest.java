package edu.ucsb.cs156.rec.entities; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "recommendationrequest")
public class RecommendationRequest {
    private long requesterId; // assigned automatically in backend on form submission
    private long professorId; // assigned by student in dropdown
    private String requestType; // form
    private String details; // form
    private LocalDateTime neededByDate; // form 
    private LocalDateTime submissionDate; // assigned automatically in backend on form submission
    private LocalDateTime completionDate; // assigned later by professor
    private String status; // assigned automatically in backend as "Pending", then changed to "Completed" or "Rejected" by professor

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}