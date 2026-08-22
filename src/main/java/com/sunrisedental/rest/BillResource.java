package com.sunrisedental.rest;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dto.ErrorResponseDTO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.util.ValidationUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/bills")
public class BillResource {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();

    @GET
    @Path("/{appointmentNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateBill(@PathParam("appointmentNumber") String appointmentNumber) {
        if (!ValidationUtil.isValidAppointmentNumber(appointmentNumber)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponseDTO("Invalid appointment number format."))
                    .build();
        }

        Appointment appt = appointmentDAO.findByNumber(appointmentNumber);
        if (appt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponseDTO("No appointment found for number " + appointmentNumber))
                    .build();
        }

        Bill bill = new Bill(appt.getAppointmentNumber(), appt.getTotalTreatmentFee(),
                BillDAO.getStandardConsultationFee());
        billDAO.save(bill);

        return Response.ok(bill).build();
    }
}
