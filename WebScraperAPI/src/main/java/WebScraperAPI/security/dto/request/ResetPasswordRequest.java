package WebScraperAPI.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    @Pattern(regexp = "^\\S{6,40}$", message = "La contraseña no debe contener espacios y debe tener entre 6 y 40 caracteres.")
    private String newPassword;

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
