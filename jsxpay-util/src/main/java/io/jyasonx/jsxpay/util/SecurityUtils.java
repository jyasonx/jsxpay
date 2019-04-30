package io.jyasonx.jsxpay.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

/**
 * Utility methods for common cryptography usages including digesting, signing & verifying,
 * encrypting & decrypting, etc.
 */
public class SecurityUtils {
    public static final String DEFAULT_PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
    public static final String X509 = "X.509";
    public static final String PKCS12 = "PKCS12";
    public static final String DEFAULT_SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    // For 1024bit encrypt/decrypt block key size
    private static final int ENCRYPT_BLOCK_KEY_SIZE = 117;
    private static final int DECRYPT_BLOCK_KEY_SIZE = 128;

    public static final String MD5 = "MD5";
    public static final String MD2 = "MD2";
    public static final String SHA1 = "SHA1";
    public static final String SHA256 = "SHA-256";
    public static final String SHA512 = "SHA-512";
    public static final String SM3 = "SM3";
    public static final String RSA = "RSA";
    public static final String AES = "AES";
    public static final String DSA = "DSA";
    public static final String SM2 = "SM2";

    public static final String DES = "DES";
    public static final String SHA1_WITH_RSA = "SHA1withRSA";
    public static final String SHA512_WITH_RSA = "SHA512WithRSA";
    private static final String DEFAULT_ISSUER = "CN=root,OU=上银支付,O=上海银行";
    private static final String DEFAULT_ALIAS = "BRAAVOS";
    private static final int DEFAULT_KEY_SIZE = 1024;

    public static final String PFX_EXT = ".pfx";
    public static final String CERT_EXT = ".cer";

    private SecurityUtils() {
        // private constructor for util class...
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Creates a {@code PrivateKey} from the base64 encoded {@code KeyStore} content.
     *
     * @param type     the type of {@code PrivateKey}
     * @param content  the base64 encoded {@code PrivateKey}
     * @param pwd      the password of {@code PrivateKey}
     * @param provider optional parameters, the name for {@link Provider} If it is
     *                 empty, the method will use system default provider.
     * @return the {@code PrivateKey}
     * @throws IllegalArgumentException if the content of key is not base64 encoded
     * @throws SecurityException        if the key is failed to generate
     */
    public static PrivateKey from(String type, String content,
                                  String pwd, String provider) throws SecurityException {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(content);

        if (!Base64.isBase64(content)) {
            throw new IllegalArgumentException("The content of private key must be encoded as base64");
        }

        try {
            if (Strings.isNullOrEmpty(pwd)) {
                KeyFactory keyFactory = Strings.isNullOrEmpty(provider)
                        ? KeyFactory.getInstance(type) : KeyFactory.getInstance(type, provider);
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(content)));
            } else {
                char[] chars = pwd.toCharArray();
                KeyStore keyStore = getKeyStore(type, content, pwd, provider);
                Enumeration<String> keyAliases = keyStore.aliases();
                String keyAlias;
                PrivateKey privateKey = null;

                while (keyAliases.hasMoreElements()) {
                    keyAlias = keyAliases.nextElement();
                    if (keyStore.isKeyEntry(keyAlias)) {
                        privateKey = (PrivateKey) keyStore.getKey(keyAlias, chars);
                        break;
                    }
                }

                if (privateKey == null) {
                    throw new InvalidKeyException("No available private key was found");
                } else {
                    return privateKey;
                }
            }
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | InvalidKeySpecException | UnrecoverableKeyException | KeyStoreException err) {
            throw new SecurityException("Error generating the private key", err);
        }
    }

    /**
     * Use BC Provider as default provider for getting private key.
     */
    public static PrivateKey from(String type, String content, String pwd) throws SecurityException {
        return from(type, content, pwd, DEFAULT_PROVIDER);
    }

    /**
     * Creates a {@code PublicKey} from the base64 encoded {@code Certificate} content.
     *
     * @param type    the type of {@code PublicKey}
     * @param content the base64 encoded {@code PublicKey}
     * @return the {@code PublicKey}
     * @throws IllegalArgumentException if the content of certificate is not base64 encoded
     * @throws SecurityException        if the key is failed to generate
     */
    public static PublicKey from(String type, String content) throws SecurityException {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(content);

        if (!Base64.isBase64(content)) {
            throw new IllegalArgumentException("The content of cert must be encoded as base64");
        }

        try {
            if (X509.equals(type)) {
                return CertificateFactory
                        .getInstance(type, DEFAULT_PROVIDER)
                        .generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(content)))
                        .getPublicKey();
            } else {
                return KeyFactory
                        .getInstance(type, DEFAULT_PROVIDER)
                        .generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(content)));
            }
        } catch (NoSuchProviderException | NoSuchAlgorithmException
                | CertificateException | InvalidKeySpecException err) {
            throw new SecurityException("Error generating the public key", err);
        }
    }

    /**
     * Retrieves the serial number from the base64 encoded {@code PrivateKey}.
     *
     * @param type     the type of {@code PrivateKey}
     * @param key      the base64 encoded {@code PrivateKey}
     * @param password the password of {@code PrivateKey}
     * @return the serial no of {@code PrivateKey}
     * @throws IllegalArgumentException if the content of key is not base64 encoded
     * @throws SecurityException        if the serial no is failed to retrieve
     */
    public static String serialNo(String type, String key, String password) throws SecurityException {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(password);

        if (!Base64.isBase64(key)) {
            throw new IllegalArgumentException("The content of private key must be encoded as base64");
        }

        X509Certificate cert = getCertificate(type, key, password, DEFAULT_PROVIDER);
        return cert.getSerialNumber().toString();
    }

    /**
     * Retrieves the serial number from the base64 encoded {@code PublicKey}.
     *
     * @param type the type of {@code PublicKey}
     * @param key  the base64 encoded {@code PublicKey}
     * @return the serial no of {@code PublicKey}
     * @throws IllegalArgumentException if the content of key is not base64 encoded
     * @throws SecurityException        if the serial no is failed to retrieve
     */
    public static String serialNo(String type, String key) throws SecurityException {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(key);

        if (!Base64.isBase64(key)) {
            throw new IllegalArgumentException("The content of certificate must be encoded as base64");
        }

        try {
            CertificateFactory factory = CertificateFactory.getInstance(type);
            return ((X509Certificate) factory.generateCertificate(
                    new ByteArrayInputStream(Base64.decodeBase64(key)))).getSerialNumber().toString();
        } catch (CertificateException err) {
            throw new SecurityException("Error retrieving the serial no from key", err);
        }
    }

    /**
     * Generates a salt bytes randomly with the specified algorithm.
     *
     * @param algorithm the algorithm
     * @return the generated salt bytes
     * @throws SecurityException if the salt is failed to generate
     */
    public static byte[] generateSalt(String algorithm) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        try {
            // 8 bytes is good enough for salt
            // see more details on: http://stackoverflow.com/a/5197921/339286
            byte[] salt = new byte[8];
            SecureRandom.getInstance(algorithm).nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException err) {
            throw new SecurityException("Error generating the salt", err);
        }
    }

    /**
     * Generates key randomly with the specified algorithm.
     *
     * @param algo the algorithm
     * @return the {@code Key}
     */
    public static Key genRandomKey(String algo) throws SecurityException {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance(algo);
        } catch (NoSuchAlgorithmException err) {
            throw new SecurityException("Error generating the key", err);
        }
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        return keygen.generateKey();
    }

    /**
     * Generates a {@code SecretKey} with the specified algorithm.
     *
     * @param algorithm the algorithm
     * @return the {@code SecretKey}
     * @throws SecurityException if the secret key is failed to generate
     */
    public static SecretKey generateSecretKey(String algorithm) throws GeneralSecurityException {
        return generateSecretKey(algorithm, DEFAULT_PROVIDER);
    }


    public static SecretKey generateSecretKey(String algorithm, String provider)
            throws GeneralSecurityException {
        KeyGenerator generator = Strings.isNullOrEmpty(provider) ?
                KeyGenerator.getInstance(algorithm) :
                KeyGenerator.getInstance(algorithm, DEFAULT_PROVIDER);
        return generator.generateKey();
    }

    /**
     * Generates a {@code SecretKey} with the specified algorithm and key specification.
     *
     * @param algorithm the algorithm
     * @param spec      the {@code KeySpec}
     * @return the {@code SecretKey}
     * @throws SecurityException if the secret key is failed to generate
     */
    public static SecretKey generateSecretKey(String algorithm, KeySpec spec)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(spec);
        try {
            return SecretKeyFactory.getInstance(algorithm, DEFAULT_PROVIDER).generateSecret(spec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException err) {
            throw new SecurityException("Error generating the secret key", err);
        }
    }

    /**
     * Generates a {@code SecretKey} with the specified algorithm.
     */
    public static SecretKey generateSecretKey(String algorithm, int len)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkArgument(len > 0);
        try {
            KeyGenerator kg = KeyGenerator.getInstance(algorithm, SecurityUtils.DEFAULT_PROVIDER);
            kg.init(len);
            return kg.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException err) {
            throw new SecurityException("Error generating the secret key", err);
        }
    }

    /**
     * Completes the hash computation by performing final operations such as padding.
     *
     * @param algorithm the algorithm
     * @param data      the data bytes to be computed with
     * @return the array of bytes for the resulting hash value
     * @throws SecurityException if failed to digest data
     */
    public static byte[] digest(String algorithm, byte[] data) throws SecurityException {
        return digest(algorithm, data, null);
    }

    /**
     * Completes the hash computation by performing final operations such as padding.
     *
     * @param algorithm the algorithm
     * @param data      the data bytes to be computed with
     * @return the array of bytes for the resulting hash value
     * @throws SecurityException if failed to digest data
     */
    public static byte[] digest(String algorithm, byte[] data, byte[] salt) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(data);

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm, DEFAULT_PROVIDER);
            if (salt != null) {
                digest.update(salt);
            }
            digest.update(data);
            return digest.digest();
        } catch (NoSuchProviderException | NoSuchAlgorithmException err) {
            throw new SecurityException("Error digesting the data", err);
        }
    }

    /**
     * Completes the hash computation by performing final operations such as padding
     * (without provider).
     *
     * @param algorithm the algorithm
     * @param data      the data bytes to be computed with
     * @return the array of bytes for the resulting hash value
     * @throws SecurityException if failed to digest data
     */
    public static byte[] digestWithNoProvider(String algorithm, byte[] data)
            throws SecurityException {
        return digestWithNoProvider(algorithm, data, null);
    }

    /**
     * Completes the hash computation by performing final operations such as padding
     * (without provider).
     *
     * @param algorithm the algorithm
     * @param data      the data bytes to be computed with
     * @return the array of bytes for the resulting hash value
     * @throws SecurityException if failed to digest data
     */
    public static byte[] digestWithNoProvider(String algorithm, byte[] data, byte[] salt)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(data);

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                digest.update(salt);
            }
            digest.update(data);
            return digest.digest();
        } catch (NoSuchAlgorithmException err) {
            throw new SecurityException("Error digesting the data", err);
        }
    }

    /**
     * Signs data bytes with the specified algorithm and {@code PrivateKey} instance.
     *
     * @param algorithm the algorithm
     * @param key       the {@code PrivateKey}
     * @param data      the data bytes to be signed
     * @return the bytes of signature
     * @throws SecurityException if failed to sign data
     */
    public static byte[] sign(String algorithm, PrivateKey key, byte[] data)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);

        try {
            Signature signer = Signature.getInstance(algorithm, DEFAULT_PROVIDER);
            signer.initSign(key);
            signer.update(data);
            return signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException
                | SignatureException | NoSuchProviderException err) {
            throw new SecurityException("Error signing the data with private key", err);
        }
    }

    /**
     * Verifies data bytes with the corresponding signature, algorithm and {@code PublicKey}
     * instance.
     *
     * @param algorithm the algorithm
     * @param key       the {@code PublicKey}
     * @param data      the data bytes that already signed
     * @param signature the signature string to be verified
     * @return true if signature is valid, false otherwise
     * @throws SecurityException if failed to verify data
     */
    public static boolean verify(String algorithm, PublicKey key, byte[] data, byte[] signature)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(signature);

        try {
            Signature verifier = Signature.getInstance(algorithm);
            verifier.initVerify(key);
            verifier.update(data);
            return verifier.verify(signature);
        } catch (NoSuchAlgorithmException
                | InvalidKeyException | SignatureException err) {
            throw new SecurityException("Error verifying the data with public key", err);
        }
    }

    /**
     * Encrypts data bytes with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm the algorithm
     * @param key       the {@code key}
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] encrypt(String algorithm, Key key, byte[] data) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);

        try {
            Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            return encryptor.doFinal(data);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error encrypt the data with key", err);
        }
    }

    /**
     * Encrypts data bytes with the specified algorithm, parameter specification and {@code Key}
     * instance.
     *
     * @param algorithm the algorithm
     * @param spec      the algorithm parameter specification
     * @param key       the {@code key} instance*
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] encrypt(String algorithm, AlgorithmParameterSpec spec, Key key, byte[] data)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(spec);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);

        try {
            Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            encryptor.init(Cipher.ENCRYPT_MODE, key, spec);
            return encryptor.doFinal(data);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException
                | IllegalBlockSizeException | InvalidAlgorithmParameterException err) {
            throw new SecurityException("Error encrypt the data with key & spec", err);
        }
    }

    /**
     * Encrypts data bytes with the specified algorithm, parameter specification and {@code Key}
     * instance.
     *
     * @param algorithm the algorithm
     * @param key       the {@code key} instance*
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] encryptWithSpec(String algorithm, Key key, byte[] data)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);
        try {
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            IvParameterSpec spec = new IvParameterSpec(new byte[cipher.getBlockSize()]);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(data);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException
                | IllegalBlockSizeException | InvalidAlgorithmParameterException err) {
            throw new SecurityException("Error encrypt the data with key & spec", err);
        }
    }

    /**
     * Encrypts data bytes with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm the algorithm
     * @param key       the {@code key}
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] encryptWithNoProvider(String algorithm, Key key, byte[] data) throws
            SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);

        try {
            Cipher encryptor = Cipher.getInstance(algorithm);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            return encryptor.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error encrypt the data with key", err);
        }
    }

    /**
     * Block encrypts data bytes with the specified algorithm and {@code Key} instance by block.
     *
     * @param algorithm the algorithm
     * @param key       the {@code key}
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] blockEncrypt(String algorithm,
                                      Key key, byte[] data) throws SecurityException {
        return blockEncrypt(algorithm, key, data, ENCRYPT_BLOCK_KEY_SIZE);
    }

    /**
     * Block encrypts data bytes with the specified algorithm and {@code Key} instance by block.
     *
     * @param algorithm           the algorithm
     * @param key                 the {@code key}
     * @param data                the raw data bytes
     * @param encryptBlockKeySize the encrypt block key size
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] blockEncrypt(String algorithm, Key key, byte[] data,
                                      int encryptBlockKeySize) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);
        try {
            Cipher encryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = null;
            for (int i = 0; i < data.length; i += encryptBlockKeySize) {
                byte[] doFinal = encryptor.doFinal(subArray(data, i, i + encryptBlockKeySize));
                result = addAll(result, doFinal);
            }
            return result;
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error encrypt the data with key", err);
        }

    }

    /**
     * Block encrypts data bytes with the specified algorithm and {@code Key} instance by block
     * (without provider).
     *
     * @param algorithm the algorithm
     * @param key       the {@code key}
     * @param data      the raw data bytes
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] blockEncryptWithNoProvider(String algorithm, Key key, byte[] data)
            throws SecurityException {
        return blockEncryptWithNoProvider(algorithm, key, data, ENCRYPT_BLOCK_KEY_SIZE);
    }

    /**
     * Block encrypts data bytes with the specified algorithm and {@code Key} instance by block
     * (without provider).
     *
     * @param algorithm           the algorithm
     * @param key                 the {@code key}
     * @param data                the raw data bytes
     * @param encryptBlockKeySize the encrypt block key size
     * @return the encrypted data bytes
     * @throws SecurityException if failed to encrypt data
     */
    public static byte[] blockEncryptWithNoProvider(String algorithm, Key key, byte[] data,
                                                    int encryptBlockKeySize) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(data);
        try {
            Cipher encryptor = Cipher.getInstance(algorithm);
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = null;
            for (int i = 0; i < data.length; i += encryptBlockKeySize) {
                byte[] doFinal = encryptor.doFinal(subArray(data, i, i + encryptBlockKeySize));
                result = addAll(result, doFinal);
            }
            return result;
        } catch (NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error encrypt the data with key", err);
        }

    }

    /**
     * Decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm     the algorithm
     * @param key           the {@code key} instance
     * @param encryptedData the encrypted data bytes
     * @return the decrypted data bytes
     * @throws SecurityException if failed to decrypt data
     */
    public static byte[] decrypt(String algorithm, Key key, byte[] encryptedData)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);

        try {
            Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            decryptor.init(Cipher.DECRYPT_MODE, key);
            return decryptor.doFinal(encryptedData);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error decrypt the data with key", err);
        }
    }

    /**
     * Decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm     the algorithm
     * @param spec          the algorithm parameter specification
     * @param key           the {@code key} instance
     * @param encryptedData the encrypted data bytes
     * @return the decrypted data bytes
     * @throws SecurityException if failed to decrypt data
     */
    public static byte[] decrypt(String algorithm,
                                 AlgorithmParameterSpec spec,
                                 Key key,
                                 byte[] encryptedData) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(spec);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);

        try {
            Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            decryptor.init(Cipher.DECRYPT_MODE, key, spec);
            return decryptor.doFinal(encryptedData);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException
                | IllegalBlockSizeException | InvalidAlgorithmParameterException err) {
            throw new SecurityException("Error decrypt the data with key & spec", err);
        }
    }

    /**
     * Decrypts data bytes asymmetrically with the specified algorithm and spec.
     */
    public static byte[] decryptWithSpec(String algorithm, Key key, byte[] encryptedData)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);
        try {
            Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            IvParameterSpec spec = new IvParameterSpec(new byte[decryptor.getBlockSize()]);
            decryptor.init(Cipher.DECRYPT_MODE, key, spec);
            return decryptor.doFinal(encryptedData);
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException
                | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error decrypt the data with key", err);
        }
    }

    /**
     * Decrypts data bytes asymmetrically with the specified algorithm and {@code Key} instance.
     *
     * @param algorithm     the algorithm
     * @param key           the {@code key} instance
     * @param encryptedData the encrypted data bytes
     * @return the decrypted data bytes
     * @throws SecurityException if failed to decrypt data
     */
    public static byte[] decryptWithNoProvider(String algorithm, Key key, byte[] encryptedData)
            throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);

        try {
            Cipher decryptor = Cipher.getInstance(algorithm);
            decryptor.init(Cipher.DECRYPT_MODE, key);
            return decryptor.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error decrypt the data with key", err);
        }
    }

    /**
     * Block decrypts data bytes asymmetrically with the specified algorithm and {@code Key}
     * instance.
     */
    public static byte[] blockDecrypt(String algorithm,
                                      Key key, byte[] encryptedData) throws SecurityException {
        return blockDecrypt(algorithm, key, encryptedData, DECRYPT_BLOCK_KEY_SIZE);
    }

    /**
     * Block decrypts data bytes asymmetrically with the specified algorithm and {@code Key}
     * instance.
     */
    public static byte[] blockDecrypt(String algorithm, Key key, byte[] encryptedData,
                                      int decryptBlockKeySize) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);
        byte[] result = null;

        try {
            Cipher decryptor = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            decryptor.init(Cipher.DECRYPT_MODE, key);
            for (int i = 0; i < encryptedData.length; i += decryptBlockKeySize) {
                byte[] doFinal = decryptor.doFinal(subArray(encryptedData, i, i + decryptBlockKeySize));
                result = addAll(result, doFinal);
            }
            return result;
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error decrypt the data with key", err);
        }
    }


    /**
     * Block decrypts data bytes asymmetrically with the specified algorithm and {@code Key}
     * instance (without provider).
     */
    public static byte[] blockDecryptWithNoProvider(String algorithm,
                                                    Key key, byte[] encryptedData) throws SecurityException {
        return blockDecryptWithNoProvider(algorithm, key, encryptedData, DECRYPT_BLOCK_KEY_SIZE);
    }

    /**
     * Block decrypts data bytes asymmetrically with the specified algorithm and {@code Key}
     * instance (without provider).
     */
    public static byte[] blockDecryptWithNoProvider(String algorithm, Key key, byte[] encryptedData,
                                                    int decryptBlockKeySize) throws SecurityException {
        Preconditions.checkNotNull(algorithm);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encryptedData);
        byte[] result = null;

        try {
            Cipher decryptor = Cipher.getInstance(algorithm);
            decryptor.init(Cipher.DECRYPT_MODE, key);
            for (int i = 0; i < encryptedData.length; i += decryptBlockKeySize) {
                byte[] doFinal = decryptor.doFinal(subArray(encryptedData, i, i + decryptBlockKeySize));
                result = addAll(result, doFinal);
            }
            return result;
        } catch (NoSuchAlgorithmException | InvalidKeyException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException err) {
            throw new SecurityException("Error decrypt the data with key", err);
        }
    }

    private static byte[] clone(byte[] array) {
        if (array == null) {
            return new byte[0];
        }
        return array.clone();
    }

    private static byte[] subArray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return new byte[0];
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;

        if (newSize <= 0) {
            return new byte[0];
        }

        byte[] subArray = new byte[newSize];

        System.arraycopy(array, startIndexInclusive, subArray, 0, newSize);

        return subArray;
    }

    private static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static KeyStore getKeyStore(String type, String key, String pwd, String provider)
            throws SecurityException {
        try {
            char[] chars = pwd.toCharArray();
            KeyStore keyStore = Strings.isNullOrEmpty(provider)
                    ? KeyStore.getInstance(type) : KeyStore.getInstance(type, provider);
            keyStore.load(new ByteArrayInputStream(Base64.decodeBase64(key)), chars);
            return keyStore;
        } catch (NoSuchProviderException | NoSuchAlgorithmException | KeyStoreException
                | CertificateException | IOException err) {
            throw new SecurityException("Error generating the private key", err);
        }
    }

    /**
     * get certificate.
     */
    public static X509Certificate getCertificate(String type,
                                                 String key,
                                                 String pwd,
                                                 String provider) throws SecurityException {
        try {
            KeyStore keyStore = getKeyStore(type, key, pwd, provider);
            Enumeration<String> keyAliases = keyStore.aliases();
            if (keyAliases.hasMoreElements()) {
                String keyAlias = keyAliases.nextElement();
                return (X509Certificate) keyStore.getCertificate(keyAlias);
            } else {
                throw new SecurityException("Error generating X509Certificate from the private key!");
            }
        } catch (KeyStoreException err) {
            throw new SecurityException("Error generating X509Certificate from the private key", err);
        }
    }

    /**
     * generate certification.
     */
    public static Certificate generateCertification(String issuer,
                                                    BigInteger serial,
                                                    Date notBefore,
                                                    Date notAfter,
                                                    KeyPair keyPair) throws SecurityException {
        try {
            if (Strings.isNullOrEmpty(issuer)) {
                issuer = DEFAULT_ISSUER;
            }
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    new X500Name(issuer), serial, notBefore, notAfter,
                    new X500Name(issuer), keyPair.getPublic());
            ContentSigner signer = new JcaContentSignerBuilder(SHA1_WITH_RSA)
                    .setProvider("BC").build(keyPair.getPrivate());
            X509CertificateHolder holder = builder.build(signer);
            CertificateFactory factory = CertificateFactory.getInstance(X509);
            InputStream inputStream = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
            X509Certificate certification = (X509Certificate) factory.generateCertificate(inputStream);
            inputStream.close();
            return certification;
        } catch (IOException | CertificateException | OperatorCreationException err) {
            throw new SecurityException("Error generate X509Certificate ", err);
        }
    }

    /**
     * 生成RSA的PFX证书和CERT证书[临时目录]. 以BASE64编码文件返回.
     */
    public static RsaCertification generateRsaCertification(String password,
                                                            String filePath,
                                                            String fileName,
                                                            String alias,
                                                            String issuer,
                                                            Integer keySize)
            throws SecurityException {

        try {
            Preconditions.checkNotNull(password);
            Preconditions.checkNotNull(filePath);
            Preconditions.checkNotNull(fileName);

            if (Strings.isNullOrEmpty(alias)) {
                alias = DEFAULT_ALIAS;
            }
            if (keySize == null) {
                keySize = DEFAULT_KEY_SIZE;
            }
            File dir = new File(filePath);
            if (!dir.exists()) {
                FileUtils.forceMkdir(dir);
            }
            //生成RSA秘钥对
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(SecurityUtils.RSA);
            keyPairGen.initialize(keySize);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            BigInteger serial = BigInteger.probablePrime(32, new Random());
            Date notBefore = new Date();
            Date notAfter = org.apache.commons.lang3.time.DateUtils.addYears(notBefore, 10);
            Certificate certificate = SecurityUtils.generateCertification(issuer, serial,
                    notBefore, notAfter, keyPair);

            // 创建KeyStore
            KeyStore store = KeyStore.getInstance(PKCS12);
            store.load(null, null);
            store.setKeyEntry(alias, keyPair.getPrivate(),
                    password.toCharArray(), new Certificate[]{certificate});

            //pfx证书
            String pfxPath = filePath + File.separator + fileName + PFX_EXT;
            try (FileOutputStream pfxOut = new FileOutputStream(pfxPath)) {
                store.store(pfxOut, password.toCharArray());
            }

            //cert证书
            String certPath = filePath + File.separator + fileName + CERT_EXT;
            try (FileOutputStream certOut = new FileOutputStream(certPath)) {
                IOUtils.write(certificate.getEncoded(), certOut);
            }

            byte[] pfxBuffer = readFile(pfxPath);
            byte[] certBuffer = readFile(certPath);

            return RsaCertification.builder()
                    .pfxFileContent(StringUtils.encodeBase64(pfxBuffer))
                    .pfxPassword(password)
                    .certFileContent(StringUtils.encodeBase64(certBuffer))
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException
                | CertificateException err) {
            throw new SecurityException("Error generate rsa certification ", err);
        }
    }

    private static byte[] readFile(String path) throws IOException {
        try (FileInputStream certIn = new FileInputStream(path)) {
            byte[] certBuffer = new byte[certIn.available()];
            IOUtils.read(certIn, certBuffer);
            return certBuffer;
        }
    }

    public static KeyPair generateKeyPair(String algorithm, int keySize,
                                          String provider) throws GeneralSecurityException {
        Preconditions.checkNotNull(algorithm);
        KeyPairGenerator generator = Strings.isNullOrEmpty(provider) ?
                KeyPairGenerator.getInstance(algorithm) :
                KeyPairGenerator.getInstance(algorithm, DEFAULT_PROVIDER);
        generator.initialize(keySize > 0 ? keySize : DEFAULT_KEY_SIZE,
                SecureRandom.getInstance(DEFAULT_SECURE_RANDOM_ALGORITHM));

        return generator.generateKeyPair();
    }

    public static KeyPair generateKeyPair(String algorithm) throws GeneralSecurityException {
        return generateKeyPair(algorithm, DEFAULT_KEY_SIZE, DEFAULT_PROVIDER);
    }





}
