package name.sccu.jpath;

public class NodesNotFoundException extends RuntimeException {
    private final String path;

    public NodesNotFoundException(String jpath) {
        this(jpath, null);
    }

    public NodesNotFoundException(String jpath, Throwable e) {
        super(e);
        this.path = jpath;
    }

    @Override
    public String getMessage() {
        return "Elements not Found in \"" + path + "\"";
    }
}
