/**
 * @package com.mdv.controllers
 * @brief Endpoints for receiving incoming requests
 */
package com.mdv.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mdv.exceptions.NoActionFoundException;
import com.mdv.exceptions.UserAlreadyFoundException;
import com.mdv.exceptions.UserMultipleRecordsException;
import com.mdv.exceptions.UserNotFoundException;
import com.mdv.exceptions.VoteAlreadyFoundException;
import com.mdv.model.User;
import com.mdv.model.UserIdentifier;
import com.mdv.model.UserIdentifierImpl;
import com.mdv.model.VoteIdentifier;
import com.mdv.services.UserService;

/**
 * 
 * @brief Class to intercepts incoming Http requests
 * 
 * @author Ioana Ivan
 * @date 29/05/2020
 */
@RestController
public class UserRESTController {

	/**
	 * @brief Service for User-related operations
	 */
	@Autowired
	public UserService userService;

	/**
	 * @brief Logger for the UserRESTController class
	 */
	private Logger log = LoggerFactory.getLogger(UserRESTController.class);

	/**
	 * @brief Endpoint for incoming registration requests
	 * 
	 * @author Ioana Ivan
	 * @date 29/05/2020
	 * 
	 * @param User the user to be created
	 * @exception UserAlreadyFoundException    if user has already registered with
	 *                                         that idCard
	 * @exception UserMultipleRecordsException if multiple records for this user
	 *                                         already exist <b>fraud case</b>
	 * @exception UserNotFoundException        if user is not found in the
	 *                                         government database
	 * @return <b>UserIdentifier</b> the user's identifiers generated by the
	 *         application
	 */
	@CrossOrigin
	@PostMapping("/createUser")
	UserIdentifier createUser(@RequestBody User user)
			throws UserAlreadyFoundException, UserMultipleRecordsException, UserNotFoundException {
		log.info("POST Request to /createUser received with data : " + "firstName: " + user.getFirstName() + " name: "
				+ user.getName() + " location: " + user.getLocation() + " nationalcardId: " + user.getNationalCardId()
				+ " securityCardId: " + user.getSecurityCardId());
		return userService.createUser(user);
	}

	/**
	 * @brief Endpoint for incoming authentication requests
	 * 
	 * @author Ioana Ivan
	 * @date 29/05/2020
	 * 
	 * @param UserIdentifierImpl the user's identifiers and passcode
	 * @exception UserNotFoundException        if no user is found for the provided
	 *                                         identifiers
	 * @exception UserMultipleRecordsException if multiple registration actions for
	 *                                         this user already exist <b>fraud
	 *                                         case</b>
	 * @exception NoActionFoundException       if no registration action is found
	 *                                         for the user
	 * @return void
	 */
	@CrossOrigin
	@PostMapping("/authUser")
	void authUser(@RequestBody UserIdentifierImpl userIdentifier)
			throws UserNotFoundException, UserMultipleRecordsException, NoActionFoundException {
		log.info("POST Request to /authUser received with data : " + "id: " + userIdentifier.getId());
		userService.authUser(userIdentifier);
	}

	/**
	 * @brief Endpoint for incoming vote requests
	 * 
	 * @author Ioana Ivan
	 * @date 29/05/2020
	 * 
	 * @param VoteIdentifier the user's identifier and vote option
	 * @exception VoteAlreadyFoundException if a vote is already found for this user
	 * 
	 * @exception NoActionFoundException    if no authentication action is found for
	 *                                      the user
	 * @return void
	 */
	@CrossOrigin
	@PostMapping("/vote")
	void vote(@RequestBody VoteIdentifier voteIdentifier) throws NoActionFoundException, VoteAlreadyFoundException {
		log.info("POST Request to /vote received with data : " + "id: " + voteIdentifier.getId());
		userService.createVote(voteIdentifier);
	}

	/**
	 * @brief Endpoint for testing if service is up
	 * 
	 * @author Ioana Ivan
	 * @date 29/05/2020
	 * 
	 * @return Default success answer
	 */
	@CrossOrigin
	@GetMapping(value = "/test", produces = "text/plain")
	String test() {
		log.info("Test attempt");
		return "{\"success\":1}";
	}
}
