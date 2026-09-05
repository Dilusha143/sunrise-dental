# Project Plan - Remaining Work Against the CIS6003 Brief

This organizes what's done vs. what's left, task by task.

## Task A - UML diagrams (20 marks) - ~90% done

**Update:** the diagrams below have since been revised to match the final,
completed system (this happened after this plan was first written - see
the "Diagram revision log" note at the end of this file for exactly what
changed and why).

Done:
- Use Case diagram (`uml/UseCaseDiagram.png` / `.puml`)
- Class diagram (`uml/ClassDiagram.png` / `.puml`)
- Sequence diagrams: Login, Register Appointment, Search Appointment, Billing
  (`uml/SequenceDiagrams.puml`, all four rendered to PNG)

Left to do (report writing, not code):
- [ ] Write ~400-600 words explaining each diagram's design decisions:
      why these actors/use cases, why these classes/associations, why these
      alt/else branches in the sequence diagrams.
- [ ] State assumptions explicitly (e.g. the three staff roles - see
      README.md "Design assumptions" for a starting point to expand on).

## Task B - Development (40 marks) - now complete

Done:
- Interactive UI: login, register/search appointment, billing, help (JSP)
- Validation: `ValidationUtil` (name, phone, appointment number formats)
- **(i) Distributed / web services**: REST API added this session under
  `/api/*` using Jersey (JAX-RS) - `AppointmentResource`, `BillResource`,
  DTOs. See README.md "REST API" section for endpoints.
- **(ii) Design patterns**: DAO (`dao` package), Singleton
  (`DBConnectionManager`), DTO (`dto`/`rest` packages), overall MVC via
  Servlet/JSP.
- **(iii) Database**: MySQL, `sql/schema.sql`.

Left to do:
- [ ] Run `mvn clean package` locally and confirm it builds (Maven Central
      wasn't reachable in this sandbox to verify - see note below).
- [ ] Deploy to a local Tomcat and smoke-test both the JSP pages and the
      new `/api/*` endpoints (curl examples in README.md).
- [ ] Write the report section explaining the REST layer and each design
      pattern with justification (short justifications are already in the
      Javadoc comments in the code - expand those into report prose).

## Task C - Testing (20 marks) - complete

`TEST_PLAN.md` already covers test rationale, TDD red-green-refactor
approach, test data, and manual integration test cases, with a
requirement-to-test-case traceability table. Just carry this into the
report (it's already written in report-ready prose).

## Task D - Git/GitHub (20 marks) - needs your action

A real local git repository has been created with genuine, logically
separated commits reflecting the actual build-up of the system:

```
765e0a1 Add Task B REST web service layer (Jersey/JAX-RS)...
e729389 Add Task C: JUnit tests and test plan with TDD rationale
71d58c6 Add Task A UML diagrams (use case, class, sequence diagrams)
95a011b Initial core system: login, register/search, billing
```

Left to do (this part only you can do honestly):
- [ ] Create a **public** GitHub repository.
- [ ] `git remote add origin <your-repo-url>` and `git push -u origin master`.
- [ ] From here on, **commit for real, as you actually make changes**, on
      the days you actually make them - e.g. after you finish the report
      writing, after you test the deployed WAR, after any bug fix. The
      brief wants to see genuine version history "where modifications are
      applied each day" - don't backdate or batch-fake commits, since that
      undermines exactly what Task D is assessing (your actual use of
      version control day to day).
- [ ] In your report, screenshot the commit history / GitHub network graph
      and briefly explain your branching/commit strategy, and link the
      repo.

## Word count note

The brief's 4000-word count includes source code, figures, and tables.
Between the existing code, `TEST_PLAN.md`, and the diagrams, you already
have substantial content - the main remaining writing is the explanatory
prose for Tasks A and B.

## Note on this session's limitations

This sandbox can reach `pypi.org`, `npmjs.com`, `github.com`, and Ubuntu's
package archives, but not Maven Central (`repo.maven.apache.org`), so I
could not run `mvn clean package` end-to-end here to prove the new REST
code compiles against the real Jersey jars. The code follows standard
JAX-RS 3.1 / Jakarta EE 9 patterns consistent with your existing
`jakarta.servlet` dependency version, but please run the build yourself and
let me know if you hit any compiler errors - I can fix them quickly.

## Diagram revision log (post-development update)

The system grew several features after the diagrams above were first
drawn (dentist roster management, staff registration, appointment
status workflow, status-dependent billing). The UML has been revised to
match the final system exactly - no application code was changed as
part of this revision, only `uml/*.puml` and their rendered `.png` files.

**Class diagram** (`ClassDiagram.puml`):
- `Appointment.treatmentId` (single value) replaced with
  `treatments : List<TreatmentType>`, reflecting the real many-to-many
  design (an appointment can include more than one treatment/consultation,
  via the `appointment_treatments` junction table).
- Removed a fictional `IAppointmentDAO` / `AppointmentDAOImpl` interface
  split that was never actually implemented in code - the real design is
  a single concrete `AppointmentDAO` class.
- Added `DentistDAO`, `TreatmentDAO`, `ValidationUtil`, `PasswordUtil`,
  `AppointmentNumberGenerator`, and the new controllers
  (`AppointmentsListServlet`, `UpdateAppointmentStatusServlet`,
  `RegisterStaffServlet`, `AddDentistServlet`), none of which existed
  when the diagram was first drawn.
- `User` now shows `dentistId` / `dentistSpecialization` and the
  permission methods (`isLinkedToDentist()`, `canRegisterAppointments()`,
  `canManageStaff()`) that drive role-based access control throughout
  the app.

**Use case diagram** (`UseCaseDiagram.puml`):
- Added: "View Appointments List", "Complete Appointment",
  "Cancel Appointment", "Add Dentist to Roster"; renamed "Manage Users"
  to "Register Staff Login" for accuracy.
- Documented that "Calculate Bill" now depends on appointment status
  (SCHEDULED/COMPLETED/CANCELLED), and that only a Dentist (or Admin)
  can complete an appointment, while both Dentist and Receptionist can
  cancel one.

**Sequence diagrams** (`SequenceDiagrams.puml`):
- Rewrote the Billing sequence with the three status branches
  (cancelled -> no bill, scheduled -> consultation fee only,
  completed -> full bill).
- Added a new sequence diagram, `SequenceDiagram_UpdateAppointmentStatus`,
  covering the Complete/Cancel workflow and its role checks.
- Updated the Register Appointment sequence to show the dentist/treatment
  dropdowns being loaded live from the database (previously the dentist
  list was hard-coded in the JSP - now fixed) and corrected the
  appointment-number generation step to reference the actual
  `AppointmentNumberGenerator` utility.
- Updated the Login sequence note to reflect the join against `dentists`
  that now happens as part of authentication.
