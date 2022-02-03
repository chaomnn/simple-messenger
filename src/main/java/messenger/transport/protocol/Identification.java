package messenger.transport.protocol;

public class Identification implements Data {
    private String id;

    public Identification(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Identification{" +
                "id='" + id + '\'' +
                '}';
    }
}