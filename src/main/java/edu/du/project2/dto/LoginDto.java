package edu.du.project2.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginDto {
    private String loginId;
    private String password;
}
