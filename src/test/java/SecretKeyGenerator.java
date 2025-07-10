import java.security.SecureRandom;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?/";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 64; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        System.out.println(sb.toString());
    }
}
