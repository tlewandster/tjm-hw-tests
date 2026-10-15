package pl.tlewand.task3.reservation;

import java.util.Optional;

public interface RoomRepository {

    Optional<Room> findById(String roomId);
}