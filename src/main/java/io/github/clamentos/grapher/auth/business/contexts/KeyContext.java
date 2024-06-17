package io.github.clamentos.grapher.auth.business.contexts;

///
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;

///..
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;

///.
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Paths;

///..
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

///..
import java.security.interfaces.RSAPublicKey;

///..
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

///.
import lombok.Getter;

///.
import org.springframework.beans.factory.annotation.Value;

///..
import org.springframework.stereotype.Component;

///
@Getter
@Component

///
public final class KeyContext {

    ///
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    ///..
    private final JWSSigner jwtSigner;
    private final JWSVerifier jwtVerifier;

    ///
    public KeyContext(

        @Value("${grapher-auth.privateKeyPath}") String privatePath,
        @Value("${grapher-auth.publicKeyPath}") String publicPath

    ) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(Paths.get(privatePath))));
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Files.readAllBytes(Paths.get(publicPath))));

        jwtSigner = new RSASSASigner(privateKey);
        jwtVerifier = new RSASSAVerifier((RSAPublicKey) publicKey);
    }

    ///
}
