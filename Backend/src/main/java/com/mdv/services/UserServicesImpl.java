/*
 * Class containing services used for processing
 */

package com.mdv.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mdv.exceptions.NoActionFoundException;
import com.mdv.exceptions.UserAlreadyFoundException;
import com.mdv.exceptions.UserMultipleRecordsException;
import com.mdv.exceptions.UserNotFoundException;
import com.mdv.exceptions.VoteAlreadyFoundException;
import com.mdv.model.*;
import com.mdv.repository.*;

@Service
public class UserServicesImpl implements UserService {

	@Autowired
	private UserServiceJDBCTemplate userJDBC;

	private static Logger log = LoggerFactory.getLogger(UserService.class);

	@Override
	public UserIdentifier createUser(User user) throws UserAlreadyFoundException, UserMultipleRecordsException {
		log.info("User creation for user firstName: " + user.getFirstName() + ", name : " + user.getName());

		// Check if user is a real person - present in Gov DB
		// TODO

		// Check if user already registered - present in MDV DB
		userJDBC.findByIdCard(user.getNationalCardId());

		UserIdentifier userIdent = new UserIdentifierImpl();

		// Generate user identifier and password before store in DB
		String idGen = userIdent.generateId();
		String codeGen = userIdent.generateCode();

		System.out.println("the code is:" + codeGen);

		// Test the encryption method
		String enCode = userIdent.encryptCode(codeGen);
		System.out.println("encrypted code:" + enCode);

		// Test the decryption method
		String deCode = userIdent.decryptCode(enCode);
		System.out.println("decrypted code:" + deCode);

		// Create the user with encrypted code
		userIdent.setId(idGen);
		userIdent.setCode(enCode);

		userJDBC.createUser(user, userIdent);

		// Sucessful registration
		userJDBC.saveAction("Register", "SUCCESS", "NULL", enCode);

		return userIdent;
	}

	@Override
	public void authUser(UserIdentifier userIdentifier)
			throws UserNotFoundException, UserMultipleRecordsException, NoActionFoundException {
		log.info("User authentification for user id: " + userIdentifier.getId());
		userJDBC.findByIdentifier(userIdentifier);

		// Check authentication condition : successful registration

		if (!userJDBC.getAction(userIdentifier.getId(), "Register", "SUCCESS").equals("")) {

			// Successful authentication
			userJDBC.saveAction("Authenticate", "SUCCESS", "NULL", userIdentifier.getId());
		} else {
			log.info("No  registration found for user: " + userIdentifier.getId());
			userJDBC.saveAction("Authenticate", "FAILED", "No registration found.", userIdentifier.getId());
			throw new NoActionFoundException("User not registered. Please register first.");
		}
	}

	@Override
	public void createVote(VoteIdentifier voteIdentifier)
			throws NoActionFoundException, UserMultipleRecordsException, VoteAlreadyFoundException {
		log.info("Voting for user id: " + voteIdentifier.getId());

		// Check vote conditions: successful authentication and no previous vote present
		if (userJDBC.getAction(voteIdentifier.getId(), "Authenticate", "SUCCESS").equals("")) {
			throw new NoActionFoundException("User not authenticated. Please authenticate first.");
		} else {
			if (!userJDBC.getAction(voteIdentifier.getId(), "Vote", "SUCCESS").equals("")) {
				throw new VoteAlreadyFoundException(
						"You have already voted. To log a complain, please call customer support.");
			} else {
				// Sucessful vote
				userJDBC.createVote(voteIdentifier);
				userJDBC.saveAction("Vote", "SUCCESS", "NULL", voteIdentifier.getId());
			}
		}
	}
}
