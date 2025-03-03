package com.footoff.api.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDTO {
    
    @Getter @Setter
    public static class Request {
        private String email;
        private String password;
        private String nickname;
        private String profileImage;
    }
    
    @Getter @Setter
    public static class Response {
        private Long id;
        private String email;
        private String nickname;
        private String profileImage;
        private boolean isActive;
        private String role;
    }
    
    @Getter @Setter
    public static class UpdateRequest {
        private String nickname;
        private String profileImage;
    }
} 