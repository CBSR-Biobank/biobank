package edu.ualberta.med.biobank.server.security;

import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.system.security.acegi.authentication.CSMAuthenticationProvider;

import java.text.MessageFormat;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;

/**
 * Don't really need to translate this text since this message will only be
 * displayed in stacktraces.
 */
public class BiobankAuthenticationProvider extends CSMAuthenticationProvider {

    @Override
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
                    MessageFormat
                        .format(
                            "At least 3 failed connection attempts. Login for ''{0}'' disabled for 30 min.", //$NON-NLS-1$
                            user));
            }
            throw e;
        }
    }
}
