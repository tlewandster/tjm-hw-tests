package pl.tlewand.task3.reservation;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
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

    @Captor
    private ArgumentCaptor<ConfirmationEmail> emailCaptor;

    @Captor
    private ArgumentCaptor<String> publishCaptor;

    private Room room;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        room = new Room("room-01", "Best Room", 20, false);
        timeSlot = new TimeSlot(LocalDate.of(2026, 10, 14),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0));
    }

    private void givenStubsSetup(String generatedCode) {

        when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));
        when(codeGenerator.generate()).thenReturn(generatedCode);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation reservation = inv.getArgument(0);
                    reservation.setId(1L);
                    return reservation;
                });
    }

    @Nested
    @DisplayName("ArgumentCaptor - verification of stored data")
    class VerificationOfStoredData {

        @Test
        void shouldSaveReservationWithCorrectInformation() {
            //given
            String organizerEmail = "jas.kunefal@beka.pl";
            int attendees = 5;
            String generatedCode = "RES-A1B2C3D4";
            givenStubsSetup(generatedCode);

            //when
            Reservation reservation = reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);

            //then
            verify(reservationRepository).save(reservationCaptor.capture());
            Reservation saved = reservationCaptor.getValue();

            assertThat(saved.getRoomId()).isEqualTo(room.getRoomId());
            assertThat(saved.getOrganizerEmail()).isEqualTo(organizerEmail);
            assertThat(saved.getTimeSlot()).isEqualTo(timeSlot);
            assertThat(saved.getAttendees()).isEqualTo(attendees);
            assertThat(saved.getConfirmationCode()).isEqualTo(generatedCode);
            assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
            assertThat(reservation.getId()).isEqualTo(1L);
        }

        @Test
        void shouldSendEmailWithCorrectContent() {
            //given
            String organizerEmail = "jas.kunefal@beka.pl";
            int attendees = 5;
            String generatedCode = "RES-A1B2C3D4";
            givenStubsSetup(generatedCode);

            //when
            reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);

            //then
            verify(emailService).sendConfirmation(emailCaptor.capture());
            ConfirmationEmail email = emailCaptor.getValue();

            assertThat(email.getRecipientEmail()).isEqualTo(organizerEmail);
            assertThat(email.getSubject()).isEqualTo("Potwierdzenie rezerwacji sali " + room.getName());
            assertThat(email.getBody()).contains(
                    room.getName(),
                    timeSlot.getDate().toString(),
                    timeSlot.getStartTime().toString(),
                    timeSlot.getEndTime().toString(),
                    generatedCode
            );
        }

        @Test
        void shouldPublishEventWithReservationDetails() {
            //given
            String organizerEmail = "jas.kunefal@beka.pl";
            int attendees = 5;
            String generatedCode = "RES-A1B2C3D4";
            givenStubsSetup(generatedCode);

            //when
            reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);

            //then
            verify(eventPublisher).publish(eq("RESERVATION_CREATED"), publishCaptor.capture());
            String details = publishCaptor.getValue();
            assertThat(details).contains(
                    generatedCode,
                    room.getName()
            );

        }
    }
}
