package edu.ualberta.med.biobank.server.security;

import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.system.security.acegi.authentication.CSMAuthenticationProvider;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;

public class BiobankAuthenticationProvider extends CSMAuthenticationProvider {

    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {

        // Use CSM authenticationManager to authenticate User.
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (BadCredentialsException e) {
            String user = userDetails.getUsername();
            boolean lockout = LockoutManager.getInstance()
                .isUserLockedOut(user);
            if (lockout) {
                throw new BadCredentialsException(
                    "At least 3 failed connection attempts. Login for '" + user
                        + "' disabled for 30 min.");
            }
            throw e;
        }
    }
}
