package edu.ualberta.med.biobank.server.security;

import gov.nih.nci.security.authentication.LockoutManager;
import gov.nih.nci.system.security.acegi.authentication.CSMAuthenticationProvider;

import java.text.MessageFormat;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.BadCredentialsException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;

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
                            Messages.getString("BiobankAuthenticationProvider.failed_connection_msg"), //$NON-NLS-1$
                            user));
            }
            throw e;
        }
    }
}
