package pe.sccu.selector;

public class ElementsNotFoundException extends RuntimeException {
    private final String path;

    public ElementsNotFoundException(String jpath) {
        this(jpath, null);
    }

    public ElementsNotFoundException(String jpath, Throwable e) {
        super(e);
        this.path = jpath;
    }

    @Override
    public String getMessage() {
        return "Elements not Found in \"" + path + "\"";
    }
}
