package fr.univlyon1.tiw1.framework.vue;

public class ResponseEntity<T> {

    private T body;

    int status = 200;

    public ResponseEntity(T body) {
        this.body = body;
    }

    private ResponseEntity() {
        this.body = null;
    }

    public ResponseEntity(T body, int status) {
        this.body = body;
        this.status = status;
    }

    public static ResponseEntity<?> empty(int status) {
        ResponseEntity<?> response = new ResponseEntity<>();
        response.status = status;
        return response;
    }

    public int getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }
}
