import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    private String success;
    private User user;
    private String accessToken;
    private String refreshToken;
}
