# Test Plan - Sunrise Dental Clinic Management System

## 1. Test Rationale

The system separates business/validation logic (`ValidationUtil`, `PasswordUtil`,
`Bill`) from persistence logic (DAOs) and HTTP handling (Servlets), following
the MVC + DAO architecture described in Task B. This separation was chosen
partly *because* it makes unit testing possible: validation and calculation
rules can be tested in complete isolation, with no database, no servlet
container, and no network - fast, deterministic tests that run in
milliseconds and don't depend on external state.

DAO and Servlet classes are **not** unit tested directly, since they require
a live MySQL connection and a servlet container respectively. These are
covered instead by the manual integration test cases in Section 4, which
exercise the full stack (browser -> Servlet -> DAO -> MySQL) end to end.

## 2. Test-Driven Development (TDD) Approach

For the three business-logic classes, tests were written before or alongside
the implementation, following the red-green-refactor cycle:

1. **Red** - write a test describing the required behaviour (e.g.
   "a valid Sri Lankan phone number is exactly 10 digits starting with 0")
   before the corresponding regex/logic exists, so the test fails.
2. **Green** - implement or adjust the minimum code in `ValidationUtil` /
   `PasswordUtil` / `Bill` needed to make the test pass.
3. **Refactor** - clean up the implementation once tests pass, re-running
   tests after each change to confirm nothing broke.

This approach surfaced two concrete design decisions during development:

- **Phone format**: an initial version of the regex accepted a leading `+`
  (international format). A test for `"+94712345678"` was added to decide
  behaviour explicitly - it was decided to **reject** it, since the clinic
  only stores local 10-digit numbers, and register-appointment.jsp only
  provides a local input mask. This assumption is documented here and
  should be re-confirmed with the client (Sunrise Dental Clinic) if
  international patients need to be registered.
- **Appointment number case sensitivity**: a test asserting `"apt1234"`
  (lowercase) should be rejected drove the decision to keep
  `generateAppointmentNumber()` always producing uppercase output.

## 3. Automated Unit Tests (JUnit 5)

Location: `src/test/java/com/sunrisedental/`

| Test class | Class under test | What it verifies |
|---|---|---|
| `ValidationUtilTest` | `ValidationUtil` | Name format, phone format, non-empty checks, appointment number format - valid and invalid cases, including null inputs |
| `PasswordUtilTest` | `PasswordUtil` | Hashing is deterministic, produces a 64-char hex SHA-256 digest, differs for different inputs, and never equals the plaintext |
| `BillTest` | `Bill` | Total = treatment fee + consultation fee, across multiple treatment types; appointment number is stored correctly; documents a known gap (no negative-fee validation yet) |

### 3.1 Test data used

| Input type | Valid examples | Invalid examples |
|---|---|---|
| Patient name | `Nimal Perera`, `A. B. Silva` | `""`, `"N"`, `"Nimal123"`, `"N@me"` |
| Phone number | `0712345678`, `0771234567` | `712345678` (no leading 0), `07123456789` (too long), `+94712345678` |
| Appointment number | `APT1234`, `APT12345678` | `apt1234` (lowercase), `APT12` (too short), `""` |
| Treatment fee + consultation fee | `3500.00 + 1000.00 = 4500.00`, `12000.00 + 1000.00 = 13000.00` | (negative fee documented as known gap, not yet rejected) |

### 3.2 Running the tests

```bash
mvn test
```

Maven's Surefire plugin will discover and run all `*Test.java` classes and
report pass/fail counts in the console and in `target/surefire-reports/`.

## 4. Manual Integration Test Cases (full stack)

These cover the parts that automated unit tests deliberately don't reach
(login session handling, DB writes, role-based access control).

| # | Test case | Steps | Expected result |
|---|---|---|---|
| IT-1 | Successful login | Go to `/login`, enter `admin` / `admin123` | Redirected to `/dashboard.jsp`, session created |
| IT-2 | Failed login | Enter `admin` / `wrongpassword` | Error message shown, no session created |
| IT-3 | Register appointment (valid data) | Log in as admin, fill in the register form with valid data, submit | Appointment saved, success message with generated appointment number shown |
| IT-4 | Register appointment (invalid phone) | Submit with phone `12345` | Form re-displayed with phone validation error, no DB row inserted |
| IT-5 | Search appointment (found) | Search using a valid appointment number from IT-3 | Full appointment details displayed |
| IT-6 | Search appointment (not found) | Search using `APT99999999` | "No appointment found" message shown |
| IT-7 | Generate bill | Enter a valid appointment number on the billing page | Bill displayed with correct treatment fee + Rs. 1000 consultation fee = total; row inserted into `bills` table |
| IT-8 | Role restriction (Dentist) | Log in as a Dentist-role user, attempt to access `/register-appointment` directly via URL | HTTP 403 Forbidden returned |
| IT-9 | Logout | Click Logout | Session invalidated, redirected to login, back button does not restore dashboard |

## 5. Evaluation - Success, Failure, and Lessons Learned

**What worked well:**
- Isolating validation and calculation logic from persistence made those
  rules fully unit-testable and fast to iterate on during development.
- The alt/else branches modelled in the Task A sequence diagrams map
  directly onto the alt/else conditions actually tested here (IT-2, IT-4),
  showing traceability between design and test coverage.

**What didn't work / limitations identified:**
- `Bill` does not currently validate against negative fees (see
  `BillTest.bill_rejectsNegativeTotalsAsInvalid` - the name is a slight
  misnomer left deliberately as a note-to-self; it currently documents
  the gap rather than proving prevention). This should be fixed before
  a production deployment by adding a check in the `Bill` constructor.
- DAO and Servlet classes have no automated tests. A future iteration
  could introduce Mockito to mock `Connection`/`PreparedStatement` and
  unit test DAO SQL construction without a live database - this was
  scoped out here in favour of the simpler manual integration tests
  in Section 4, given the coursework's time constraints.

**Traceability - requirement to test case:**

| Assessment brief requirement | Covered by |
|---|---|
| User Authentication (Login) | IT-1, IT-2 |
| Register New Appointment | IT-3, IT-4, `ValidationUtilTest` |
| Display Appointment Details | IT-5, IT-6 |
| Calculate and Print Bill | IT-7, `BillTest` |
| Role-based access (assumption from Task A) | IT-8 |
| Exit System | IT-9 |
