package com.kampuskart.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role;
    private String type;
    private String sellerType;
    private String campus;
    private String phone;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSellerType() { return sellerType; }
    public void setSellerType(String sellerType) { this.sellerType = sellerType; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String resolveRole() {
        String t = type != null ? type : role;
        if (t == null) return "buyer";
        return switch (t.toLowerCase()) {
            case "individual", "seller" -> "seller";
            case "shop" -> "shop";
            case "admin" -> "buyer";
            default -> "buyer";
        };
    }
}
