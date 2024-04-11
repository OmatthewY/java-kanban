package exceptions;
import java.io.IOException;
public class VerificationException extends IOException {
    public VerificationException() {};

    public VerificationException(String message) {
        super.getMessage();
    }
}
