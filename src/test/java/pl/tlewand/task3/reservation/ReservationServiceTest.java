package pl.tlewand.task3.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService - advanced mocking")
public class ReservationServiceTest {
    String organizerEmail = "jas.kunefal@beka.pl";
    int attendees = 5;
    String generatedCode = "RES-A1B2C3D4";
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

    private void givenStubsSetup() {

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
        @DisplayName("Should save reservation with correct information")
        void shouldSaveReservationWithCorrectInformation() {
            //given
            givenStubsSetup();

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
        @DisplayName("Should send email with correct content")
        void shouldSendEmailWithCorrectContent() {
            //given
            givenStubsSetup();

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
        @DisplayName("Should publish event with reservation details")
        void shouldPublishEventWithReservationDetails() {
            //given
            givenStubsSetup();

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

    @Nested
    @DisplayName("InOrder - Verification of operations order")
    class VerificationOfOperationsOrder {

        @Test
        @DisplayName("Should perform operations in correct order")
        void shouldPerformOperationsInCorrectOrder() {
            //given
            givenStubsSetup();

            //when
            reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);

            //then
            InOrder order = inOrder(
                    roomRepository,
                    reservationRepository,
                    codeGenerator,
                    emailService,
                    eventPublisher
            );

            order.verify(roomRepository).findById(any());
            order.verify(reservationRepository).existsByRoomIdAndTimeSlot(any(), any());
            order.verify(codeGenerator).generate();
            order.verify(reservationRepository).save(any());
            order.verify(emailService).sendConfirmation(any());
            order.verify(eventPublisher).publish(any(), any());
        }

    }

    @Nested
    @DisplayName("thenAnswer - dynamic generation of results")
    class DynamicGenerationOfResults {

        @Test
        @DisplayName("Should generate unique codes for each subsequent reservation")
        void shouldGenerateUniqueCodesForEachSubsequentReservation() {
            //given
            int[] counter = {0};
            when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));
            when(codeGenerator.generate()).thenAnswer(inv -> {
                counter[0]++;
                return "RES-0000000" + counter[0];
            });
            when(reservationRepository.save(any(Reservation.class)))
                    .thenAnswer(inv -> {
                        Reservation reservation = inv.getArgument(0);
                        reservation.setId(1L);
                        return reservation;
                    });

            //when
            reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);
            reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);
            reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees);

            //then
            verify(reservationRepository,times(3)).save(reservationCaptor.capture());
            List<Reservation> saved = reservationCaptor.getAllValues();

            assertThat(saved)
                    .extracting(Reservation::getConfirmationCode)
                    .containsExactly(
                            "RES-00000001",
                            "RES-00000002",
                            "RES-00000003"
                    );
        }
    }

    @Nested
    @DisplayName("Validation - Error Testing")
    class ErrorTesting {
        @Test
        @DisplayName("Should throw exception when room not exist")
        void shouldThrowExceptionWhenRoomNotExist() {
            //given
            when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.empty());

            //when
            Throwable thrown = catchThrowable(() -> reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, attendees));

            //then
            assertThat(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(room.getRoomId());
            verifyNoInteractions(
                    reservationRepository,
                    codeGenerator,
                    emailService,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("Should throw exception when there are too many participants")
        void shouldThrowExceptionWhenThereAreTooManyParticipants() {
            //given
            when(roomRepository.findById(room.getRoomId())).thenReturn(Optional.of(room));
            int tooManyAttendees = room.getCapacity()+1;

            //when
            Throwable exception = catchThrowable(() -> reservationService.createReservation(room.getRoomId(), organizerEmail, timeSlot, tooManyAttendees));

            //then
            assertThat(exception)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContainingAll(
                            room.getName(),
                            String.valueOf(room.getCapacity()),
                            String.valueOf(tooManyAttendees)
                    );
            verifyNoInteractions(
                    reservationRepository,
                    codeGenerator,
                    emailService,
                    eventPublisher
            );
        }
    }


}
