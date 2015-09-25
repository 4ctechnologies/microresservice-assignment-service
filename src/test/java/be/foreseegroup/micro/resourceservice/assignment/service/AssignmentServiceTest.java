package be.foreseegroup.micro.resourceservice.assignment.service;

import be.foreseegroup.micro.resourceservice.assignment.AssignmentServiceApplication;
import be.foreseegroup.micro.resourceservice.assignment.model.Assignment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Kaj on 25/09/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AssignmentServiceApplication.class)
@WebIntegrationTest
public class AssignmentServiceTest {

    private static final String ROOT_PATH = "http://localhost:8888";
    private static final String UNIT_PATH = "/assignments";
    private static final String UNIT_RESOURCE = ROOT_PATH + UNIT_PATH;


    private static final Assignment CONTRACT_1 = new Assignment("consultantId1", "customerId1", "startDate1", "endDate1");
    private static final Assignment CONTRACT_2 = new Assignment("consultantId2", "customerId2", "startDate2", "endDate2");
    private static final String NON_EXISTING_ID = "nonExistingId";

    @Autowired
    private AssignmentRepository repo;

    private RestTemplate restTemplate = new TestRestTemplate();


    @Before
    public void setUp() throws Exception {
        repo.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll();
    }

    /** Test case: getExistingPersonShouldReturnPerson
     *
     * Test if a GET result on an existing entry return the entry itself
     * Also, the Http response should have HttpStatus Code: OK (200)
     */

    @Test
    public void getExistingPersonShouldReturnPerson() {
        //Add the Assignment that we will try to GET request to the database
        Assignment savedAssignment = repo.save(CONTRACT_1);

        String url = UNIT_RESOURCE + "/" + savedAssignment.getId();

        //Instantiate the HTTP GET Request
        ResponseEntity<Assignment> response = restTemplate.getForEntity(url, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.OK;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());


        Assignment receivedAssignment = response.getBody();

        //Finally, match if the values of the received Assignment are valid
        assertEquals("ID of the received object is invalid", savedAssignment.getId(), receivedAssignment.getId());
        assertEquals("customerId of the received object is invalid", savedAssignment.getCustomerId(), receivedAssignment.getCustomerId());
        assertEquals("consultantId of the received object is invalid", savedAssignment.getConsultantId(), receivedAssignment.getConsultantId());
        assertEquals("startDate of the received object is invalid", savedAssignment.getStartDate(), receivedAssignment.getStartDate());
        assertEquals("endDate of the received object is invalid", savedAssignment.getEndDate(), receivedAssignment.getEndDate());

    }

    /** Test case: getUnexistingPersonShouldReturnHttpNotFoundError
     *
     * Test if a GET result on an unexisting entry return an error
     * It should not contain an object in its body
     * It should return a HttpStatus code: NOT_FOUND (404)
     */

    @Test
    public void getUnexistingPersonShouldReturnHttpNotFoundError() {
        String url = UNIT_RESOURCE + "/" + NON_EXISTING_ID;

        //Instantiate the HTTP GET Request
        ResponseEntity<Assignment> response = restTemplate.getForEntity(url, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.NOT_FOUND;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNull("Http Request response body did contain a Assignment object", response.getBody());
    }

    /** Test case: getPersonsShouldReturnAllPersons
     *
     * Test if a GET results without specifying an ID results all the entries
     * It should contain all the entries in its body
     * It should return HttpStatus code: OK (200)
     */
    @Test
    public void getPersonsShouldReturnAllPersons() {
        //Add the Assignment that we will try to GET request to the database
        Assignment savedAssignment1 = repo.save(CONTRACT_1);
        Assignment savedAssignment2 = repo.save(CONTRACT_2);

        String url = UNIT_RESOURCE;

        //Instantiate the HTTP GET Request
        ParameterizedTypeReference<Iterable<Assignment>> responseType = new ParameterizedTypeReference<Iterable<Assignment>>() {};
        ResponseEntity<Iterable<Assignment>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.OK;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());

        //Add the received entries to an ArrayList (has a .size() method to count the entries)
        ArrayList<Assignment> responseList = new ArrayList<>();
        if (response.getBody() != null) {
            for (Assignment u : response.getBody()) {
                responseList.add(u);
            }

        }

        //Check if the amount of entries is correct
        assertEquals("Response body size did not match", 2, responseList.size());
    }


    /** Test case: getAssignmentsByConsultantIdShouldReturnAssignments
     *
     * Test if a GET result based on ConsultantId returns the Assignments where the specifiec ConsultantId is involved
     * The Http response should have HttpStatus Code: OK (200)
     */

    @Test
    public void getAssignmentsByConsultantIdShouldReturnAssignments() {
        //Add the Assignments that we will try to GET request to the database;

        Assignment a = new Assignment("consultantId1","customerId1","startDate1","endDate1");
        Assignment b = new Assignment("consultantId1","customerId2","startDate2","endDate2");
        Assignment c = new Assignment("consultantId1","customerId3","startDate3","endDate3");
        Assignment d = new Assignment("consultantId4","customerId4","startDate4","endDate4");
        Assignment e = new Assignment("consultantId5","customerId5","startDate5","endDate5");

        repo.save(a);
        repo.save(b);
        repo.save(c);
        repo.save(d);
        repo.save(e);

        String url = ROOT_PATH + "/assignmentsbycid/" + a.getConsultantId();

        //Instantiate the HTTP GET Request
        ParameterizedTypeReference<Iterable<Assignment>> responseType = new ParameterizedTypeReference<Iterable<Assignment>>() {};
        ResponseEntity<Iterable<Assignment>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);


        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.OK;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());

        //Add the received entries to an ArrayList (has a .size() method to count the entries)
        ArrayList<Assignment> responseList = new ArrayList<>();
        if (response.getBody() != null) {
            for (Assignment u : response.getBody()) {
                responseList.add(u);
            }

        }

        //Check if the amount of entries is correct
        assertEquals("Response body size did not match", 3, responseList.size());
    }



    /** Test case: getAssignmentsByCustomerIdShouldReturnAssignments
     *
     * Test if a GET result based on CustomerId returns the Assignments where the specifiec CustomerId is involved
     * The Http response should have HttpStatus Code: OK (200)
     */

    @Test
    public void getAssignmentsByCustomerIdShouldReturnAssignments() {
        //Add the Assignments that we will try to GET request to the database;

        Assignment a = new Assignment("consultantId1","customerId1","startDate1","endDate1");
        Assignment b = new Assignment("consultantId2","customerId1","startDate2","endDate2");
        Assignment c = new Assignment("consultantId3","customerId1","startDate3","endDate3");
        Assignment d = new Assignment("consultantId4","customerId4","startDate4","endDate4");
        Assignment e = new Assignment("consultantId5","customerId5","startDate5","endDate5");

        repo.save(a);
        repo.save(b);
        repo.save(c);
        repo.save(d);
        repo.save(e);

        String url = ROOT_PATH + "/assignmentsbycuid/" + a.getCustomerId();

        //Instantiate the HTTP GET Request
        ParameterizedTypeReference<Iterable<Assignment>> responseType = new ParameterizedTypeReference<Iterable<Assignment>>() {};
        ResponseEntity<Iterable<Assignment>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);


        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.OK;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());

        //Add the received entries to an ArrayList (has a .size() method to count the entries)
        ArrayList<Assignment> responseList = new ArrayList<>();
        if (response.getBody() != null) {
            for (Assignment u : response.getBody()) {
                responseList.add(u);
            }
        }

        //Check if the amount of entries is correct
        assertEquals("Response body size did not match", 3, responseList.size());
    }



    /** Test case: createAssignmentShouldCreateAssignment
     *
     * Test if a POST result of a Assignment instance results in the Assignment being saved to the database
     * The Http Request response should return with the HttpStatus code: OK (200)
     */
    @Test
    public void createAssignmentShouldCreateAssignment() {
        String url = UNIT_RESOURCE;

        //Instantiate the HTTP POST Request
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignment> httpEntity = new HttpEntity<>(CONTRACT_1, requestHeaders);
        ResponseEntity<Assignment> response = restTemplate.postForEntity(url, httpEntity, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.OK;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());

        //Check if the returned object is valid in comparison with the published on
        assertEquals("Returned entry is invalid", CONTRACT_1.getConsultantId(), response.getBody().getConsultantId());
        assertEquals("Returned entry is invalid", CONTRACT_1.getCustomerId(), response.getBody().getCustomerId());
        assertEquals("Returned entry is invalid", CONTRACT_1.getStartDate(), response.getBody().getStartDate());
        assertEquals("Returned entry is invalid", CONTRACT_1.getEndDate(), response.getBody().getEndDate());

        //Check if the returned entry contains an ID
        assertNotNull("Returned entry did not contain an ID", response.getBody().getId());

        //Check if the assignment was added to the database
        Assignment assignmentFromDb = repo.findOne(response.getBody().getId());

        //Check if the entry that was added is valid
        assertEquals("consultantId did not match",CONTRACT_1.getConsultantId(),assignmentFromDb.getConsultantId());
        assertEquals("customerId did not match",CONTRACT_1.getCustomerId(),assignmentFromDb.getCustomerId());
        assertEquals("startDate did not match",CONTRACT_1.getStartDate(),assignmentFromDb.getStartDate());
        assertEquals("endDate did not match",CONTRACT_1.getEndDate(),assignmentFromDb.getEndDate());

        //Check if only 1 entry was added
        assertEquals("More than one record was added to the database", 1, repo.count());
    }

    /** Test case: createAssignmentWithoutBodyShouldNotAddAssignment
     *
     * Test if a POST request without a body does not result in an entry added to the database
     * Also, the Http Request response should have HttpStatus code: BAD_REQUEST (400)
     */
    @Test
    public void createAssignmentWithoutBodyShouldNotAddAssignment() {
        String url = UNIT_RESOURCE;

        //Instantiate the HTTP POST Request
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignment> httpEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<Assignment> response = restTemplate.postForEntity(url, httpEntity, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.BAD_REQUEST;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());

        //Check if a Assignment was added to the database
        assertEquals("An entry was added to the database", 0, repo.count());
    }

    /** Test case: editAssignmentShouldSaveEditionsAndReturnUpdatedAssignment
     *
     * Test if a PUT request to edit an entry results in the entry being saved
     * The Http Request should respond with an updated entry
     * Also, the Http Request response should have HttpStatus code: OK (200)
     */
    @Test
    public void editAssignmentShouldSaveEditionsAndReturnUpdatedAssignment() {
        //Add the Assignment that we will try to PUT request to the database
        Assignment savedAssignment = repo.save(CONTRACT_1);

        String url = UNIT_RESOURCE + "/" + savedAssignment.getId();

        //Update the Assignment
        savedAssignment.setConsultantId("consultantIdEdited");
        savedAssignment.setCustomerId("customerIdEdited");
        savedAssignment.setStartDate("startDateEdited");
        savedAssignment.setEndDate("endDateEdited");


        //Instantiate the HTTP PUT Request
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignment> httpEntity = new HttpEntity<>(savedAssignment, requestHeaders);
        ResponseEntity<Assignment> response = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.OK;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNotNull("Http Request response body did not contain a Assignment object", response.getBody());

        //Check if the returned entry contains is valid
        assertEquals("Returned entry contained invalid field values", savedAssignment.getId(), response.getBody().getId());
        assertEquals("Returned entry contained invalid field values", savedAssignment.getConsultantId(), response.getBody().getConsultantId());
        assertEquals("Returned entry contained invalid field values", savedAssignment.getCustomerId(), response.getBody().getCustomerId());
        assertEquals("Returned entry contained invalid field values", savedAssignment.getStartDate(), response.getBody().getStartDate());
        assertEquals("Returned entry contained invalid field values", savedAssignment.getEndDate(), response.getBody().getEndDate());

        //Fetch the updated entry from the database
        Assignment updatedAssignment = repo.findOne(savedAssignment.getId());

        //Check if the update was saved to the database
        assertEquals("Updated entry was not saved to the database", savedAssignment.getConsultantId(), updatedAssignment.getConsultantId());
        assertEquals("Updated entry was not saved to the database", savedAssignment.getCustomerId(), updatedAssignment.getCustomerId());
        assertEquals("Updated entry was not saved to the database", savedAssignment.getStartDate(), updatedAssignment.getStartDate());
        assertEquals("Updated entry was not saved to the database", savedAssignment.getEndDate(), updatedAssignment.getEndDate());
    }

    /** Test case: editUnexistingAssignmentShouldReturnError
     *
     * Test that when we try to update an unexisting entry the Http Request response does not contain an object
     * Also, it should have HttpStatus code: BAD_REQUEST (400)
     */
    @Test
    public void editUnexistingAssignmentShouldReturnError() {
        //Instantiate the HTTP PUT Request
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignment> httpEntity = new HttpEntity<>(CONTRACT_1, requestHeaders);
        ResponseEntity<Assignment> response = restTemplate.exchange(UNIT_RESOURCE+"/unexistingid", HttpMethod.PUT, httpEntity, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.BAD_REQUEST;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNull("Http Request response body did contain an entry object", response.getBody());
    }

    /** Test case: deleteUnexistingAssignmentShouldReturnError
     *
     * Test that if we try to delete an unexisting entry, this returns the HttpStatus code: BAD_REQUEST (400)
     */
    @Test
    public void deleteUnexistingAssignmentShouldReturnError() {
        String url = UNIT_RESOURCE + "/" + NON_EXISTING_ID;

        //Instantiate the HTTP DELETE Request
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignment> httpEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<Assignment> response = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.BAD_REQUEST;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNull("Http Request response body did contain an entry object", response.getBody());
    }

    /** Test case: deleteAssignmentShouldReturnError
     *
     * Test if instantiating a DELETE request on an existing entry results in the entry being deleted
     * The Http Request response should have HttpStatus code: NO_CONTENT (204)
     */
    @Test
    public void deleteAssignmentShouldReturnError() {
        //Add the Assignment that we will try to GET request to the database
        Assignment savedAssignment = repo.save(CONTRACT_1);

        String url = UNIT_RESOURCE + "/" + savedAssignment.getId();

        //Instantiate the HTTP DELETE Request
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Assignment> httpEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<Assignment> response = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Assignment.class);

        //Check if we received a response
        assertNotNull("Http Request response was null", response);

        //Check if we receive the correct HttpStatus code
        HttpStatus expectedCode = HttpStatus.NO_CONTENT;
        assertEquals("HttpStatus code did not match", expectedCode, response.getStatusCode());

        //Check if the response contained a Assignment object in its body
        assertNull("Http Request response body did contain an entry object", response.getBody());

        //Check if the entry was deleted in the database
        assertEquals("Assignment was not deleted from the database", 0, repo.count());
    }
}