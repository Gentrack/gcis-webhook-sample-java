import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class VerifySignature {
    /**
     * To verify signature
     *
     * @param v         - The 'v' part in the 'X-Payload-Signature' http header
     * @param content   - The content to be verified, e.g. '1616634644.{"appId":"59eaf43d-31c0-4151-8c1f-2ae76c68ce65"....}'
     * @param publicKey - The public key, in pem format, used to verify
     * @return true if passed, otherwise false
     */
    public boolean verify(String v, String content, String publicKey) {
        try {
            final PublicKey pKey = loadPublicKey(publicKey);
            final Signature sha512withRSA = Signature.getInstance("SHA512withRSA");
            sha512withRSA.initVerify(pKey);
            sha512withRSA.update(content.getBytes(StandardCharsets.UTF_8));
            return sha512withRSA.verify(Base64.getDecoder().decode(v));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey loadPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final byte[] decode = Base64.getDecoder().decode(
                publicKey.replaceAll("[\\r\\n]+", "")
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
        );
        final X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpecX509);
    }

    public static void main(String[] args) {
        final String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzXMzPHjqwmHsAJ8thkP9\n" +
                "abNbyFuUqbmJNwKmG5j9wVcC4D1hMFY6MzTNTZWoI3VviYbKJXhcqR35WlEfmXCs\n" +
                "WItIsG+8N8+uxY1qyUJxvqi2VkJnQc+60OwZ7CaSVHLdOfoYvNmnSJeCWb+Ukhda\n" +
                "T5AaR+oDNjvjT5VMqe1cGiafJMOZV363QrYY0LLUis37YapWynbx6g0MHMX14riF\n" +
                "htodqHxis7Kl7NCH8DkZr+mDxlpz5DU6MeDG/LH8GeqtgDAetSe+P9azBj1tClI3\n" +
                "0EJCd/3Z2KwzslW9mF1Jy8SioMxuTUdjPZCUQrzDcVRPyDX/09ODRAe0l6u37HRG\n" +
                "JwIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        final String t = "1616634644";
        final String v = "YurjkdI66SDlLsbixHF3mdvbP6zN0hYZl2U3SvXw8NFmkgpcpLYl+PNInGRsW723K7TCqDnE8cuCkRCx+cc7Mthl2u4ICmWgA7CMG8AwntfzeC9+IWQkBK+abbjM+rdH8qFmen+febxKq7QX50M1KpoZ5D6EdW+ZKnHKREq6rzlnkr3FMF4X4k5J2xA3+TZfPSo/OyEo2t7eLCMyX9JFEz3DtEjISQzCwPB4qVPL+gOZNat/I5nKUeC0cEbVPhZ6/MkBSu55Eo72m98vD45Y5bRqeoKc4JOxVlpHnL2DFNdaEH59cqSnrG0WdbyDEnWi+YPyI2oyH8apyovxQ2TlZA==";
        final String payload = "{\"appId\":\"59eaf43d-31c0-4151-8c1f-2ae76c68ce65\",\"createdAt\":\"2021-03-25T01:10:44.392Z\",\"data\":{\"message\":\"This is a ping message\"},\"eventId\":\"9a93dd8d-b81e-4ede-b7ab-c003d5fea3ee\",\"eventType\":\"integration:ping\",\"tenantId\":\"053c83d2-1334-453e-9c88-81ae69bb69b6\",\"version\":1}";
        VerifySignature verifySignature = new VerifySignature();
        final boolean verified = verifySignature.verify(v, t + "." + payload, publicKey);
        if (verified) {
            System.out.println("*********************************************************");
            System.out.println("************ Signature verified successfully ************");
            System.out.println("*********************************************************");
        } else {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("!!!!!!!!!!!! Failed to verify signature !!!!!!!!!!!!");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
}
