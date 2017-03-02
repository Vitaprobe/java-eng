package eetjava;


import java.net.URL;
import java.security.KeyStore;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {
	
	public static void main(String[] args) {
		try {
			EetRegisterRequest request=EetRegisterRequest.builder()
			   	.dic_popl("CZ00000019") // DIC - tax indentification number of merchant
				.id_provoz("1") // ID of sales place
				.id_pokl("POKLADNA01") // ID of cash register
				.porad_cis("1") // ID of trade or sale
				.dat_trzby(EetRegisterRequest.formatDate(new Date())) // Date of transaction
				.celk_trzba(100.0) // Price in CZK - this will be updated as we progress with the implementation
				.rezim(0) // Mode of sale, 0 is standard
				.pkcs12(EetRegisterRequest.loadStream(Main.class.getResourceAsStream("/eetjava/EET_CA1_Playground-CZ00000019.p12"))) // Certificate used to identify merchant
				.pkcs12password("eet") // Password for the certificate
				.build();

			//for receipt printing in online mode
			String bkp=request.formatBkp();

			//for receipt printing in offline mode
			String pkp=request.formatPkp();
			//the receipt can be now stored for offline processing

			//try send
			String requestBody=request.generateSoapRequest();
			System.out.printf("===== BEGIN EET REQUEST =====\n%s\n===== END EET REQUEST =====\n",requestBody);

			String response=request.sendRequest(requestBody, new URL("https://pg.eet.cz:443/eet/services/EETServiceSOAP/v3"));
			System.out.printf("===== BEGIN EET RESPONSE =====\n%s\n===== END EET RESPONSE =====\n",response);
			Pattern pattern = Pattern.compile("Potvrzeni fik=\"(.*)\" ");
			Matcher matcher = pattern.matcher(response);
			matcher.find();
			String fik = matcher.group(1);
			// Print out important details
			System.out.printf("===== BKP: %s\n\n", bkp);
			System.out.printf("===== PKP: %s\n\n", pkp);
			System.out.printf("===== FIK: %s\n\n", fik);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
