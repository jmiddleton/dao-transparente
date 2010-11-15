package ar.com.tunuyan.dao;

public class GenericDaoException extends Exception {

    private static final long serialVersionUID = 6569209771104397671L;

    public GenericDaoException() {
        super();
    }

    public GenericDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericDaoException(String message) {
        super(message);
    }

    public GenericDaoException(Throwable cause) {
        super(cause);
    }
}
