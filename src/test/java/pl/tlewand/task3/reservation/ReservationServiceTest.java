package pl.tlewand.task3.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService - advanced mocking")
public class ReservationServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationEmailService emailService;

    @Mock
    private ConfirmationCodeGenerator codeGenerator;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ReservationService reservationService;

    @Captor
    private ArgumentCaptor<Reservation> reservationCaptor;

    @Nested
    @DisplayName("ArgumentCaptor - verification of stored data")
    class VerificationOfStoredData {
        @Test
        void shouldSaveReservationWithCorrectInformation() {
            //given
            String roomId = "room-01";
            String organizerEmail = "jas.kunefal@beka.pl";
            TimeSlot timeSlot = new TimeSlot(LocalDate.of(2026, 10, 14),
                    LocalTime.of(10, 0),
                    LocalTime.of(16, 0));
            int attendees = 5;

            when(roomRepository.findById(roomId)).thenReturn(Optional.of(new Room(roomId, "Best Room", 20, false)));
            when(codeGenerator.generate()).thenReturn("RES-A1B2C3D4");
            when(reservationRepository.save(any(Reservation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            //when
            Reservation reservation = reservationService.createReservation(roomId, organizerEmail, timeSlot, attendees);
            verify(reservationRepository).save(reservationCaptor.capture());

            //then
            Reservation expected = reservationCaptor.getValue();
            assertThat(reservation)
                    .usingRecursiveAssertion()
                    .isEqualTo(expected);
        }

    }
}
