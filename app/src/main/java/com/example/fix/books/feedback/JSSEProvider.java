package com.example.fix.books.feedback;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public final class JSSEProvider extends Provider {

    private static final long serialVersionUID = 1L;

    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            put("SSLContext.TLS",
                    "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
            put("Alg.Alias.SSLContext.TLSv1", "TLS");
            put("KeyManagerFactory.X509",
                    "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
            put("TrustManagerFactory.X509",
                    "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
            return null;
        });
    }
}
