package com.sunrisedental.rest;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.dto.AppointmentRequestDTO;
import com.sunrisedental.dto.AppointmentResponseDTO;
import com.sunrisedental.dto.ErrorResponseDTO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.TreatmentType;
import com.sunrisedental.util.AppointmentNumberGenerator;
import com.sunrisedental.util.ValidationUtil;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;


@Path("/appointments")
public class AppointmentResource {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAll() {
        List<AppointmentResponseDTO> result = appointmentDAO.findAll()
                .stream()
                .map(AppointmentResponseDTO::fromModel)
                .collect(Collectors.toList());
        return Response.ok(result).build();
    }

    @GET
    @Path("/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByNumber(@PathParam("number") String number) {
        if (!ValidationUtil.isValidAppointmentNumber(number)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO("Invalid appointment number format."))
                    .build();
        }

        Appointment appt = appointmentDAO.findByNumber(number);
        if (appt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO("No appointment found for number " + number))
                    .build();
        }
        return Response.ok(AppointmentResponseDTO.fromModel(appt)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(AppointmentRequestDTO req) {
        StringBuilder errors = new StringBuilder();

        if (!ValidationUtil.isValidName(req.getPatientName())) {
            errors.append("Patient name must contain only letters and be 2-100 characters. ");
        }
        if (!ValidationUtil.isNotEmpty(req.getAddress())) {
            errors.append("Address is required. ");
        }
        if (!ValidationUtil.isValidPhone(req.getContactNumber())) {
            errors.append("Contact number must be a valid 10-digit number starting with 0. ");
        }
        if (!ValidationUtil.isNotEmpty(req.getAppointmentDate()) || !ValidationUtil.isNotEmpty(req.getAppointmentTime())) {
            errors.append("Appointment date and time are required. ");
        }
        if (req.getTreatmentIds() == null || req.getTreatmentIds().isEmpty()) {
            errors.append("At least one treatmentId is required. ");
        }

        if (errors.length() > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO(errors.toString().trim()))
                    .build();
        }

        try {
            List<TreatmentType> allTreatments = treatmentDAO.findAll();
            List<TreatmentType> selected = new java.util.ArrayList<>();
            for (Integer id : req.getTreatmentIds()) {
                for (TreatmentType option : allTreatments) {
                    if (option.getTreatmentId() == id) {
                        selected.add(option);
                        break;
                    }
                }
            }
            if (selected.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponseDTO("No valid treatmentIds matched."))
                        .build();
            }

            Appointment appt = new Appointment();
            appt.setAppointmentNumber(AppointmentNumberGenerator.generate());
            appt.setPatientName(req.getPatientName().trim());
            appt.setAddress(req.getAddress().trim());
            appt.setContactNumber(req.getContactNumber().trim());
            appt.setDentistId(req.getDentistId());
            appt.setTreatments(selected);
            appt.setAppointmentDate(Date.valueOf(req.getAppointmentDate()));
            appt.setAppointmentTime(Time.valueOf(req.getAppointmentTime() + ":00"));
            appt.setStatus("SCHEDULED");

            boolean saved = appointmentDAO.save(appt);
            if (!saved) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponseDTO("Failed to save appointment."))
                        .build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity(AppointmentResponseDTO.fromModel(appt))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO("Invalid date, time, or numeric field format."))
                    .build();
        }
    }
}
