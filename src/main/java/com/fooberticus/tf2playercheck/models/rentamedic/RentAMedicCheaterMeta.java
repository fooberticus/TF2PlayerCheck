package com.fooberticus.tf2playercheck.models.rentamedic;

import lombok.Data;

import java.util.List;

@Data
public class RentAMedicCheaterMeta {
    private String steamName;
    private String steamAvatar;
    private String seenFirstDate;
    private String seenLastDate;
    List<String> maps;
}
