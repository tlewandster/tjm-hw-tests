package pl.tlewand.task3.reservation;

public interface EventPublisher {

    void publish(String eventType, String details);
}