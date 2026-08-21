# Sunrise Dental Clinic - Appointment & Patient Management System

CIS6003 coursework project. A Java web application (Servlets + JSP + MySQL)
with a REST web service layer (Jersey/JAX-RS), built to the assignment
scenario: computerised replacement for Sunrise Dental Clinic's manual,
paper-based appointment and billing process.

## Features (mapped to the assignment brief)

| Brief requirement | Implementation |
|---|---|
| User Authentication | `LoginServlet`, `UserDAO`, SHA-256 hashing via `PasswordUtil` |
| Register New Appointment | `RegisterAppointmentServlet` + `register-appointment.jsp` |
| Display Appointment Details | `SearchAppointmentServlet` + `search-appointment.jsp` |
| Calculate and Print Bill | `BillingServlet` + `billing.jsp` |
| Help Section | `help.jsp` |
| Exit System | `LogoutServlet` |
| Distributed application / web services (Task B.i) | REST API under `/api/*` (Jersey/JAX-RS), see below |
| Design patterns (Task B.ii) | DAO (persistence), Singleton (`DBConnectionManager`), DTO (`rest`/`dto` packages), MVC overall |
| Database (Task B.iii) | MySQL, schema in `sql/schema.sql` |

## Prerequisites

- JDK 11+
- Maven 3.6+
- MySQL 8.x
- A Servlet container (Tomcat 10+, since this uses the `jakarta.*` namespace)

## Setup

1. Create the database:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```
2. Update credentials in `src/main/java/com/sunrisedental/util/DBConnectionManager.java`
   if your MySQL user/password differs from `root`/`root`.
3. Build:
   ```bash
   mvn clean package
   ```
4. Deploy `target/sunrise-dental.war` to Tomcat, then visit:
   `http://localhost:8080/sunrise-dental/login.jsp`

## REST API (Task B.i)

Base path: `/sunrise-dental/api`

| Method | Path | Description |
|---|---|---|
| GET | `/api/appointments` | List all appointments |
| GET | `/api/appointments/{number}` | Get one appointment by appointment number |
| POST | `/api/appointments` | Register a new appointment (JSON body) |
| GET | `/api/bills/{appointmentNumber}` | Calculate and save the bill for an appointment |

Example:
```bash
curl http://localhost:8080/sunrise-dental/api/appointments/APT1A2B3C4D

curl -X POST http://localhost:8080/sunrise-dental/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientName":"Nimal Perera","address":"12 Galle Rd, Colombo","contactNumber":"0712345678","dentistId":1,"treatmentId":1,"appointmentDate":"2026-08-01","appointmentTime":"10:30"}'
```

## Running tests (Task C)

```bash
mvn test
```

See `TEST_PLAN.md` for test rationale, TDD approach, test data, and manual
integration test cases.

## UML diagrams (Task A)

See `uml/` - use case diagram, class diagram, and sequence diagrams for
Login, Register Appointment, Search Appointment, and Billing. Source files
(`.puml`) can be regenerated with `plantuml *.puml`.

## Design assumptions

- Three staff roles assumed: Admin, Receptionist, Dentist (not specified in
  the scenario, but implied by "only authorized staff can use the system" -
  documented as an assumption per the brief's instruction that additional
  design decisions must be explained).
- Consultation fee is a fixed Rs. 1000 added to the treatment-specific fee.
- Appointment numbers are system-generated (`APT` + 8 hex characters), not
  entered manually, to guarantee uniqueness.
