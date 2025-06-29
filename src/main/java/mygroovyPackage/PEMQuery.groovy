/*
java ListCryptoProviders

cd C:\worksplace\mygroovy\src\main\java\mygroovyPackage
groovy PEMQuery.groovy
*/

package mygroovyPackage

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.provider.X509CertificateObject
import org.bouncycastle.openssl.*

@Grab(group='commons-lang', module='commons-lang', version='2.4')
@Grab(group='commons-codec', module='commons-codec', version='1.10')

import java.io.StringWriter

//just copy over jar
//@Grab(group='org.bouncycastle', module='bcprov-jdk16', version='1.45')

import java.text.DateFormat
import java.text.SimpleDateFormat

class PEMQuery {

	public PEMQuery() {
		// TODO Auto-generated constructor stub
	}

	static main(args) {
		def pi = new PEMQuery()
		pi.exec(args)
	}

	public exec(def args) {
		//Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Security.addProvider(new BouncyCastleProvider())
		
		def openssl = true
		def BouncyCastle = true
		
		def getPublicKey = true
		
		def input = './www.google.com.pem'
		def output = './www.google.com.pem.info.txt'
		
		def inputFile = new File(input)
		def outputFile = null
		
		def outputPW = getPrintWriter(output)
		
		//plan openssl cmd offers more info...
		//openssl x509 -text -in www.google.com.pem >> www.google.com.pem.info
		if(openssl) {
			outputPW.println "=== openssl START ==="
			
			// Use OpenSSL
			String opensslCommandString = "openssl x509 -text -in ${inputFile.absolutePath}"
			runOpensslCommand(outputPW, opensslCommandString)
			
			if (getPublicKey) {
				opensslCommandString = "openssl x509 -pubkey -noout -in ${inputFile.absolutePath}"
				String publicKeyFileName = "${input}.openSSL.pub"
				File publicKeyFile = new File(publicKeyFileName)
				PrintWriter publicKeyPW  = getPrintWriter(publicKeyFileName)
				runOpensslCommand(publicKeyPW, opensslCommandString)
				publicKeyPW.flush()
				publicKeyPW.close()
			}
			
			outputPW.println "=== openssl END ==="
		}
		
		//Base64 (Bouncy Castle Library 1.37 API Specification)
		//Encode the byte data to base 64 writing it to the given output stream
		if(BouncyCastle) {
			outputPW.println "=== Base64 BouncyCastle START ==="

			DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			gmtFormat.setTimeZone(gmtTime);

			FileReader fileReader = new FileReader(inputFile)
			PEMReader pemReader = new PEMReader(fileReader)
			Object obj = pemReader.readObject()
			pemReader.close();
			X509CertificateObject x509Cert = (X509CertificateObject) obj

			//snHex
			String snHex = bytesToHex(x509Cert.serialNumber.toByteArray())
			outputPW.println "Serial Number : ${snHex}"
			outputPW.println "Issuer        : " + x509Cert.getIssuerDN()
			outputPW.println "Subject       : " + x509Cert.getSubjectDN()

			//
			String notBefore = gmtFormat.format(x509Cert.notBefore)
			String notAfter  = gmtFormat.format(x509Cert.notAfter)
			outputPW.println "Validity      :"
			outputPW.println "   Not Before : ${notBefore}"
			outputPW.println "   Not After  : ${notAfter}"

			outputPW.println encodeCertificate(x509Cert.encoded)

			if (getPublicKey) {
				PublicKey publicKey = x509Cert.getPublicKey()
				String publicKeyFileName = "${input}.Base64.pub"
				File publicKeyFile = new File(publicKeyFileName)
				PrintWriter publicKeyPW  = getPrintWriter(publicKeyFileName)
				runBase64Command(publicKeyPW,publicKey)
				publicKeyPW.flush()
				publicKeyPW.close()

			}
						
			outputPW.println "=== Base64 BouncyCastle END ==="
		}
		
		outputPW.flush()
		outputPW.close()
	}

	public String runCommand(String command) {
		def out = new StringBuilder()
		def err = new StringBuilder()

		def process = command.execute()
		process.waitForProcessOutput(out, err)
		return out.toString()
	}

	public void runOpensslCommand(def publicKeyPW, def opensslCommandString) {
		publicKeyPW.println runCommand(opensslCommandString)
	}
	
	public void runBase64Command(def publicKeyPW, def publicKey) {
		publicKeyPW.println "Public Key Format   : " + publicKey.format
		publicKeyPW.println "Public Key Algorithm: " + publicKey.algorithm
		publicKeyPW.println "    serialVersionUID: " + publicKey.serialVersionUID
		publicKeyPW.println encodePublicKey(publicKey.encoded)
	}
	
	PrintWriter getPrintWriter(def fileName) {
		def out
		if(fileName) {
			out = new PrintWriter(new File(fileName))
		} else {
			//use STDOUT if fileName is not specified
			out = new PrintWriter(System.out, true)
		}
		return out
	}
	
	String bytesToHex(byte[] bytes) {
		Hex.encodeHexString( bytes )
	}

	String encodePublicKey(byte[] bytes) {
		Base64 encoder = new Base64(64);
		String boundaryBegin = "-----BEGIN PUBLIC KEY-----";
		String boundaryEnd   = "-----END PUBLIC KEY-----";
		return encodeBytes(bytes, boundaryBegin, boundaryEnd);
	}

	String encodeCertificate(byte[] bytes) {
		Base64 encoder = new Base64(64);
		String boundaryBegin = "-----BEGIN CERTIFICATE-----";
		String boundaryEnd   = "-----END CERTIFICATE-----";
		return encodeBytes(bytes, boundaryBegin, boundaryEnd);
	}

	String encodeBytes(byte[] bytes, String boundaryBegin, String boundaryEnd) {
		Base64 encoder = new Base64(64);
		String body    = new String(encoder.encode(bytes));
		StringWriter strOut = new StringWriter()
		strOut.write boundaryBegin + "\n"
		strOut.append body.replace("\r", "")
		strOut.append boundaryEnd

		String result = strOut.toString()
		return result
	}
}

/*

launch dos
PS C:\Users\ht021> openssl --version
OpenSSL 3.5.0 8 Apr 2025 (Library: OpenSSL 3.5.0 8 Apr 2025)

cd C:\worksplace\mygroovy\src\main\java\mygroovyPackage
dir www.google.com (download cert from www.google.com)
openssl x509 -in www.google.com -out www.google.com.pem -outform PEM
openssl x509 -text -in www.google.com.pem >> www.google.com.pem.info
openssl x509 -pubkey -noout -in www.google.com.pem -out www.google.com.pub
////////////////////

this spit out www.google.com.pub
openssl x509 -pubkey -noout -in www.google.com.pem -out www.google.com.pub
1>i eyeball diff 'www.google.com.pub' and 'www.google.com.pem.openSSL.pub' that ran thru openssl encoding. SAME
2>i eyeball diff 'www.google.com.pub' and 'www.google.com.pem.Base64.pub' that ran thru Base64 encoding. SAME
3>unless i am mistaken pub key is PEM which already fully encoded (byte already all convert to  64 different ASCII characters 
so no further is necessary. any additional encoding is redundant...

//////////////////

# Extract key
openssl pkey -in www.google.com.pem -out www.google.com-key.pem

# Extract all the certs
openssl crl2pkcs7 -nocrl -certfile www.google.com.pem |
  openssl pkcs7 -print_certs -out www.google.com-certs.pem

# Extract the textually first cert as DER
openssl x509 -in www.google.com.pem -outform DER -out first-cert.der

*/
