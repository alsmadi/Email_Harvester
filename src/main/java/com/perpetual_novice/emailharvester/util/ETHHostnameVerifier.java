package com.perpetual_novice.emailharvester.util;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.kerberos.KerberosPrincipal;

import com.ning.http.util.HostnameChecker;

public class ETHHostnameVerifier implements HostnameVerifier {
	
	/** Checks if a given hostname matches the certificate or principal of
     * a given session.
     */
    /*private boolean hostnameMatches(String hostname, SSLSession session) {
        HostnameChecker checker = HostnameChecker.getInstance(HostnameChecker.TYPE_TLS);

        boolean validCertificate = false, validPrincipal = false;
        try {
            Certificate[] peerCertificates = session.getPeerCertificates();

            if (peerCertificates.length > 0 &&
                    peerCertificates[0] instanceof X509Certificate) {
                X509Certificate peerCertificate =
                        (X509Certificate)peerCertificates[0];

                try {
                    checker.match(hostname, peerCertificate);
                    // Certificate matches hostname
                    validCertificate = true;
                } catch (CertificateException ex) {
                    // Certificate does not match hostname
                }
            } else {
                // Peer does not have any certificates or they aren't X.509
            }
        } catch (SSLPeerUnverifiedException ex) {
            // Not using certificates for peers, try verifying the principal
            try {
                Principal peerPrincipal = session.getPeerPrincipal();
                if (peerPrincipal instanceof KerberosPrincipal) {
                    //validPrincipal = HostnameChecker.match(hostname,(KerberosPrincipal)peerPrincipal);
                } else {
                    // Can't verify principal, not Kerberos
                }
            } catch (SSLPeerUnverifiedException ex2) {
                // Can't verify principal, no principal
            }
        }

        return validCertificate || validPrincipal;
    }*/
	
	public boolean verify(String arg0, SSLSession arg1) {
		return false;
		
        /*if (hostnameMatches(arg0, arg1)) {
            return true;
        } else {
            // TODO: Add application-specific checks for
            // hostname/certificate match
            return false;
        }*/
	}

}
