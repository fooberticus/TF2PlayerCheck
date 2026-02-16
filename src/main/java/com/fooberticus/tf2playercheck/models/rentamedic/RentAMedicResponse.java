package com.fooberticus.tf2playercheck.models.rentamedic;

import lombok.Data;

import java.util.List;

@Data
public class RentAMedicResponse {
    private List<RentAMedicResult> results;
}
