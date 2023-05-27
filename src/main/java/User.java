import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String email;
    private String password;
    private String name;

    public User(String password, String name) {
        this.password = password;
        this.name = name;
    }
}
