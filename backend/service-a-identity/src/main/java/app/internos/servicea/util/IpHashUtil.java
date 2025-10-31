package app.internos.servicea.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class IpHashUtil {
    
    private final String pepper;
    
    public IpHashUtil(@Value("${IP_HASH_PEPPER:}") String pepper) {
        if (pepper == null || pepper.isEmpty()) {
            throw new IllegalArgumentException("IP_HASH_PEPPER environment variable must be set");
        }
        this.pepper = pepper;
    }
    
    /**
     * Hashes an IP address using HMAC-SHA256 with pepper.
     * This ensures one-way hashing and prevents rainbow table attacks.
     * 
     * @param ipAddress IP address to hash
     * @return Hashed IP address as hexadecimal string
     */
    public String hashIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    pepper.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            
            byte[] hashBytes = mac.doFinal(ipAddress.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash IP address", e);
        }
    }
    
    /**
     * Alternative hashing method using SHA-256 with pepper appended.
     * This is a fallback if HMAC is not available.
     * 
     * @param ipAddress IP address to hash
     * @return Hashed IP address as hexadecimal string
     */
    public String hashIpWithSha256(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedIp = ipAddress + pepper;
            byte[] hashBytes = digest.digest(saltedIp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash IP address with SHA-256", e);
        }
    }
}

