package com.example.bami.city.dto;

import lombok.Data;

@Data
public class AIRequestDTO {
    private int residence_sgg_cd;
    private String gender;
    private int age_grp;
    private int travel_num;
    private int travel_motive_1;
    private String mvmn_nm;
    private int companion_age_grp;
    private int rel_cd;

//    private int RESIDENCE_SGG_CD; //residence_sgg_cd
//    private String GENDER; //gender
//    private int AGE_GRP; //age_grp
//    private int TRAVEL_NUM; //travel_num
//    private int TRAVEL_MOTIVE_1; //travel_motive_1
//    private String MVMN_NM; //mvmn_nm
//    private int COMPANION_AGE_GRP; //companion_age_grp
//    private int REL_CD; //rel_cd
}
