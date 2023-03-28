package com.ms.tickettacheservice.model;

import lombok.*;

import java.util.Date;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Sprint {
    private Long id;
    private Date dateLancement;
    private Date dateFin;
    private String objectif;
    private int velocite;
    private String etat;
}
