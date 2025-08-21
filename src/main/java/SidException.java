public class SidException extends Exception {
    public SidException(String message) {
        super(message);
        Sid.SpecialPrint(message);
    }
}
